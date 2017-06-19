east.
south.
north.
west.
unknown.

position(_,_).

move(position(X1,Y1), position(X2,Y2), east) :- X2 is X1+1, Y2 is Y1, position(X2,Y2).
move(position(X1,Y1), position(X2,Y2), west) :- X2 is X1-1, Y2 is Y1, position(X2,Y2).
move(position(X1,Y1), position(X2,Y2), north) :- X2 is X1, Y2 is Y1-1, position(X2,Y2).
move(position(X1,Y1), position(X2,Y2), south) :- X2 is X1, Y2 is Y1+1, position(X2,Y2).

poss(N, position(X,Y)) :- X>=0,Y>=0,X<N,Y<N.

complete_path(N, P, P, [], []) :- poss(N, P).
complete_path(N, _curP, _tarP, [M|R1], [M|R2]) :-
	M \= unknown, move(_curP, _nPos, M), poss(N, _nPos), complete_path(N, _nPos, _tarP, R1, R2).
complete_path(N, _curP, _tarP, [M|R1], L) :-
	M \= unknown, move(_curP, _nPos, M), not(poss(N, _nPos)), complete_path(N, _curP, _tarP, R1, L).
complete_path(N, _curP, _tarP, [unknown|R1], [M|L]) :-
	move(_curP, _nPos, M), poss(N, _nPos), complete_path(N, _nPos, _tarP, R1, L).

