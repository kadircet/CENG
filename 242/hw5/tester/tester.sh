score=0
if [ -f hw5.pl ]; then
	for i in $(seq 0 $1)
		do
			echo Generating output $i
			swipl hw5.pl < ./testInputs/$i.in &> ./yourOutputs/$i.out
		done
	python2 testOutputs.py
else
	echo 'File hw5.pl not found!'
fi
 
