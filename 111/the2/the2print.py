#!/usr/bin/python
from pygame import *

L=input()
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
	
wh = (255, 255, 255)
bl = (0,0,0)

def isCircle(s):
	return len(s) == 4
def isTriangle(s):
	return len(s) == 7

init()
w = display.set_mode((1600, 900))
for shape in L:
	for i in range(1, len(shape)):
		shape[i] = int(shape[i])
	if isCircle(shape):
		draw.circle(w, wh if shape[0]==1 else bl, (shape[1], shape[2]), shape[3])
	elif isTriangle(shape):
		draw.polygon(w, wh if shape[0]==1 else bl, [(shape[i], shape[i+1]) for i in range(1,6,2)])
	else:
		x = [(shape[i], shape[i+1]) for i in range(1,8,2)]
		x.sort()
		x[3], x[2] = x[2], x[3]
		draw.polygon(w, wh if shape[0]==1 else bl, [i for i in x])
	display.update()
done = False
while not done:
	for e in event.get(): # User did something
		if e.type == QUIT: # If user clicked
			done=True # Flag that we are done so we exit this loop
