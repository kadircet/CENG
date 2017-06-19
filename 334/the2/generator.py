import random as rand
import sys
MAXDOCK=100
MAXSHIP=500
MAXCARG=10000
MAXTIME=100
MSHPCAP=100
MAXRLEN=100
if len(sys.argv)>1:
    MAXDOCK = sys.argv[1]
if len(sys.argv)>2:
    MAXSHIP = sys.argv[2]
if len(sys.argv)>3:
    MAXCARG = sys.argv[3]

Nd = rand.randint(2,MAXDOCK)
Ns = rand.randint(1,MAXSHIP)
Nc = rand.randint(1,MAXCARG)
dockCap = []
count = []
print Nd,Ns,Nc
for i in range(Nd):
    dockCap.append(rand.randint(1,2*Ns/3))
    count.append(0)
    print dockCap[-1],
print

for i in range(Ns):
    if sum(dockCap)==0:
        print "GENERATION FAILED SORRY :-("
        sys.exit(1)
    L = rand.randint(1,MAXRLEN)
    route = []
    route.append(rand.randint(0,Nd-1))
    while dockCap[route[-1]]<=1:
        route[-1] = (route[-1]+1)%Nd
    for i in range(L-1):
        n = rand.randint(0,Nd-1)
        while n==route[-1] or dockCap[n]<=1:
            n = (n+1)%Nd
        route.append(n)
    dockCap[route[-1]]-=1
    print rand.randint(1,MAXTIME), rand.randint(1,MSHPCAP), rand.randint(1,MAXTIME), L, " ".join(map(str,route))

for i in range(Nc):
    src = rand.randint(0,Nd-1)
    dst = rand.randint(0,Nd-1)
    if dst==src:
        dst = (dst+1)%Nd
    print src, dst
