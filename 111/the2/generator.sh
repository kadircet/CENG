for i in `seq 100`
do
	echo "Case $i"
	time python the2tester.py > inp/$i.txt
	time python the2.py < inp/$i.txt > out/$i.txt
done
