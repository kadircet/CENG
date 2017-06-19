this = 1
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
import fcntl, os

port = 31337
socks = []
sendsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

def parse(msg):
    frm= ord(msg[0])%5
    to = (ord(msg[0])/5)%5
    ack= ord(msg[0])/25
    print "Routing msg from",machines[frm],"to",
    print machines[to],"via",machines[route[this][to]]
    print "\tACK=",ack,"DATA=",msg[10:]
    sendsock.sendto(msg, (ips[route[this][to]][this], port))

for ip in ips[this].values():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((ip, port))
    print "Listening on",(ip,port)
    fcntl.fcntl(sock, fcntl.F_SETFL, os.O_NONBLOCK)
    socks.append(sock)

while True:
    for s in socks:
        try:
            msg = s.recv(1024)
            parse(msg)
        except:
            continue
