this = 1


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
machines = ['A','B','C','D','E']#id to name for machines

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
            
import socket	#for python Socket API
import fcntl, os #used for OS calls

port = 31337#All communication is performed over one port
socks = []#socks list is used to keep track of opened sockets, since we have multiple interfaces
sendsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)#sendsock is the outgoing socket
#parses the message and forwards it to the next node in the topology,
#	From the first byte of the message we can find out where the message is coming from, where it is going and whether it is an acknowledgemt mesage or not.
#		first byte % 5 -> source
#		(first byte / 5 ) % 5 -> destination
#		(firts byte / 25) % 5 -> acknowledgemt flag
#	
#	Then use this information to pass the data to the next node on the route
def parse(msg):
    frm= ord(msg[0])%5
    to = (ord(msg[0])/5)%5
    ack= ord(msg[0])/25
    print "Routing msg from",machines[frm],"to",
    print machines[to],"via",machines[route[this][to]]
    print "\tACK=",ack,"DATA=",msg[10:]
    sendsock.sendto(msg, (ips[route[this][to]][this], port))

# Open appropriate listening sockets for the current node
#		for each ip returned from ip table with 'this' node as source (i.e. for each node adjacent to 'this' node)
#		open a UDP socket and bind it to the appropriate ip and port
#		Also add opened socket to socks list to keep track of it.	
for ip in ips[this].values():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((ip, port))
    print "Listening on",(ip,port)
    fcntl.fcntl(sock, fcntl.F_SETFL, os.O_NONBLOCK) #make listening sockets nonblocking
    socks.append(sock)

# Listen for messages indefinetely, when a message comes forward it to the next node.
while True:
    for s in socks:
        try:
            msg = s.recv(1024) #nonblocking rcv, since we need to listen on
            					#multiple interfaces either we needed threading
            					#or a nonblocking mechanism, choose the nonblocking.
            parse(msg) #route the message according to the header
        except:
            continue
