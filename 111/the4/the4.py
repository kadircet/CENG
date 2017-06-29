def isLeaf(L):
	return len(L)==1
	
def getLeafs(T):
	res = []
	if isLeaf(T):
		return [T[0].lower()]
	
	for c in T[1:]:
		res += getLeafs(c)
	return res
	
def newDict(D):
	res = {}
	for key in D:
		res[key.lower()] = []
		for obj in D[key]:
			res[key.lower()].append(obj.lower())
	return res
		
def is_valid(T, D, S):
	leafs=getLeafs(T)
	D = newDict(D)
	S = S.lower().split(' ')
	if len(S)!=len(leafs):
		return False
	for i in range(len(leafs)):
		if leafs[i] not in D or S[i] not in D[leafs[i]]:
			return False
	return True
	
#T = ["S", ["NP", ["Pronoun"]], ["VP", ["Verb"], ["NP", ["Det"], ["Noun"], ["Noun"]]]]
#D = {"Pronoun":["I", "He", "She"], "Det":["the", "a", "an"], \
#"Verb":["like", "love", "attend"], \
#"Noun":["CEng111", "CEng100", "morning", "evening", "lecture", "breeze", "course"]}
#print is_valid(T, D, "I like the CEng111 course")
#print is_valid(T, D, "I like the morning breeze")
#print is_valid(T, D, "I the morning breeze like")
#print is_valid(T, D, "I run every day")
