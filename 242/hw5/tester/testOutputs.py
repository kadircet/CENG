score=0

def getCases(foldername,case):
	f=open(foldername+str(case)+'.out','r')
	a=f.readlines()
	x=0
	full=[]
	while not(a[x].startswith('Case')):
		x+=1
	while x < len(a):
		current=[]
		if a[x].startswith('Case'):
			x+=1
			while x < len(a) and (not(a[x].startswith('Case'))):
				current.append(a[x])
				x+=1
			full.append(current)
	f.close()
	return full

def generateDiff(case,true,my):
	global score
	d=open('yourOutputs/'+str(case)+'.diff','w')
	inp=open('testInputs/'+str(case)+'.in','r').readlines()
	for i in range(1000):
		try:
			if(true[i]!=my[i]):
				d.write('Case '+str(i)+': \n'+str(inp[i])+'\n')
				d.write('True output:\n')
				for x in true[i]:
					d.write(str(x))
				d.write('\nYour output:\n')
				for y in my[i]:
					d.write(str(y))
				d.write('\n')
			else:
				score+=0.001
		except:
			break
	d.close()

def trywithoutFalse(true,my):
	print true

for num in range(100):
	true=getCases('trueOutputs/',num)
	for x in true:
		x.sort()
		
	my=getCases('yourOutputs/',num)
	for y in my:
		y.sort()

	if my==true:
		print 'Success @'+str(num)
		score+=1
		
	else:
		#trywithoutFalse(true,my)
		print 'Fail @'+str(num)
		generateDiff(num,true,my)
                break

print '\nScore='+str(score)+'/100'
		


		
