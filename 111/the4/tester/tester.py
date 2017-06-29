import the4
import sys

grade=0
for i in range(100):
	sys.stdin = file('inp/case%d.txt'%i, 'r')
	T = input()
	D = input()
	n = input()
	for j in range(n):
		Q = input()
		if the4.is_valid(T,D,Q[0])!=Q[1]:
			print "fault case:",i,"test",j,":/"
			grade-=1

print grade
print "yes the max grade is 0, isnt it perfect ^^ u get minus points",
print "if u couldnt ve done the thing, heil hydra"
