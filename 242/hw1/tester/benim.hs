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

exists [] id = False
exists (x:xs) id
	| x==id		= True
	| otherwise	= exists xs id
	
addToList lst elm
	| (exists lst elm)	= lst
	| otherwise			= elm:lst

dltFromList [] _ = []
dltFromList (cur:rest) elm
	| cur==elm	= rest
	| otherwise	= cur:(dltFromList rest elm)

userExists [] id = False
userExists ((User uname _ _ _):xs) id
	| uname==id	= True
	| otherwise	= userExists xs id

newUser (DB users groups) user@(User uname _ _ _)
	| (userExists users uname)==False	= DB (user:users) groups
	| otherwise							= DB users groups

addAsFriend [] _ _ = []
addAsFriend (user@(User uname rname friends posts):rest) us1 us2
	| uname==us1= (User uname rname (addToList friends us2) posts):rest
	| otherwise	= user:(addAsFriend rest us1 us2)

addFriend (DB users groups) us1 us2 = (DB (addAsFriend 
	(addAsFriend users us1 us2) us2 us1) groups)

addPostU [] _ _ = []
addPostU (user@(User uname rname friends posts):rest) post rcv
	| uname==rcv= (User uname rname friends (addToList posts post)):rest
	| otherwise	= user:(addPostU rest post rcv)

addPostM [] _ _ = []
addPostM (user@(User uname rname friends posts):rest) gusers post
	| (exists gusers uname)	= (User uname rname friends (addToList posts post)):
		(addPostM rest gusers post)
	| otherwise				= user:(addPostM rest gusers post)

addPostG [] _ _ = []
addPostG (group@(Group gname gusers posts):rest) post rcv
	| gname==rcv= (Group gname gusers (addToList posts post)):rest
	| otherwise	= group:(addPostG rest post rcv)

getMembers [] _ = []
getMembers ((Group gname gusers posts):rest) grp
	| gname==grp= gusers
	| otherwise	= getMembers rest grp

sendPost db _ _ [] = db
sendPost (DB users groups) u1 msg (UserID to:tos)	= 
	sendPost (DB (addPostU users (Post u1 msg) to) groups) u1 msg tos
sendPost (DB users groups) u1 msg (GroupID to:tos)	= 
	sendPost (DB (addPostM users (getMembers groups to) pst) (addPostG groups pst to)) u1 msg tos
	where pst = Post u1 msg
	
groupExists [] id = False
groupExists ((Group gname _ _):xs) id
	| gname==id	= True
	| otherwise	= groupExists xs id

newGroup db@(DB users groups) group
	| (groupExists groups group)	= db
	| otherwise						= DB users ((Group group [] []):groups)

addToGroup [] _ _ = []
addToGroup (cur@(Group gname gusers gposts):rest) group user
	| gname==group	= (Group gname (addToList gusers user) gposts):rest
	| otherwise		= cur:(addToGroup rest group user)
	
addMember (DB users groups) group user = (DB users (addToGroup groups group user))

removeFromGroup [] _ _ = []
removeFromGroup (cur@(Group gname gusers gposts):rest) group user
	| gname==group	= (Group gname (dltFromList gusers user) gposts):rest
	| otherwise		= cur:(removeFromGroup rest group user)
removeMember (DB users groups) group user = DB users (removeFromGroup groups group user)

getRealName [] _ = ""
getRealName ((User uname rname friends posts):rest) user
	| uname==user	= rname
	| otherwise		= getRealName rest user

getFriends [] _ = []
getFriends ((User uname _ friends _):rest) user
	| uname==user	= friends
	| otherwise		= getFriends rest user
getFriendNames (DB users groups) user 
	= [getRealName users x | x <- (getFriends users user)]

getPostU [] _ = []
getPostU ((User uname _ _ posts):rest) id
	| uname==id	= posts
	| otherwise	= getPostU rest id

getPostG [] _ = []
getPostG ((Group gname _ posts):rest) id
	| gname==id = posts
	| otherwise	= getPostG rest id

getPosts (DB users groups) (UserID to) = getPostU users to
getPosts (DB users groups) (GroupID to) = getPostG groups to

listGroups (DB users groups) user 
	= filter (\(Group _ users _) -> exists users user) groups

intersect lst1 lst2 = [x | x<-lst1, exists lst2 x]
hasCommon lst1 lst2 lim = length (intersect lst1 lst2) >= lim

suggestFriends (DB users groups) (User uname _ uFriends _) lim 
	= filter (\(User x _ _ _) -> (not ((exists uFriends x) || uname==x)))
	(filter (\(User _ _ friends _)->hasCommon uFriends friends lim) users)
