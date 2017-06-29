import random

PCOMP=0.7
PADD =0.8
PDEF = 0.5
totalPrice=0
hasgen={}
maxDepth=10
maxBranch=5

def randName():
	l=random.randint(5, 20)
	res=""
	for i in range(l):
		res += chr(ord('A')+int(random.random()*(ord('Z')-ord('A'))))
	if res in hasgen:
		return randName()
	hasgen[res]=True
	return res

parts = {}
isDefined = {}

class Part:
	def __init__(self, depth):
		self.depth=depth
		self.name = randName()
		self.isComp=depth>0
		self.list=[]
		self.price=0
		if depth==0:
			self.price=random.random()*10
		else:
			for d in range(depth):
				for part in parts[d]:
					if random.random()>(1-PADD):
						self.list.append([random.randint(1,100), part])
						self.price += self.list[-1][0]*self.list[-1][1].price
			if len(self.list)==0:
				self.list.append([random.randint(1,100), random.choice(parts[random.randint(0, depth-1)])])
				self.price += self.list[-1][0]*self.list[-1][1].price
			random.shuffle(self.list)
	
	def __repr__(self, d=0):
		res = self.name
		if self.name in isDefined:
			return res
		isDefined[self.name]=True
		items=[]
		if self.isComp:
			res += "(\n"
			for item in self.list:
				items.append("\t"*(d+1)+((str(item[0])+"*") if item[0]!=1 or random.random()<0.4 else "")+item[1].__repr__(d+1))
			random.shuffle(items)
			res += ",\n".join(items)+"\n"+"\t"*d+")"
		else:
			res += "["+str(self.price)+"]"
		return res
			
		
dep=random.randint(1,maxDepth)
for d in range(dep):
	parts[d]=[]
	n=random.randint(1, maxBranch)
	for i in range(n):
		parts[d].append(Part(d))
		
p=Part(dep)
print "['%s', %lf]" %(p, p.price)
