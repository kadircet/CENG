#!/usr/bin/python

CIRCLE = 4
TRIANGLE = 7
RECTANGLE = 9
pi = 3.1415926535897932

def dist(p1, p2):
	return ((p1[1]-p2[1])**2.0 + (p1[0]-p2[0])**2.0)**.5

def getCenter(obj):
	if len(obj) == CIRCLE:
		return (obj[1], obj[2])
	elif len(obj) == TRIANGLE:
		return ((obj[1]+obj[3]+obj[5])/3.0, (obj[2]+obj[4]+obj[6])/3.0)
	return ((obj[1]+obj[3]+obj[5]+obj[7])/4.0, (obj[2]+obj[4]+obj[6]+obj[8])/4.0)
	
def getArea(obj):
	if len(obj) == CIRCLE:
		return obj[0]*pi*obj[3]**2
	A,B,C = (obj[1], obj[2]), (obj[3], obj[4]), (obj[5], obj[6])
	if len(obj) == TRIANGLE:
		return obj[0]*abs(A[0]*(B[1]-C[1])+B[0]*(C[1]-A[1])+C[0]*(A[1]-B[1]))/2.0
	return obj[0]*abs(A[0]*(B[1]-C[1])+B[0]*(C[1]-A[1])+C[0]*(A[1]-B[1]))
	
def cm(shapes):
	C = getCenter(shapes[0])
	A = getArea(shapes[0])
	if len(shapes)==1:
		return list(C) + [A]
	
	res = cm(shapes[1:])
	d = dist(C, (res[0], res[1]))
	S = abs(res[2])
	G = abs(A)
	if S>G:
		G=S
		S=abs(A)
	if A*res[2]>=0:
		x = S/(G+S)
		res[0] = res[0] + (C[0]-res[0])*(x if abs(abs(res[2])-G)<=1e-6 else (1-x))
		res[1] = res[1] + (C[1]-res[1])*(x if abs(abs(res[2])-G)<=1e-6 else (1-x))
	else:
		x = S/(G-S)
		res[0] = res[0] + (C[0]-res[0])*(1+x if abs(abs(res[2])-S)<=1e-6 else -x)
		res[1] = res[1] + (C[1]-res[1])*(1+x if abs(abs(res[2])-S)<=1e-6 else -x)
	res[2] = res[2] + A
	
	return res

#L=input()
#print cm(L)
