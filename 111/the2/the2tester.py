#!/usr/bin/python
from random import random, shuffle
from math import sin, cos
pi = 3.14159265358979323846264338327950288419716

CIRC= 2
TRI = 3
RECT= 4

def dist(p1, p2):
	return ((p1[1]-p2[1])**2.0 + (p1[0]-p2[0])**2.0)**.5

def sign(p1, p2, p3):
	s = ((p1[0]-p3[0]) * (p2[1]-p3[1]) - (p2[0]-p3[0])*(p1[1]-p3[1]))
	if s==0:
		return 0
	return -1 if s<0 else 1
	
def inT(T, p):
	s1 = sign(p, T[0], T[1])
	s2 = sign(p, T[1], T[2])
	s3 = sign(p, T[2], T[0])
	return s1==s2 and s2==s3
	
def inR(T, p):
	T.sort()
	s1 = sign(p, T[0], T[1])
	s2 = sign(p, T[1], T[3])
	s3 = sign(p, T[3], T[2])
	s4 = sign(p, T[2], T[0])
	return s1==s2 and s2==s3 and s3==s4

def inC(T, p):
	return dist(T[0], p) < T[1]
	
def inShape(T, p):
	if len(T) == CIRC:
		return inC(T, p)
	if len(T) == TRI:
		return inT(T, p)
	return inR(T, p)

def randomC(T):
	alpha = random()*2*pi
	dis = random()*T[1]
	return (T[0][0]+dis*cos(alpha), T[0][1]+dis*sin(alpha))
	
def randomT(T, d=1):
	a,b = random()/d, random()/d
	a,b = T[0][0]+a*(T[1][0]-T[0][0])+b*(T[2][0]-T[0][0]), T[0][1]+a*(T[1][1]-T[0][1])+b*(T[2][1]-T[0][1])
	if not inT(T, (a,b)):
		return randomT(T, d+1)
	return (a,b)

def randomR(T):
	T.sort()
	a,b = random(), random()
	return (T[0][0]+a*(T[1][0]-T[0][0])+b*(T[2][0]-T[0][0]), T[0][1]+a*(T[1][1]-T[0][1])+b*(T[2][1]-T[0][1]))
	
def randomP(T):
	if len(T) == CIRC:
		return randomC(T)
	if len(T) == TRI:
		return randomT(T)
	return randomR(T)
	
def between(p, q, r):
	return q[0]<=max(p[0], r[0]) and q[0]>=min(p[0], r[0]) and q[1]<=max(p[1], r[1]) and q[1]>=min(p[1], r[1])

def intersect(S, T):
	if len(T) == CIRC and len(S) == CIRC:
		return dist(T[0], S[0]) > T[1] + S[1]
	
	if len(S) == CIRC:
		S,T = T,S
		
	if len(T) == CIRC:
		d = getMinDistEdge(S, T[0])
		if d<T[1] or inShape(S,T[0]):
			return True
		return False
		
	for t in T:
		if inShape(S, t):
			return True
			
	Se = getEdges(S)
	Te = getEdges(T)
	
	for s in Se:
		for t in Te:
			o1 = sign(s[0], s[1], t[0])
			o2 = sign(s[0], s[1], t[1])
			o3 = sign(t[0], t[1], s[0])
			o4 = sign(t[0], t[1], s[1])
			if o1!=o2 and o3!=o4:
				return True
			if o1==0 and between(s[0], t[0], s[1]):
				return True
			if o2==0 and between(s[0], t[1], s[1]):
				return True
			if o3==0 and between(t[0], s[0], t[1]):
				return True
			if o4==0 and between(t[0], s[1], t[1]):
				return True
				
	return False
	
def getEdges(T):
	T.sort()
	if len(T) == TRI:
		E = [(T[i], T[(i+1)%3]) for i in range(3)]
	else:
		E = [(T[i], T[(i+1)%3]) for i in range(4)]
	return E

def intersects(L, T):
	for l in L:
		if intersect(l, T):
			return True
	return False
	
def pointToEdges(T, c):
	E = getEdges(T)
	D = [abs((e[1][0]-e[0][0])*(e[0][1]-c[1])-(e[0][0]-c[0])*(e[1][1]-e[0][1]))/dist(e[1], e[0]) for e in E]
	return min(D)
	
def getMinDistEdge(T, c):
	if len(T) == CIRC:
		return T[1] - dist(T[0], c)
	return pointToEdges(T, c)

MAXN = 100
def generate(curd, maxd, parent):
	L = []
	if curd==maxd:
		return L
	N = int(random()*MAXN)+1
	for i in range(N):
		s = int(random()*3)
		if s==0: #circle
			c = randomP(parent)
			r = random()*getMinDistEdge(parent, c)/2+1
			if not intersects(L, [c,r]):
				#print "Adding", [c,r]
				L.append([c,r])
		elif s==1: #triangle
			p = []
			while len(p)<3:
				c = randomP(parent)
				if c not in p:
					p.append(c)
			if not intersects(L, p):
				#print "Add:", p
				L.append(p)
		else: #rect
			i=10
			poss = []
			while i>0:
				i-=1
				alpha = random()*2*pi
				p = [randomP(parent)]
				q=p
				while len(p)<2:
					c = randomP(parent)
					if c not in p and p[0][0]!=c[0] and p[0][1]!=c[1]:
						p.append(c)
				p.append((p[0][0], p[1][1]))
				p.append((p[1][0], p[0][1]))
				if inShape(parent, p[2]) and inShape(parent, p[3]):
					poss = p
				
				p = [(x[0]-p[0][0], x[1]-p[0][1]) for x in p]
				p = [(x[0]*cos(alpha)-x[1]*sin(alpha), x[0]*sin(alpha)+x[1]*cos(alpha)) for x in p]
				p = [(x[0]+q[0][0], x[1]+q[0][1]) for x in p]
				
				flag = True
				for t in p:
					if not inShape(parent, t):
						flag = False
						break
				if flag:
					poss = p
					break
			p = poss
			if len(p)>0 and not intersects(L, p):
				#print "Add:", p
				L.append(p)
	
	subs = []
	for i in range(len(L)):
		subs += generate(curd+1, maxd, L[i])
	
	for i in range(len(L)):
		if len(L[i])>CIRC:
			shuffle(L[i])
	
	for i in range(len(L)):
		L[i] = [1 if curd%2==0 else -1] + L[i]
	return L+subs

root = [(-1e3, -1e3), (1e3, -1e3), (1e3, 1e3), (-1e3, 1e3)]
#root = [(0, 0), (0, 900), (1600, 0), (1600, 900)]
L=generate(0, 5, root)
r=L
L = []
for s in r:
	q = []
	for t in s:
		if isinstance(t, (list,tuple)):
			for x in t:
				q.append(x)
		else:
			q.append(t)
	L.append(q)
print L
