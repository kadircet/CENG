gcc the3.c -othe3 -Wall -pedantic-errors -ansi
if [ ! -d "cases" ]
then
	mkdir cases
	for i in `seq 0 99`
	do
		python the3tester.py > cases/$i.case
	done
fi
time python test.py

