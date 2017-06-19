:- module(hw5, [complete_path/5]).

move(position(X1,Y1), position(X2,Y1), east) :- X2 is X1+1.
move(position(X1,Y1), position(X2,Y1), west) :- X2 is X1-1.
move(position(X1,Y1), position(X1,Y2), north) :- Y2 is Y1-1.
move(position(X1,Y1), position(X1,Y2), south) :- Y2 is Y1+1.

poss(N, position(X,Y)) :- X>=0,Y>=0,X<N,Y<N.

%base case
complete_path(N, P, P, [], []) :- 
	poss(N, P), !.
	
%drop harmful
complete_path(N, _curP, _tarP, [M|R1], L) :-
	move(_curP, _nPos, M), \+(poss(N, _nPos)), !, complete_path(N, _curP, _tarP, R1, L).

%do the move	
complete_path(N, _curP, _tarP, [M|R1], [M|L]) :-
	move(_curP, _nPos, M), poss(N, _nPos), !, complete_path(N, _nPos, _tarP, R1, L).

%replace unknown
complete_path(N, _curP, _tarP, [unknown|R1], [M|L]) :-
	nonvar(M), move(_curP, _nPos, M),!, poss(N, _nPos), complete_path(N, _nPos, _tarP, R1, L).
complete_path(N, _curP, _tarP, [unknown|R1], [M|L]) :-
	move(_curP, _nPos, M), poss(N, _nPos), complete_path(N, _nPos, _tarP, R1, L).

