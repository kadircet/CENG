this = 0

#Create the ip table:
#	Ips are stored as a list of python dictionaries for fast lookup.
#	ips[source][destination] = 'ip of destination'
#	first index (source) defines the source and second index (destination) defines the destination.
#	Only the ips of the nodes adjacent to the source node are stored.
#	Queries asking for not adjacent nodes' ips are invalid.

ips = [0]*5
for i in range(5):
    ips[i] = dict()
ips[0][1] = '10.10.1.1'
ips[1][0] = '10.10.1.2'
ips[1][2] = '10.10.2.1'
ips[2][1] = '10.10.2.2'
ips[2][3] = '10.10.3.1'
ips[3][2] = '10.10.3.2'
ips[3][4] = '10.10.4.1'
ips[4][3] = '10.10.4.2'
machines = ['A','B','C','D','E'] #id to name for machines

##Creation of routing table, simply set targets which are
##before the current node to the successor, and after the
##current node to the predecessor of that node
route = []
for i in range(5):
    route.append([])
    for j in range(5):
        route[i].append(0)
        if i<j:
            route[i][j] = i+1
        elif i>j:
            route[i][j] = i-1
        else:
            route[i][j] = i

import socket #for python Socket API
import struct #used in encoding messages
import fcntl, os #used for OS calls
import time #used to generate timestamps

port = 31337 #All communication is performed over one port
socks = []   #socks list is used to keep track of opened sockets, since we can have multiple interfaces
sendsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)  #sendsock is the outgoing socket

#parse parses the message and sends it to the destination.
#   Destination is found by taking the first byte of the package then dividing it by 5 and then taking the modulus 5.
#   Then a lookup is perfomed on the ips to get the ip of the machine for the nexthop
def parse(msg):
    to = (ord(msg[0])/5)%5
    sendsock.sendto(msg, (ips[route[this][to]][this], port))

#createMsg encodes the message:
#	First byte holds the information about the sender(from), destination(to) and acknowledment flag(ack) in radix 5 format.
#	'from' can be in the interval [0,4].
#	'to' can be in the interval [0,4].
#	'ack' can be 0 or 1.
#		e.g. Assume A(0) sends an acknowledment package to E(4)
#				from = 0
#				to = 4
#				ack = 1
#			so we should send (140)_5, or (1*25 + 4*5 + 0*1) = 45 in base 10
#			since this number cannot be greater than 256 we can safely use a byte to encode this information hence the function call to chr()
#	
#	Second byte holds the timestamp, struct.pack packs the time into a byte
#
#	Third byte holds the length of the data in the message
#
#	Rest of the package is the message, message can be up to 256 bytes.
#
def createMsg(frm,to,ack,data):
    msg=chr(frm+to*5+ack*25)+struct.pack("d",time.time())+chr(len(data))+data
    return msg

#Send message to 'to', message is passed as 'data'.
def sendMsg(data,to):
    parse(createMsg(this,to,0,data[:256]))

#Send ACK signal to 'to', message is passed as 'data'.
def sendACK(data,to):
    parse(createMsg(this,to,1,data[:256]))

# Open appropriate listening sockets for the current node
#		for each ip returned from ip table with 'this' node as source (i.e. for each node adjacent to 'this' node)
#		open a UDP socket and bind it to the appropriate ip and port
#		Also add opened socket to socks list to keep track of it.
for ip in ips[this].values():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((ip, port))
    sock.settimeout(3)#if no ACK is received in 3 seconds pkg is assumed to be lost.
    print "Listening on", (ip,port)
    #fcntl.fcntl(sock, fcntl.F_SETFL, os.O_NONBLOCK)
    socks.append(sock)

rtt = [] # list to store round trip times
e2e = [] # list to store end to end delays

# Send 100 packets (ping) to 'E' and wait for answers.
# Timeouts after 3 seconds of idle wait and experiment
# needs to be repeated since network was not stable.
for _ in range(100):
    print "Sending PING to E\r"
    start = time.time()
    sendMsg("PING", 4) #send packet
    msg=socks[0].recv(1024) #receive ACK
    end = time.time() #get the received time
    estart = struct.unpack("d",msg[1:9])[0] #extract timestamp from package
    print "Received PONG from E @"+str((end-estart)*1000)+"ms"
    rtt.append(end-start) #calculate rtt, just for fun
    e2e.append(end-estart)#calculate e2e, diff between the rcv time and timestamp in pkg

print "Average:",sum(e2e)/len(res)*1000,"ms"
