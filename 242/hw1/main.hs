import HW1

main = do
	print (testNewUser)
	print (testAddFriend)
	print (testGetFriendNames)
	print (testSuggestFriends)
	print (testSend)
	print (testNewUserComplex)
	print (testAddUserComplex)
	print (testSendAndGetPosts)
	print (testSendAndGetPosts2)
	print (testSendAndGetPosts3)
	print (testRemoveMember)
	print (testListGroups)
	print (testFriendNamesComplex)

__getUserHelper ((u@(User un _ _ _)):users) uname = if un == uname then u else (__getUserHelper users uname)
__getUser (DB users _) uname = __getUserHelper users uname

testNewUser = (newUser (DB [] [] ) (User "john" "John" [ ] [ ] )) == (DB [User "john" "John" [] []] [])

testAddFriend = let db = (addFriend (DB [User "ssg" "Sedat" [] [],User "kanzuk" "Basak" [] []] []) "ssg" "kanzuk") in 
	let fr = getFriendNames db "ssg" in let fr2 = getFriendNames db "kanzuk" in (elem "Basak" fr) && (elem "Sedat" fr2)

testGetFriendNames = (getFriendNames (DB [User "ssg" "Sedat" ["kanzuk"] [], User "kanzuk" "Basak" ["ssg"] []] []) "ssg") == (["Basak"])
testSuggestFriends = (suggestFriends (DB [(User "a" "A" ["e", "d", "b"] []), (User "e" "E" ["a", "f"] []),(User "d" "D" ["a"] []),(User "b" "B" ["a", "f", "c"] []),(User "c" "C" ["b"] []),(User "f" "F" ["e", "b"] []),(User "g" "G" ["b", "h"] []),(User "h" "H" ["g"] [])] []) (User "a" "A" ["e", "d", "b"] []) 2) == ([User "f" "F" ["e","b"] []])

base = DB [User "user4" "user4" [] [],User "user3" "user3" [] [],User "user2" "user2" [] [],User "user1" "user1" [] []] [Group "ceng" ["user2", "user3", "user4"] [],Group "metu" ["user1", "user4"] []]

testSend = let posts = (getPosts ((sendPost (sendPost base "user3" "first post" [GroupID "metu", GroupID "ceng", UserID "user1"])) "user1" "my great post" [UserID "user2", GroupID "metu"]) (UserID "user1")) in ((elem (Post "user1" "my great post") posts) && (elem (Post "user3" "first post") posts) && length posts == 2)

base2 = DB [User "user6" "user6" [] [],User "user5" "user5" [] [],User "user4" "user4" [] [],User "user3" "user3" [] [],User "user2" "user2" [] [],User "user1" "user1" [] []] [Group "music" [] [],Group "literature" [] [],Group "ceng" ["user2","user3","user4"] [],Group "metu" ["user1","user4"] []]

testNewUserComplex = 
	let oldDb = (DB [User "user7" "user7" ["user2"] [],User "user6" "user6" ["user2","user5"] [],User "user5" "user5" ["user6","user1"] [],User "user4" "user4" ["user1"] [],User "user3" "user3" ["user2"] [],User "user2" "user2" ["user7","user3","user6","user1"] [],User "user1" "user1" ["user2","user5","user4"] []] [Group "music" ["user3","user1","user7","user4"] [],Group "literature" ["user1","user2","user7","user5"] [],Group "ceng" ["user2","user3","user4"] [],Group "metu" ["user1","user4"] []]) in
	let db@(DB users groups) = newUser oldDb (User "user8" "user8" [] []) in
		(elem (User "user8" "user8" [] []) users) && (elem (__getUser oldDb "user1") users) && (elem (__getUser oldDb "user2") users) && (elem (__getUser oldDb "user3") users) && (elem (__getUser oldDb "user4") users) && (elem (__getUser oldDb "user5") users) && (elem (__getUser oldDb "user6") users) && (elem (__getUser oldDb "user7") users) && (length users == 8)

testAddUserComplex = 
	let inputDb = (DB [User "user8" "user8" [] [],User "user7" "user7" ["user2"] [],User "user6" "user6" ["user2","user5"] [],User "user5" "user5" ["user6","user1"] [],User "user4" "user4" ["user1"] [],User "user3" "user3" ["user2"] [],User "user2" "user2" ["user7","user3","user6","user1"] [],User "user1" "user1" ["user2","user5","user4"] []] [Group "music" ["user3","user1","user7","user4"] [],Group "literature" ["user1","user2","user7","user5"] [],Group "ceng" ["user2","user3","user4"] [],Group "metu" ["user1","user4"] []]) in
	let db@(DB users groups) = addFriend inputDb "user8" "user7" in 
		let fr = getFriendNames db "user8" in let fr2 = getFriendNames db "user7" in (elem "user7" fr) && (elem "user8" fr2) 

__count x xs = length (filter (\a -> x == a) xs)

testSendAndGetPosts = let db@(DB users groups) = sendPost (DB [User "user8" "user8" ["user7"] [],User "user7" "user7" ["user8","user2"] [],User "user6" "user6" ["user2","user5"] [],User "user5" "user5" ["user6","user1"] [],User "user4" "user4" ["user1"] [],User "user3" "user3" ["user2"] [],User "user2" "user2" ["user7","user3","user6","user1"] [],User "user1" "user1" ["user2","user5","user4"] []] [Group "music" ["user3","user1","user7","user4"] [],Group "literature" ["user1","user2","user7","user5"] [],Group "ceng" ["user2","user3","user4"] [],Group "metu" ["user1","user4"] []]) "user8" "Here I've come !" [UserID "user3",UserID "user7",GroupID "literature",GroupID "metu",UserID "user4"] in
	let posts1 = getPosts db (UserID "user1") in
	let posts2 = getPosts db (UserID "user2") in
	let posts3 = getPosts db (UserID "user3") in
	let	posts4 = getPosts db (UserID "user4") in
	let	posts7 = getPosts db (UserID "user7") in 
	let postsMetu = getPosts db (GroupID "metu") in
	let postsLit = getPosts db (GroupID "literature") in
	let post = (Post "user8" "Here I've come !") in (__count post postsMetu == 1) && (__count post postsLit == 1) && (__count post posts1 == 1) && 
					(__count post posts2 == 1) && (__count post posts3 == 1) && (__count post posts4 == 1) && (__count post posts7 == 1)

testSendAndGetPosts2 = let db@(DB users groups) = sendPost (DB [User "user8" "user8" ["user7"] [],User "user7" "user7" ["user8","user2"] [],User "user6" "user6" ["user2","user5"] [],User "user5" "user5" ["user6"] [],User "user2" "user2" ["user6","user7"] []] [Group "music" ["user7"] [],Group "literature" ["user2","user7","user5"] [],Group "ceng" ["user2"] [],Group "metu" ["user8"] []]) "user2" "Is anyone there?" [UserID "user8", UserID "user2", GroupID "ceng", GroupID "literature"] in
	let posts2 = getPosts db (UserID "user2") in
	let posts5 = getPosts db (UserID "user5") in
	let	posts7 = getPosts db (UserID "user7") in 
	let	posts8 = getPosts db (UserID "user8") in 
	let postsCeng = getPosts db (GroupID "ceng") in
	let postsLit = getPosts db (GroupID "literature") in 
	let post = (Post "user2" "Is anyone there?") in (__count post postsCeng == 1) && (__count post postsLit == 1) && (__count post posts2 == 1) && 
					(__count post posts5 == 1) && (__count post posts7 == 1) && (__count post posts8 == 1)

testSendAndGetPosts3 = let db@(DB users groups) = sendPost (DB [User "user8" "user8" ["user7"] [],User "user7" "user7" ["user8","user2"] [],User "user6" "user6" ["user2","user5"] [],User "user5" "user5" ["user6","user1"] [],User "user4" "user4" ["user1"] [],User "user3" "user3" ["user2"] [],User "user2" "user2" ["user7","user3","user6","user1"] [],User "user1" "user1" ["user2","user5","user4"] []] [Group "music" ["user3","user1","user7","user4"] [],Group "literature" ["user1","user2","user7","user5"] [],Group "ceng" ["user2","user3","user4"] [],Group "metu" ["user1","user4"] []]) "user4" "hmmmmmmm ok" [UserID "user4", GroupID "music", GroupID "metu"] in
	let posts1 = getPosts db (UserID "user1") in
	let posts3 = getPosts db (UserID "user3") in
	let	posts4 = getPosts db (UserID "user4") in 
	let	posts7 = getPosts db (UserID "user7") in 
	let postsMusic = getPosts db (GroupID "music") in
	let postsMetu = getPosts db (GroupID "metu") in
	let post = (Post "user4" "hmmmmmmm ok") in (__count post postsMusic == 1) && (__count post postsMetu == 1) && (__count post posts1 == 1) && 
					(__count post posts3 == 1) && (__count post posts4 == 1) && (__count post posts7 == 1)

testRemoveMember = let db = (DB [User "user1" "user1" ["user2"] [],User "user2" "user2" ["user1", "user3"] [], User "user3" "user3" ["user2"] []] [(Group "metu" ["user1", "user2"] [])]) in
		let step1 = sendPost db "user1" "post 1" [GroupID "metu"] in
		let step2 = sendPost (removeMember step1 "metu" "user1") "user2" "post 2" [GroupID "metu", UserID "user3"] in
		let step3 = sendPost (addMember step2 "metu" "user1") "user1" "post 3" [GroupID "metu", GroupID "metu"] in
		let posts1 = getPosts step3 (UserID "user1") in
		let postsMetu = getPosts step3 (GroupID "metu") in
		let post1 = (Post "user1" "post 1") in 
		let post2 = (Post "user2" "post 2") in 
		let post3 = (Post "user1" "post 3") in (__count post1 postsMetu == 1) && (__count post2 postsMetu == 1) && (__count post3 postsMetu == 1) && 
					(__count post1 posts1 == 1) && (__count post2 posts1 == 0) && (__count post3 posts1 == 1)

testListGroups = let db = (DB [User "user7" "user7" ["user2"] [],User "user6" "user6" ["user2","user5"] [],User "user5" "user5" ["user6","user1"] [],User "user4" "user4" ["user1"] [],User "user3" "user3" ["user2"] [],User "user2" "user2" ["user7","user3","user6","user1"] [],User "user1" "user1" ["user2","user5","user4"] []] [Group "music" ["user3","user1","user7","user4"] [],Group "literature" ["user1","user2","user7","user5"] [],Group "ceng" ["user2","user3","user4"] [],Group "metu" ["user1","user4"] []])  in
		let list = map (\(Group gname _ _) -> gname) (listGroups db "user1") in
		(elem "literature" list) && (elem "music" list) && (elem "metu" list)


testFriendNamesComplex = let db = (DB [User "user7" "user7real" ["user2"] [],User "user6" "user6real" ["user2","user5"] [],User "user5" "user5real" ["user6","user1"] [],User "user4" "user4real" ["user1"] [],User "user3" "user3real" ["user2"] [],User "user2" "user2real" ["user7","user3","user6","user1"] [],User "user1" "user1real" ["user2","user5","user4"] []] [Group "music" ["user3","user1","user7","user4"] [],Group "literature" ["user1","user2","user7","user5"] [],Group "ceng" ["user2","user3","user4"] [],Group "metu" ["user1","user4"] []])  in
		let list = getFriendNames db "user2" in
		(__count "user1real" list == 1) && (__count "user3real" list == 1) && (__count "user6real" list == 1) && (__count "user7real" list == 1)