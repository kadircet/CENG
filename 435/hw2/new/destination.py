import sys
import socket
import fcntl, os
import time
import struct
from threading import *

port = 1337
sendsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

base = 0
wndsize = 128
end = 0
lastsent = 0
MAXPAY = 1000-1-4-2-2
TIMEOUT = 3.0
SYN = "0"
ACK = "1"
FIN = "2"
rcvbuffer = [0]*wndsize
running = True
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
ip = "10.10.2.2"
sock.bind((ip, port))
sndrmtx = Lock()
expmtx = Lock()
res=""

def chksum(data):
    res = 0
    for x in data:
        res+=ord(x)
    res = res & 0xFFFF
    return res

def createMsg(flag, seq, data):
    msg = flag + struct.pack("I", seq) + struct.pack("H", len(data)) + data
    msg = struct.pack("H", chksum(msg)) + msg
    return msg

def udt_send((dest, msg)):
    print "SENT:",((dest,port),msg)
    sendsock.sendto(msg, (dest, port))

def send(flag, seq, dst):
    udt_send((dst, createMsg(flag, seq, "")))

def rdt_recv():
    while running:
        pass
    return res

def receiver():
    global res
    while running:
        msg = sock.recv(9)
        ckh = struct.unpack("H", msg[:2])
        leng = struct.unpack("H", msg[7:])
        msg = msg+sock.recv(leng)
        if chksum(msg[2:])!=ckh:
            continue

        seq = struct.unpack("I", msg[3:7])
        data = msg[9:]
        if(msg[3]==SYN):
            rcvbuffer[seq-base]=data
            send(ACK, seq, dest)
            if seq==base:
                c = 0
                hasacked[0] = 1
                while hasacked[c]:
                    res+=rcvbuffer[c]
                    c+=1
                base+=c
                rcvbuffer=rcvbuffer[c:] + [0]*c
                hasacked= hasacked[c:] + [0]*c
        else:
            send(FIN, seq, dest)
            running=False

if len(sys.argv)!=2:
    print "Usage %s DEST_IP" % sys.argv[0]
    sys.exit(1)

dest = sys.argv[1]
Thread(target=receiver).start()
Thread(target=expirer).start()
Thread(target=sender).start()

print rdt_recv()
