import sys
import socket
import fcntl, os
import time
import struct
from threading import *

port = 1337
sendsock = []
ips = [x[:x.find('/')] for x in os.popen("ip a | grep 'inet ' | cut -d ' ' -f6").read().split('\n')]
for ip in ips:
    if ip[:2]=='10':
        sock=socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.bind((ip,port+1))
        sendsock.append(sock)
base = 0
wndsize = 128
end = 0
lastsent = 0
MAXPAY = 1000-1-4-2-2
TIMEOUT = 3.0
SYN = "0"
ACK = "1"
INF = "2"
sendbuffer = []
expire = [0]*wndsize
hasacked = [0]*wndsize
running = True
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
ip = "10.10.1.1"
sock.bind((ip, port))
sndrmtx = Lock()
expmtx = Lock()

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
    sendsock[lastsent%len(sendsock)].sendto(msg, (dest, port))

def rdt_send(dest, data):
    global end
    frags = []
    while len(data)>MAXPAY:
        frags.append((dest,createMsg(SYN, end, data[:MAXPAY])))
        data = data[MAXPAY:]
        end+=1
    if len(data)>0:
        frags.append((dest,createMsg(SYN, end, data)))
        end+=1
    sndrmtx.acquire()
    sendbuffer.extend(frags)
    sndrmtx.release()

def sender():
    global lastsent
    while running:
        while lastsent<base+wndsize and lastsent<end:
            udt_send(sendbuffer[lastsent])
            expmtx.acquire()
            expire[lastsent-base] = time.time()+TIMEOUT
            expmtx.release()
            lastsent+=1

def expirer():
    while running:
        tick = time.time()
        for i in range(len(expire)):
            if expire[i]>0 and tick>expire[i]:
                udt_send(sendbuffer[i+base])
                expmtx.acquire()
                expire[i] = time.time()+TIMEOUT
                expmtx.release()
        time.sleep(0.01)

def receiver():
    while running:
        msg = sock.recv(9)
        ckh = struct.unpack("H", msg[:2])
        leng = struct.unpack("H", msg[7:])
        msg = msg+sock.recv(leng)
        if chksum(msg[2:])!=ckh:
            continue

        seq = struct.unpack("I", msg[3:7])
        data = msg[9:]
        if(msg[3]==ACK):
            if seq==base:
                c = 0
                hasacked[0] = 1
                while hasacked[c]:
                    c+=1
                base+=c
                hasacked= hasacked[c:] + [0]*c
                expmtx.acquire()
                expire = expire[c:] + [0]*c
                expmtx.release()
            else:
                hasacked[seq-base] = 1
                expmtx.acquire()
                expire[seq-base] = 0
                expmtx.release()
            if lastsent==end and base==end and base>0:
                send(FIN, dest)
        elif msg[3]==FIN:
            send(FIN, dest)
            running=False

if len(sys.argv)!=2:
    print "Usage %s DEST_IP" % sys.argv[0]
    sys.exit(1)

dest = sys.argv[1]
Thread(target=receiver).start()
Thread(target=expirer).start()
Thread(target=sender).start()

rdt_send(dest, "sex"*1000)
