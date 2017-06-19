import sys
import socket
import fcntl, os
import time
import struct
from threading import *

port = 1337
sendsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

base = 0
#wndsize = 256
end = 0
lastsent = 0
MAXPAY = 1000-1-4-2-2
TIMEOUT = 3.0
SYN = "0"
ACK = "1"
FIN = "2"
rcvbuffer = [0]*1024
hasacked = [0]*1024
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
    #print "SENT:",((dest,port),msg)
    sendsock.sendto(msg, (dest, port))

def send(flag, seq, dst):
    udt_send((dst, createMsg(flag, seq, "")))

def rdt_recv():
    while running:
        pass
    return res

def receiver():
    global res, running, rcvbuffer, base, hasacked
    while running:
        msg = sock.recv(1024)
        #print (msg,)
        ckh = int(struct.unpack("H", msg[:2])[0])
        leng = int(struct.unpack("H", msg[7:9])[0])
        #msg = msg+sock.recv(leng)
        #print (leng, msg[9:])
        if chksum(msg[2:])!=ckh:
            print "CHK FAIL!:", (ckh, chksum(msg[2:]))
            continue

        seq = int(struct.unpack("I", msg[3:7])[0])
        data = msg[9:]
        if(msg[2]==SYN):
            print "Got seq:",seq,base,seq-base
            if seq<base:
                continue
            rcvbuffer[seq-base]=data
            hasacked[seq-base]=1
            if seq==base:
                c = 0
                hasacked[0] = 1
                while c<len(hasacked) and hasacked[c]:
                    res+=rcvbuffer[c]
                    c+=1
                base+=c
                rcvbuffer=rcvbuffer[c:] + [0]*c
                hasacked= hasacked[c:] + [0]*c
            send(ACK, seq, dest)
            print "SENT:", seq
        else:
            send(FIN, seq, dest)
            running=False

if len(sys.argv)!=2:
    print "Usage %s DEST_IP" % sys.argv[0]
    sys.exit(1)

dest = sys.argv[1]
Thread(target=receiver).start()
open('test.out','w').write(rdt_recv())
