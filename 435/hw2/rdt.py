
def checksum(data):
    res=0
    if len(data)%2==1:
        res = ord(data[-1])
    for i in range(0,len(data),2):
        res^= (ord(data[i])<<8) | ord(data[i+1])
    return res


