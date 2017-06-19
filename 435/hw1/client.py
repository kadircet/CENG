this = 0
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
machines = ['A','B','C','D','E']

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

import socket
import struct
import fcntl, os
import time

port = 31337
socks = []
sendsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

def parse(msg):
    to = (ord(msg[0])/5)%5
    sendsock.sendto(msg, (ips[route[this][to]][this], port))

def createMsg(frm,to,ack,data):
    msg=chr(frm+to*5+ack*25)+struct.pack("d",time.time())+chr(len(data))+data
    return msg

def sendMsg(data,to):
    parse(createMsg(this,to,0,data[:256]))

def sendACK(data,to):
    parse(createMsg(this,to,1,data[:256]))

for ip in ips[this].values():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((ip, port))
    sock.settimeout(3)
    print "Listening on", (ip,port)
    #fcntl.fcntl(sock, fcntl.F_SETFL, os.O_NONBLOCK)
    socks.append(sock)

rtt = []
e2e = []
for _ in range(100):
    print "Sending PING to E\r"
    start = time.time()
    sendMsg("PING", 4)
    msg=socks[0].recv(1024)
    end = time.time()
    estart = struct.unpack("d",msg[1:9])[0]
    print "Received PONG from E @"+str((end-estart)*1000)+"ms"
    rtt.append(end-start)
    e2e.append(end-estart)

print "Average:",sum(e2e)/len(res)*1000,"ms"
