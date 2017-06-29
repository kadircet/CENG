import random as rand
import sys

digits="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

def convert(s,i=2):
	if(s==0):
		return ""
	return convert(s/i,i+1)+digits[s%i]
	
visited = {}

print "Generating dataset",
sys.stdout.flush()
f=open("in.txt","w")
o=open("out.txt","w")
for i in range(1000000):
	#f=open("inp/%d.txt"%i, "w")
	s=rand.getrandbits(140-i/8000)
	while visited.get(s, False):
		s=rand.getrandbits(140-i/8000)
	visited[s]=True
	print >>f, s
	#f=open("out/%d.txt"%i, "w")
	print >>o, convert(s)
	#f.write(str(s)+"\n")
	#o.write(convert(s, 2)+"\n")
print "\t\t[DONE]"
