#!/bin/bash

# Put Makefile, inputs and this script in the same directory.

files2=({x..z}{1,2})
files3=({a..f}{1,2})
filess=(${files2[@]} ${files3[@]})

make  

ok=true

for i in ${filess[@]}; do
	for j in ${filess[@]}; do
#		echo $i $j
#       if [[ $i > $j  || $i == $j ]]; then
#           continue;
#      fi
		if [[ ${i:0:1} == ${j:0:1} ]]; then
			expected="equal"
		else
			expected="notequal"
		fi
		result=$(./runHK ${i}.txt ${j}.txt)
		if [[ $result != $expected ]]; then
			ok=false
			echo "$i vs $j:"
			echo "expected: $expected"
			echo "your result: $result"
			echo
		fi
	done
done

if $ok; then
    echo "All tests are OK"
fi
