#!/usr/bin/python

inst = {
	0: "HLT",
	1: "MOV r1, %#x",
	2: "MOV r2, %#x",
	3: "MOV r1, [%#x]",
	4: "MOV r2, [%#x]",
	5: "MOV r1, r2",
	6: "MOV r1, [r2]",
	7: "MOV [r1], r2",
	8: "MOV [%#x], r1",
	9: "JMP %#x",
	10:"JNZ %#x",
	11:"ADD r1, r2",
	12:"SUB r1, r2",
	13:"MUL r1, r2",
	14:"DIV r1, r2",
	15:"NEG r1",
	16:"CMP r1, r2"
}

desc = """HLT means HALT the THE machine
MOV means move its second parameter to its first parameter
JMP means set \"I\" register to its parameter
JNZ means set \"I\" register to its parameter if r1 is not zero
ADD means increment its first parameter by its second parameter
SUB means decrement its first parameter by its second parameter
MUL means multiply its first and second parameter and store the result in the first parameter
DIV means divide its first parameter by its second parameter and store the result in first parameter
NEG means negate its first parameter
CMP means compare its first and second parameter and set r1 to 0 if they are equal, to -1 if second parameter is greater, to 1 if first parameter is greater



BE CAREFUL WITH THE VARIABLES IN the THE MACHINE!!!
I CANT GUARANTEE THIS WILL WORK GOOD IF THE INSTRUCTIONS ARE TOO MUCH MIXED WITH THE VARIABLES, FOR BETTER PERFORMANCE MOVE YOUR VARIABLES TO THE END"""


print desc
inp = raw_input()
inp = inp.split(' ')
inp = [int(x) for x in inp]

ins = []
res = []
var = []
i = 0
while i < len(inp):
	ins.append(inp[i])
	trans = hex(i) + ": " + inst[inp[i]]
	if "%#x" in trans:
		if "[%#x]" in trans:
			var.append(inp[i+1])
		trans = trans % (inp[i+1])
		i+=1
		ins.append(inp[i])
	i+=1
	res.append(trans)

var = set(var)
for i in var:
	print "VARIABLE at [%#x]=%#x" % (i, ins[i])
	for j in range(len(res)):
		if res[j].startswith("%#x" % i):
			res[j] = ""
	
for w in res:
	if w!="":
		print w
