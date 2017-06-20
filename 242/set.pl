insert(X,R,[X|R]).
insert(X,[A|Rem],[A|List]) :- insert(X,Rem, List).

% insert first arg term to all elements of second argument
% insert to beginning of 3rd argument. when 2nd arg empty return
% 3rd argument as result
inserttermall(_,[],R,R).
inserttermall(T,[S|SRest],Inp,Res) :- inserttermall(T,SRest,[[T|S]|Inp],Res).

setequal([],[]).
setequal([A|Arem],B) :- insert(A,BRem,B), setequal(Arem,BRem).

subset([],_).
subset([A|ARest],B)   :- insert(A,BRem,B), subset(ARest,BRem).

powerset([],[[]]).
powerset([A|ARest],Res) :- powerset(ARest,PAR),
	 inserttermall(A,PAR,PAR,Res).	   % insert A to all elements of PAR
					   % combine with PAR


setunion([],T,T).
setunion([A|Rem],T, Z) :- member(A,T),!, setunion(Rem,T,Z).
setunion([A|Rem],T, [A|Z]) :- setunion(Rem,T,Z).

setinter([],_,[]).
setinter([A|Rem],T, [A|Z]) :- member(A,T),!, setinter(Rem,T,Z).
setinter([_|Rem],T, Z) :- setinter(Rem,T,Z).

setdiff(A,[],A).
setdiff(A,[B|BRest], Res ) :- insert(B,ARem, A), ! , setdiff(ARem,BRest,Res).
setdiff(A,[_|BRest], Res ) :- setdiff(A,BRest,Res).
