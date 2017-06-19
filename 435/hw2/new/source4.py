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
wndsize = 1
thres = 1024
end = 0
lastsent = 0
MAXPAY = 1000-1-4-2-2
TIMEOUT = 3.0
SYN = "0"
ACK = "1"
FIN = "2"
sendbuffer = []
expire = [0]*(2*thres)
hasacked = [0]*(2*thres)
running = True
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
ip = "10.10.1.1"
sock.bind((ip, port))
sndrmtx = Lock()
expmtx = Lock()
nonbase = 0
start = 0

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
    #print "SENT:",((dest,port),'')#msg)
    #print "SENT:", struct.unpack("I",msg[3:7])[0], base
    sendsock[lastsent%len(sendsock)].sendto(msg, (dest, port))

def rdt_send(dest, data):
    global end,start
    c=0
    frags = []
    while len(data)>MAXPAY:
        frags.append((dest,createMsg(SYN, end+c, data[:MAXPAY])))
        data = data[MAXPAY:]
        c+=1
    if len(data)>0:
        frags.append((dest,createMsg(SYN, end+c, data)))
        c+=1
    sndrmtx.acquire()
    sendbuffer.extend(frags)
    end+=c
    start=time.time()
    sndrmtx.release()

def sender():
    global lastsent,expire
    while running:
        while lastsent<base+wndsize and lastsent<end:
            udt_send(sendbuffer[lastsent])
            expmtx.acquire()
            expire[lastsent-base] = time.time()+TIMEOUT
            expmtx.release()
            lastsent+=1

def expirer():
    global expire,wndsize
    while running:
        tick = time.time()
        halved=False
        for i in range(len(expire)):
            if expire[i]>0 and tick>expire[i]:
                wndsize/=2
                thres=wndsize
                udt_send(sendbuffer[i+base])
                expmtx.acquire()
                expire[i] = time.time()+TIMEOUT
                expmtx.release()
        time.sleep(0.01)

def receiver():
    global expire,base,hasacked,end,running,nonbase,wndsize,thres
    while running:
        msg = sock.recv(1024)
        ckh = struct.unpack("H", msg[:2])[0]
        leng = struct.unpack("H", msg[7:9])[0]
        #print "RCVD", (leng, ckh, msg)
        #msg = msg+sock.recv(leng)
        if chksum(msg[2:])!=ckh:
            print "CHKFAIL"
            continue

        seq = struct.unpack("I", msg[3:7])[0]
        data = msg[9:]
        #print (seq,data)
        if(msg[2]==ACK):
            #print "GOT ACK FOR", seq, base, wndsize, lastsent*1000/(time.time()-start)/1024
            if wndsize<thres:
                wndsize+=1
            else:
                wndsize+=1.0/wndsize
            if seq==base:
                c = 0
                hasacked[0] = 1
                while c<lastsent-base and hasacked[c]:
                    c+=1
                hasacked= hasacked[c:] + [0]*c
                expmtx.acquire()
                expire = expire[c:] + [0]*c
                expmtx.release()
                base+=c
                nonbase=0
            else:
                nonbase+=1
                expmtx.acquire()
                if nonbase==3:
                    thres=wndsize
                    wndsize/=2
                    expire[0]=1
                    nonbase=0
                hasacked[seq-base] = 1
                expire[seq-base] = 0
                expmtx.release()
            if lastsent==end and base==end and base>0:
                sndrmtx.acquire()
                sendbuffer.append((dest,createMsg(FIN, end, "")))
                end+=1
                sndrmtx.release()
        else:
            sndrmtx.acquire()
            sendbuffer.append((dest,createMsg(FIN, end, "")))
            end+=1
            sndrmtx.release()
            running=False

if len(sys.argv)!=2:
    print "Usage %s DEST_IP" % sys.argv[0]
    sys.exit(1)

dest = sys.argv[1]
Thread(target=receiver).start()
Thread(target=expirer).start()
Thread(target=sender).start()

data = open('test.in').read()
rdt_send(dest, data)
while running:
    pass
elap = time.time()-start
print elap*1000, "ms"
print len(data)/elap/1024, "KB/s"
