targ = 1000000
best = 1000000
itr = 1
pres = 256
while itr<=20:
    ofs=256
    while ofs>=0:
        if abs(targ-pres*ofs*itr)<best:
            best=abs(targ-pres*ofs*itr)
            print best,ofs,itr
        ofs-=1
    itr+=1
