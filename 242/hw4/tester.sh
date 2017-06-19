./parser.sh
score=0
for i in {0..99}
do
	valgrind ./parser < ./testInputs/$i.in > ./yourOutputs/$i.out 2> test.mem
	mem=`cat test.mem | grep allocs | grep frees | cut -d' ' -f7,9`
	x=`echo $mem | cut -d' ' -f1 | tr -d ','`
	y=`echo $mem | cut -d' ' -f2 | tr -d ','`
	diff ./trueOutputs/$i.out ./yourOutputs/$i.out
	if [[ $? -eq 0 && ( $x -eq $y || $x -eq $(($y+1)) ) ]]; then
		score=$(($score+1))
		echo Success @$i
	else
		echo Case: $i is wrong.
	fi
done
echo Score=$score /100
 
