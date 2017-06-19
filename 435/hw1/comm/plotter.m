x=[40,80,160,320];
y=[42.0280623436, 80.9206318855, 162.675359249, 321.32212162];
e=[1.84840390578, 3.1282490074, 2.11410422494, 2.07671289966];

errorbar(x,y,e);
xlabel('Emulated Delay(ms)/+-20ms');
ylabel('End-to-End Delay(ms)');
title('Emulated Delay vs End-to-End Delay');
print('graph.png')
pause;
