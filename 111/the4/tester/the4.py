def isLeaf(L):
	return len(L)==1
	
def getLeafs(T):
	res = []
	if isLeaf(T):
		return [T[0].lower()]
	
	for c in T[1:]:
		res += getLeafs(c)
	return res
	
def revDict(D):
	res = {}
	for key in D:
		for obj in D[key]:
			if obj.lower() not in res:
				res[obj.lower()] = []
			res[obj.lower()].append(key.lower())
	return res
	
def is_valid(T, D, S):
	leafs=getLeafs(T)
	rev = revDict(D)
	S = S.lower().split(' ')
	if len(S)!=len(leafs):
		return False
	for i in range(len(leafs)):
		if S[i] not in rev or leafs[i] not in rev[S[i]]:
			return False
	return True

