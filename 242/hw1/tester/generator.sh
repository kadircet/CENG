g++ generator.cpp -og -std=c++11
for i in `seq 0 100`
do
	echo -n "Generating $i "
	./g > db.$i
	echo "[ DONE ]"
done
