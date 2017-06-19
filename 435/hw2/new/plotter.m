x=[1,2,3];
y=[37716.919347296, 12558.099046066003, 6332.1159084477];
y(3)/y(1)
e=[134.16511172321376, 134.6642633613152, 131.06257779543006];
x=[0.1,1,5];
y=[6074.2791353828, 6239.6920951512, 10022.8115839389];
e=[139.82206465609477, 130.66051871991084, 131.74679486441136];

errorbar(x,y,e);
xlabel('Packet Loss Percentage(%)');
ylabel('File Transfer Time(ms)');
title('File Transfer Time vs Packet Loss Percentage');
print('graph2.png')
pause;
