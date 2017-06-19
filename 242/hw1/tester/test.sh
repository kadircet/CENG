for db in db.*
do
	rm -rf test
	rm -rf test.hs
	cp $db test.in
	python2 tester.py
	echo -e "Tested $db\n\n"
done
