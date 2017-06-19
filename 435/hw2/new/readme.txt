To run the code just upload the src.py to source machine which is to send file
and dst.py to destination machine which is to receive the file. Afterwards
edit src.py's ip variable to one of source machine's ip's, and
edit dst.py's ip variable to one of destinatin machine's ip's. Then after setting
routing table's in switches first run dst.py on destination machine by giving
src machines designated ip as the argument and after that run src.py on source
machine by giving destination machine's designated ip as the argument. After
sending has finished you will see the elapsed time and file transfer speed in
the source machine.
