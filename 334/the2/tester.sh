make all
#python2 generator.py > test.in
#for i in test-*.txt
#do
i=test-08.txt
	echo $i
	./simulator < $i > $i.out
	python2 checker.py $i $i.out 400
#done
