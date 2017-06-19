module HW1 where

--Data
------

type RealName = String
type UserName = String
type GroupName = String
type Message = String

data Post    = Post UserName Message deriving (Show, Eq)
data To 	 = UserID UserName | GroupID GroupName deriving (Show, Eq)
data User    = User UserName RealName [UserName] [Post] deriving (Show, Eq)
data Group   = Group GroupName [UserName] [Post] deriving (Show, Eq)
data DB		 = DB [User] [Group] deriving (Show, Eq)

--1. Commands

newUser      :: DB -> User -> DB
addFriend    :: DB -> UserName -> UserName -> DB
sendPost 	 :: DB -> UserName -> Message -> [To] -> DB
newGroup 	 :: DB -> GroupName -> DB
addMember 	 :: DB -> GroupName -> UserName -> DB
removeMember :: DB -> GroupName -> UserName -> DB

--2. Queries

getFriendNames :: DB -> UserName -> [RealName]
getPosts 	   :: DB -> To -> [Post]
listGroups 	   :: DB -> UserName -> [Group]
suggestFriends :: DB -> User -> Int -> [User]

---- IMPLEMENTATIONS ----
--User Getters--
userName :: User -> UserName
userName (User uName _ _ _ ) = uName

realName :: User -> RealName
realName (User _ rName _ _ ) = rName

friendListU :: User -> [UserName]
friendListU (User _ _ uNameList _ ) = uNameList

postListU :: User -> [Post]
postListU (User _ _ _ pList ) = pList
--DB Getters--
userList :: DB -> [User]
userList (DB uList _ ) = uList

groupList :: DB -> [Group]
groupList (DB _ gList) = gList
--Group Getters--
groupName :: Group -> GroupName
groupName (Group gName _ _ ) = gName

userNameListG :: Group -> [UserName]
userNameListG (Group _ uList _ ) = uList

postListG :: Group -> [Post]
postListG (Group _ _ pList) = pList
--

alreadyUser :: [User]-> UserName -> Bool
alreadyUser [] name = False
alreadyUser (x:xs) name
	| name == (userName x) 	= True
	| otherwise = alreadyUser xs name

newUser database user = if alreadyUser (userList database) (userName user)
							then database
							else (DB (userList database ++ [user]) (groupList database))
--							
addFriendR :: [User] -> UserName -> UserName -> [User]
addFriendR [] user1 user2 = []
addFriendR (x:xs) user1 user2 
	| (userName x) == user1 = [(User (user1) (realName x) (friendListU x ++ [user2]) (postListU x))] ++ (addFriendR xs user1 user2)
	| (userName x) == user2 = [(User (user2) (realName x) (friendListU x ++ [user1]) (postListU x))] ++ (addFriendR xs user1 user2)
	| otherwise = [x] ++ (addFriendR xs user1 user2)
	
addFriend database user1 user2 =  DB (addFriendR (userList database) user1 user2) (groupList database)
--
sendPostRecursivelyG :: [Group] -> GroupName -> Post -> [Group]
sendPostRecursivelyG [] gName post = []
sendPostRecursivelyG (x:xs) gName post
	| ((groupName x) == gName) && (post `elem` (postListG x))/=True= [(Group gName (userNameListG x) ((postListG x) ++ [post]))] ++ xs
	| otherwise = [x] ++ (sendPostRecursivelyG xs gName post)
	
sendPostRecursivelyU :: [User] -> UserName -> Post -> [User]
sendPostRecursivelyU [] uName post = []
sendPostRecursivelyU (x:xs) uName post 
	| ((userName x) == uName)&& (post `elem` (postListU x))/=True  = [(User (uName) (realName x) (friendListU x) ((postListU) x ++ [post]))] ++ xs
	| otherwise = [x] ++ (sendPostRecursivelyU xs uName post)
 
toExtend:: Group -> [To]
toExtend group = [(UserID to) | to <- (userNameListG group)]

sendPost database user1 sentence [] = database
sendPost database user1 sentence ((GroupID x):xs) = sendPost (DB (userList database) (sendPostRecursivelyG (groupList database) x (Post user1 sentence))) user1 sentence (xs ++ toExtend(findGroup (groupList database) x))
sendPost database user1 sentence ((UserID x):xs) = sendPost (DB (sendPostRecursivelyU (userList database) x (Post user1 sentence)) (groupList database)) user1 sentence xs 
--
newGroup database gName = (DB (userList database) ( (groupList database) ++ [Group gName [] []] ))
--
addMemberR :: [UserName] -> UserName -> [UserName]
addMemberR [] uName = [uName]
addMemberR  (x:xs) uName
	| x == uName = x:xs
	| otherwise = x:(addMemberR xs uName)
	
addMemberGroupFind :: [Group] -> GroupName -> UserName -> [Group]
addMemberGroupFind [] gName uName= []
addMemberGroupFind (x:xs) gName uName
	| (groupName x) == gName = (Group gName (addMemberR (userNameListG x) uName) (postListG x)):xs
	| otherwise = x:(addMemberGroupFind xs gName uName)
	
addMember database gName uName = (DB (userList database) (addMemberGroupFind (groupList database) gName uName))
--
removeMemberGroupFind :: [Group] -> GroupName -> UserName -> [Group]
removeMemberGroupFind [] gName uName= []
removeMemberGroupFind (x:xs) gName uName
	| (groupName x) == gName = (Group gName ([a | a<-(userNameListG x),a/=uName]) (postListG x)):xs
	| otherwise = x:(removeMemberGroupFind xs gName uName)
	
removeMember database gName uName = (DB (userList database) (removeMemberGroupFind (groupList database) gName uName))
--
findUser :: [User] -> UserName -> User
findUser (x:xs) uName
	| uName==(userName x) = x
	| otherwise = findUser xs uName
namePairs :: [User] -> [(UserName,RealName)]
namePairs [] = []
namePairs (x:xs) = ((userName x), (realName x)):(namePairs xs)
findInPairs :: UserName -> [(UserName,RealName)] -> RealName
findInPairs uname ((uName,rName):xs)
	| uname == uName = rName
	| otherwise = findInPairs uname xs
realFriends :: [UserName] -> [(UserName,RealName)] -> [RealName]
realFriends [] _ = []
realFriends (x:xs) pairs = (findInPairs x pairs) : (realFriends xs pairs)

getFriendNames database uName = realFriends (friendListU(findUser (userList database) uName)) (namePairs (userList database))
--
listGroups database uName = [a | a<-(groupList database), uName `elem` (userNameListG a) ]
--
findGroup :: [Group] -> GroupName -> Group
findGroup (x:xs) gName
		| (groupName x) == gName = x
		| otherwise = findGroup xs gName
getPosts db (GroupID gName) = postListG ( findGroup (groupList db) gName)
getPosts db (UserID uName) = postListU ( findUser (userList db) uName)
--
suggestFriendsList :: [User] -> User -> Int -> [User]
suggestFriendsList [] _ _ = []
suggestFriendsList (x:xs) user num
	| (userName x) == (userName user) = suggestFriendsList xs user num
	| (userName x) `elem` (friendListU user) = suggestFriendsList xs user num
	| (length([a | a <- (friendListU user), a `elem` (friendListU x)])) >= num = x:(suggestFriendsList xs user num)
	| otherwise = suggestFriendsList xs user num
	
suggestFriends database user num = suggestFriendsList (userList database) user num

