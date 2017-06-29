from random import random as rand
import the4

MAX_CLASS=15
MAX_CHILD=5
MAX_DEPTH=5
MAX_CL_NAME=10
MAX_WORDS=15
MAX_TESTS=100

D={}

def randStr(L=10):
	r=""
	for j in range(L):
		if int(rand()*10)<5:
			r+=chr(int(rand()*26)+ord('A'))
		else:
			r+=chr(int(rand()*26)+ord('a'))
	return r
	
nclass = int(rand()*MAX_CLASS)+5
clas = []
leafs = []
for i in range(nclass):
	r=randStr(int(rand()*MAX_CL_NAME+1))
	while r.lower() in [key.lower() for key in clas]:
		r=randStr(int(rand()*MAX_CL_NAME+1))
	clas.append(r)
	D[clas[-1]] = []

words = []
nwords = int(rand()*MAX_WORDS)+5
for j in range(nclass):
	for i in range(1+int(rand()*nwords)):
		w=randStr(int(rand()*MAX_CL_NAME+1))
		while w in words:
			w = randStr(int(rand()*MAX_CL_NAME+1))
		words.append(w)
		D[clas[j]].append(words[-1])

def genTree(d):
	if d==0:
		r=clas[int(rand()*nclass)]
		while len(D[r])==0:
			r=clas[int(rand()*nclass)]
		leafs.append(r)
		return [r]
	
	res = [clas[int(rand()*nclass)]]
	nchild = int(rand()*MAX_CHILD+1)
	for i in range(nchild):
		res.append(genTree(d-1))
		
	return res
	
def genValid():
	S=""
	for l in leafs:
		S += " "+randcase(D[l][int(rand()*len(D[l]))])
	return S[1:]

def randcase(S):
	r = ""
	for c in S:
		if int(rand()*10)<5:
			r += c.upper()
		else:
			r+= c.lower()
	return r

def generate():
	S=""
	L = len(leafs)+int(rand()*10)-5
	L = max(1, L)
	for i in range(L):
		S += " "+randcase(words[int(rand()*len(words))])
	return S[1:]

T = genTree(int(rand()*MAX_DEPTH)+2)
print T
print D
ntests = 100
print ntests
for i in range(ntests):
	dice = int(rand()*10)
	if dice<7:
		S=genValid()
	else:
		S=generate()
	print [S, the4.is_valid(T,D,S)]

