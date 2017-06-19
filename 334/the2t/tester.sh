make all
i=100
while [ true ]
do
	echo $i
	python2 generator.py > test.in || continue
	./simulator < test.in > test.out
	echo 'Sim finished'
	(python2 checker.py test.in test.out 400 | grep -vi 'seems good') && (cp test.in test$i.in; cp test.out test$i.out)
	echo 'Seems good'
	i=$(($i+1))
done
