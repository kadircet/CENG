def listEq(l1, l2):
    l1.sort()
    l2.sort()
    return l1==l2

def convPost(posts):
    posts=posts[1:-1].split(',')
    res=[]
    for i in range(len(posts)):
        if posts[i]=='':
            continue
        posts[i]=posts[i].strip().split(' ')[1:]
        posts[i]=(posts[i][0][1:-1], posts[i][1][1:-1])
        res.append(posts[i])
    res.sort()
    return res

def groupConv(groups):
    groups=groups[1:-1].split('Group ')
    res=[]
    for i in range(len(groups)):
        if groups[i]=="":
            continue
        groups[i]=groups[i].strip().split(' [')
        groups[i][2].strip()
        if groups[i][2][-1]==',':
            groups[i][2]=groups[i][2][:-1]
        groups[i]=(groups[i][0][1:-1], eval('['+groups[i][1]), convPost('['+groups[i][2]))
        groups[i][1].sort()
        groups[i][2].sort()
        res.append(groups[i])
    res.sort()
    return res

def userConv(users):
    users = users[1:-1].split('User ')
    res=[]
    for i in range(len(users)):
        if users[i]=="":
            continue
        users[i]=users[i].strip()
        if users[i][-1]==',':
            users[i]=users[i][:-1]
        users[i]=users[i].split(' ', 2)
        rest=users[i][2].strip().split(' [')
        rest[1]='['+rest[1]
        rest[0]=eval(rest[0])
        rest[0].sort()
        rest[1]=convPost(rest[1])
        users[i]=(users[i][0][1:-1], users[i][1][1:-1], rest[0], rest[1])
        res.append(users[i])
    res.sort()
    return res

def userEq(u1, u2):
    return userConv(u1)==userConv(u2)

def groupEq(g1, g2):
    return groupConv(g1) == groupConv(g2)
    
import subprocess,os

hsfile="import HW1\ndb=%s\nmain = do\n"
tf=open('test.hs', 'w')
f=open('test.in').read().split('\n')[:-1]
db=f[0]

tf.write(hsfile%db)
score=0
answ=[]
cmds=[]
for i in range(1, len(f), 2):
    cmd=f[i]
    answ.append(f[i+1])
    cmds.append(cmd)
    tf.write(" print("+cmd+")\n")

tf.close()
print "Compiling"
proc=subprocess.Popen(['ghc', 'test.hs'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
proc.wait()
print "Testing"
proc=subprocess.Popen(['./test'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
outs=proc.stdout.read().split('\n')
for i in range(len(cmds)):
    cmd = cmds[i]
    res = answ[i]
    out = outs[i]
    res2=0
    if cmd.startswith('getFriend'):
        out=eval(out)
        res=eval(res)
        res2=listEq(out, res)
    elif cmd.startswith('getPosts'):
        res2=listEq(convPost(out),convPost(res))
    elif cmd.startswith('listGroups'):
        res2=groupEq(out, res)
    else:
    	res2=userEq(out, res)

    if res2==0:
        print i,cmd,out,res
        break
    else:
        score+=1

print str(score)+"/1000"

