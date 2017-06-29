#!/bin/bash
([ -f "in.txt" ] && [ -f "out.txt" ]) || python converter.py
gcc tester.c -ansi -Wall -pedantic-errors -otester -e _main || exit
time ./tester < in.txt > out2.txt
printf "%s" "Calculating score ..."
GREP=$(grep -Fvxnf out2.txt out.txt)
P=$(bc -l <<< "100-$(echo -n $GREP | wc -l)/10000.0")
printf "\t\t%s\n" "[DONE]"
for i in $(echo -n $GREP | cut -d":" -f1)
do
	echo "Error @case $i, input:$(sed "${i}q;d" in.txt), output:$(sed "${i}q;d" out.txt), your output:$(sed "${i}q;d" out2.txt)"
done
echo "Score:$P/100"

