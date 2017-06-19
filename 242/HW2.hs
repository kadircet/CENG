
module HW2(FeatureStruct, FeatureTerm(Simple, Struct), emptyfs, getpath, addpath, delpath, union, intersect) where

data FeatureTerm = Simple String | Struct FeatureStruct
data FeatureStruct = FS [(String, FeatureTerm)]
 
emptyfs :: FeatureStruct
getpath :: FeatureStruct -> [String] -> Maybe FeatureTerm
addpath :: FeatureStruct -> [String] -> FeatureTerm -> FeatureStruct
delpath :: FeatureStruct -> [String] -> FeatureStruct
union :: FeatureStruct -> FeatureStruct -> Maybe FeatureStruct
intersect :: FeatureStruct -> FeatureStruct -> FeatureStruct

-- DO NOT MODIFT ABOVE

instance Show FeatureTerm where
    -- Implement it here
	show (Simple x) = show x
	show (Struct y) = show y
instance Show FeatureStruct where
    -- Implement it here
	show (FS []) = "[]"
	show (FS ((a, Simple b):[])) = "["++a++"="++(show b)++"]"
	show (FS ((a, Simple b):xs)) = "["++a++"="++(show b)++", "++(showrest xs)++"]"
	show (FS ((a,b):[])) = "["++a++"=>"++(show b)++"]"
	show (FS ((a,b):xs)) = "["++a++"=>"++(show b)++", "++(showrest xs)++"]"
instance Eq FeatureStruct where
    -- Implement it here
	(FS []) == (FS []) = True
	(FS []) == _ = False
	_ == (FS []) = False
	fx@(FS (x@(a0,b0):xs)) == fy@(FS (y@(a1,b1):ys))
		| a0==a1 = (isEqual b0 b1) && ((FS xs) == (FS ys))
		| otherwise = False

isEqual (Simple a) (Simple b) = a==b
isEqual (Struct a) (Struct b) = a==b
isEqual _ _ = False

showrest ((a, Simple b):[]) = a++"="++(show b)
showrest ((a, Simple b):xs) = a++"="++(show b)++", "++(showrest xs)
showrest ((a, b):[]) = a++"=>"++(show b)
showrest ((a, b):xs) = a++"=>"++(show b)++", "++(showrest xs)

emptyfs = (FS [])

getpath (FS []) _ = Nothing
getpath _ [] = Nothing
getpath (FS ((a,b):xs)) p@(y:[])
	| a==y = Just b
	| a>y = Nothing
	| otherwise = getpath (FS xs) p
getpath (FS ((a,Simple b):xs)) p@(y:ys)
	| a>=y = Nothing
	| otherwise = getpath (FS xs) p
getpath (FS ((a,Struct b):xs)) p@(y:ys)
	| a==y = getpath b ys
	| a>y = Nothing
	| otherwise = getpath (FS xs) p

getList (FS x) = x
getListM (Just (FS x)) = x

addpath fs [] _ = fs
addpath f@(FS []) (y:[]) term = FS [(y, term)]
addpath f@(FS []) (y:ys) term = FS [(y, (Struct $ addpath f ys term))]
addpath (FS lx@(x@(a,b):xs)) p@(y:[]) term
	| a<y = FS (x:(getList $ addpath (FS xs) p term))
	| a==y = FS ((a, term):xs)
	| otherwise = FS ((y, term):lx)
addpath (FS lx@(x@(a,b):xs)) p@(y:ys) term
	| a<y = FS (x:(getList $ addpath (FS xs) p term))
	| a==y = FS ((a, (addpathterm b ys term)):xs)
	| otherwise = FS ((y, (addpathterm b ys term)):lx)

addpathterm (Simple a) p term = Struct $ addpath (FS []) p term
addpathterm (Struct a) p term = Struct $ addpath a p term

delpath fs@(FS []) _ = fs
delpath fs [] = fs
delpath fs@(FS (x@(a,b):xs)) p@(y:[])
	| a<y = FS (x:(getList $ delpath (FS xs) p))
	| a==y = FS xs
	| otherwise = fs
delpath fs@(FS (x@(a,b):xs)) p@(y:ys)
	| a<y = FS (x:(getList $ delpath (FS xs) p))
	| a==y = FS ((delpathterm x ys):xs)
	| otherwise = fs

delpathterm x@(a, (Simple b)) _ = x
delpathterm (a, (Struct b)) p = (a, Struct $ delpath b p)

union (FS []) fs = Just fs
union fs (FS []) = Just fs
union fx@(FS (x@(a0,b0):xs)) fy@(FS (y@(a1,b1):ys))
	| a0<a1 = case lun of
				Nothing -> Nothing
				_ -> Just (FS (x:(getListM lun)))
	| a0>a1 = case run of
				Nothing -> Nothing
				_ -> Just (FS (y:(getListM run)))
	| otherwise = case (mun,bun) of
				(Nothing,_) -> Nothing
				(_,Nothing) -> Nothing
				(Just z,_) -> Just (FS (z:(getListM bun)))
	where 
	lun = union (FS xs) fy
	run = union fx (FS ys)
	mun = unionelem x y
	bun = union (FS xs) (FS ys)

unionelem x@(a0, Simple b0) y@(a1, Simple b1)
	| b0==b1 = Just x
	| otherwise = Nothing
unionelem x@(a0, Struct b0) y@(a1, Struct b1) = case z of
	Nothing -> Nothing
	(Just z) -> Just (a0, Struct z)
	where z = union b0 b1
unionelem _ _ = Nothing

intersect f@(FS []) _ = f
intersect _ f@(FS []) = f
intersect fx@(FS (x@(a0,b0):xs)) fy@(FS (y@(a1,b1):ys))
	| a0<a1 = intersect (FS xs) fy
	| a0>a1 = intersect fx (FS ys)
	| otherwise = case (intersectelem x y) of
		(Just z) -> FS (z:(getList rest))
		_ -> rest
		where rest=intersect (FS xs) (FS ys)

intersectelem x@(a0, Simple b0) y@(a1, Simple b1)
	| b0==b1 = Just x
	| otherwise = Nothing
intersectelem x@(a0, Struct b0) y@(a1, Struct b1) = Just (a0, Struct $ intersect b0 b1)
intersectelem _ _ = Nothing
