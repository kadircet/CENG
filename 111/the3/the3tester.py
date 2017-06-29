import the3v1
import the3v2
from random import random as rand
nOP = ord('z')-ord('a')+1
ops = ['+', '-', '*', '/', '^']

def solve(L):
	if L==0 or L==2:
		return ""
	if L==1:
		return chr(ord('a')+int(rand()*nOP))
	
	res = ""
	c=0
	while c<L:
		todo=int(rand()*6)
		q = solve(L-c-2)
		if q=="":
			break
		c+=len(q)+2
		h = chr(ord('a')+int(rand()*nOP))
		if todo==0:
			res += '( '+q+' )'
		else:
			res += h+" "+ops[todo-1]+" "+q
	return res
	
i=0
while i<1000:
	L = int(rand()*1000+3)
	q=solve(L)
	if q!="":
		i+=1
		#print i
		
		v1 = the3v1.ToZoitankian(q)
		v2 = the3v2.ToZoitankian(q)
		p = the3v1.toPostfix(q, 0)
		p1 =the3v1.toPostfix(v1, 1)
		p2 =the3v1.toPostfix(v2, 1)
		if list(the3v1.swap(p))!=p1 or p1!=p2:
			print "ERR:",i,"!"
		v1 = the3v1.ToEarthian(q)
		v2 = the3v2.ToEarthian(q)
		p = the3v1.toPostfix(q, 1)
		p1 =the3v1.toPostfix(v1, 0)
		p2 =the3v1.toPostfix(v2, 0)
		if list(the3v1.swap(p))!=p1 or p1!=p2:
			print "ERR:",i,"!"
		#print L
