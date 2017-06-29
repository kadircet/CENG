#!/usr/bin/python
import the2
import sys
import time

tot = 0
c = 0
for i in range(100):
	sys.stdin = open("inp/"+str(i+1)+".txt")
	L = input()
	sys.stdin = open("out/"+str(i+1)+".txt")
	R = input()
	s = time.time()
	T = the2.cm(L)
	e = time.time()
	tot += e-s
	print "Run in", (e-s), "seconds, @", i
	flag=True
	for j in range(len(R)):
		R[j] = float(str(R[j]))
		T[j] = float(str(T[j]))
		if abs(R[j] - T[j])>1e-6:
			flag=False
			print R[j], T[j], abs(R[j] - T[j])
			print "Failed case", i+1, "at control", j
			break
	
	if flag:
		c+=1

print "Point get:",c
print "Time elapsed:",tot
