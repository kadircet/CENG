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
	
def CM(L, ql, qr):
	if ql==qr:
		return list(getCenter(L[ql])) + [getArea(L[ql])]
	m = (ql+qr)/2
	l = CM(L, ql, m)
	r = CM(L, m+1, qr)
	d = dist((l[0], l[1]), (r[0], r[1]))
	S = abs(l[2])
	G = abs(r[2])
	if S>G:
		G=S
		S=abs(r[2])
	if r[2]*l[2]>=0:
		x = S/(G+S)
		r[0] = r[0] + (l[0]-r[0])*(x if abs(abs(r[2])-G)<=1e-6 else (1-x))
		r[1] = r[1] + (l[1]-r[1])*(x if abs(abs(r[2])-G)<=1e-6 else (1-x))
	else:
		x = S/(G-S)
		r[0] = r[0] + (l[0]-r[0])*(1+x if abs(abs(r[2])-S)<=1e-6 else -x)
		r[1] = r[1] + (l[1]-r[1])*(1+x if abs(abs(r[2])-S)<=1e-6 else -x)
	r[2] = r[2] + l[2]
	
	return r

def cm(shapes):
	return CM(shapes, 0, len(shapes)-1)

#L=input()
#print cm(L)
