import subprocess
outs = []
proc = []
f = []
for i in range(100):
	inp=open('cases/'+str(i)+'.case', 'r').read().split("'")
	out = float(inp[2][1:].strip()[:-1])
	inp = inp[1]
	outs.append(out)
	open('cases/'+str(i)+'.in', 'w').write(inp)
	f.append(open('cases/'+str(i)+'.in', 'r'))
	proc.append(subprocess.Popen(['./the3'], stdout=subprocess.PIPE, stdin=f[i]))

point = 0
for i in range(100):
	out, err = proc[i].communicate()
	if abs(outs[i]-float(out))/outs[i]<1e-6:
		point+=1
	else:
		print "Difference @ case %d, True:%lf - Yours:%lf" % (i, outs[i], float(out))
	
print "You got: %d/100" % point
