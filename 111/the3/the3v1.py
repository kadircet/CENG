LEFT = 0
RIGHT = 1
ops = ['+', '-', '*', '/', '^']
ZP = [0, 2, 3, 1, 4]
ZA = [LEFT, LEFT, LEFT, LEFT, RIGHT]
EP = [3, 3, 1, 1, 0]
EA = [LEFT, LEFT, LEFT, LEFT, RIGHT]

def isOp(e):
	return e in ops

#base 0 => earthian
def prec(o1, o2, base):
	o1, o2 = ops.index(o1), ops.index(o2)
	if base:
		o1, o2 = ZP[o1], ZP[o2]
	else:
		o1, o2 = EP[o1], EP[o2]
	if o1==o2:
		return 0
	if o1>o2:
		return 1
	return -1
		
def assoc(o1, base):
	o1 = ops.index(o1)
	if base:
		o1 = ZP[o1]
		return ZA[o1]
	return EA[o1]
	
def swap(exp):
	exp = list(exp)
	for i in range(len(exp)):
		if exp[i]=='*':
			exp[i]='/'
		elif exp[i]=='/':
			exp[i] = '*'
	return "".join(exp)
	
def toInfix(exp, to):
	s = []
	for e in exp:
		if isOp(e):
			b=s.pop()
			a=s.pop()
			s.append('('+a+e+b+')')
		else:
			s.append(e)
	return s[0]

def toPostfix(exp, base):
	s = []
	res = []
	for e in exp:
		if e==' ':
			continue
		elif isOp(e):
			while len(s)!=0 and s[-1]!='(':
				if (prec(s[-1], e, base)==0 and assoc(e,base)==LEFT) or (prec(s[-1], e, base)==-1):
					res.append(s.pop())
				else:
					break
			s.append(e)
		elif e=='(':
			s.append(e)
		elif e==')':
			while s[-1]!='(':
				res.append(s.pop())
			s.pop()
		else:
			res.append(e)
	while len(s)!=0:
		res.append(s.pop())
	return res
	
def ToZoitankian(exp):
	return toInfix(swap(toPostfix(list(exp), 0)), 1)

def ToEarthian(exp):
	return toInfix(swap(toPostfix(list(exp), 1)), 0)

