%% Introduction to the System Function and System Poles and Zeros
%
% Copyright 2014 Barry Van Veen

% Examine the relationship between pole and zero locations and system
% frequency response.


%% Set up Workspace

clear
close all

set(0,'defaultaxesfontsize',22);
set(0,'defaulttextfontsize',22);
linwidth = 2;

%% First system

a = [ 1 -3/4];
b = [1];

figure(1)
[H,W] = freqz(b,a,1024);
h = plot(W,abs(H));
set(h,'LineWidth',linwidth);
axis([0 pi 0 4.5])
xlabel('Frequency (rads)')
ylabel('Gain')
h = get(0,'CurrentFigure');
set(h,'Position',[160,180,700,500])
pause



figure(2)
zplane(b,a);
#h = get(0,'CurrentFigure');
#set(h,'LineWidth',linwidth);
h = get(0,'CurrentFigure');
set(h,'Position',[160,180,700,500])
pause

%% Second System 

a = [ 1 -2*.95*cos(pi/4) .95^2];
b = [1 0 -1];

figure(3)
[H,W] = freqz(b,a,1024);
h = plot(W,abs(H));
set(h,'LineWidth',linwidth);
axis([0 pi 0 21])
xlabel('Frequency (rads)')
ylabel('Gain')
h = get(0,'CurrentFigure');
set(h,'Position',[160,180,700,500])
pause



figure(4)
zplane(b,a);
#set(h,'LineWidth',linwidth);
h = get(0,'CurrentFigure');
set(h,'Position',[160,180,700,500])
pause

%% Third System

[b,a] = butter(4,[.4 .6]);

figure(5)
[H,W] = freqz(b,a,1024);
h = plot(W,abs(H));
set(h,'LineWidth',linwidth);
axis([0 pi 0 1.2])
xlabel('Frequency (rads)')
ylabel('Gain')
h = get(0,'CurrentFigure');
set(h,'Position',[160,180,700,500])
pause
figure(6)
zplane(b,a);
#set(h,'LineWidth',linwidth);
h = get(0,'CurrentFigure');
set(h,'Position',[160,180,700,500])
pause
%% Fourth System

b = remez(20,[0, .5 .6 .8 .9 1],[0 0 1 1 0 0]);
a = 1;

figure(7)
[H,W] = freqz(b,a,1024);
h = plot(W,abs(H));
set(h,'LineWidth',linwidth);
axis([0 pi 0 1.2])
xlabel('Frequency (rads)')
ylabel('Gain')
h = get(0,'CurrentFigure');
set(h,'Position',[160,180,700,500])
pause
figure(8)
zplane(b,a);
#set(h,'LineWidth',linwidth);
h = get(0,'CurrentFigure');
set(h,'Position',[160,180,700,500])
pause
