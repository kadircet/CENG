%% 
% modified Sibel Tari
% Copyright Barry Van Veen 2014

%% Set up workspace
%
% The filter function computes the output of a system using a difference
% equation with coefficients in vectors B (applied to x[n], x[n-1], ...)
% and A (applied to y[n], y[n-1], ...).  It assumes the coefficient of y[n]
% is unity (A(1) = 1), or else normalizes A and B to make A(1) unity

clear
close all
set(0,'defaultaxesfontsize',22);
set(0,'defaulttextfontsize',22);
linwidth = 1.5;


% Input signals for examples

%cs1 = cos([0:119]*pi/8); % low frequency cosine
cs1 = cos([0:119]*pi/16); % low frequency cosine
cs2 = cos([0:119]*3*pi/16); % high frequency cosine
cs3= cos([0:119]*4*pi/16); % higher frequency cosine

stp = [ zeros(1,20), ones(1,100)]; % signal with a step change at n = 20
delt= [ zeros(1,20), ones(1,1), zeros(1,99)];
%% System 1: 6 point averaging

B = [ 1 1 1 1 1 1]/6;
A = 1;

yc1 = filter(B,A,cs1);
yc2 = filter(B,A,cs2);
yc3 = filter(B,A,cs3);
ys = filter(B,A,stp);
yd = filter(B,A,delt);

figure(1)

subplot(2,1,1)
hp = stem([0:length(ys)-1],ys);
set(hp,'LineWidth',linwidth);

hold on
hp = plot([0:length(stp)-1],stp);
set(hp,'LineStyle','-.')
set(hp,'LineWidth',1);
set(hp,'Color',[1,0,0])

title('6 pt Avg: Step Response')

subplot(2,1,2)
hp = stem([0:length(yd)-1],yd);
set(hp,'LineWidth',linwidth);

hold on
hp = plot([0:length(yd)-1],delt);
set(hp,'LineStyle','-.')
set(hp,'LineWidth',1);
set(hp,'Color',[1,0,0])

title('6 pt Avg: Impulse Response')

h = get(0,'CurrentFigure');
set(h,'Position',[0,0,1000,600])

figure(2)

subplot(3,1,1)
hp = stem([0:length(yc1)-1],yc1);
set(hp,'LineWidth',linwidth);
hold on
hp = plot([0:length(cs1)-1],cs1); %input
set(hp,'LineStyle','-.')
set(hp,'LineWidth',1);
set(hp,'Color',[1,0,0])
title('6 pt Avg: Low Frequency Cosine')

subplot(3,1,2)
hp = stem([0:length(yc2)-1],yc2);
set(hp,'LineWidth',linwidth);
hold on
hp = plot([0:length(cs2)-1],cs2);
set(hp,'LineStyle','-.')
set(hp,'LineWidth',1);
set(hp,'Color',[1,0,0])
title('6 pt Avg: High Frequency Cosine')

subplot(3,1,3)
hp = stem([0:length(yc3)-1],yc3);
set(hp,'LineWidth',linwidth);
hold on
hp = plot([0:length(cs3)-1],cs3);
set(hp,'LineStyle','-.')
set(hp,'LineWidth',1);
set(hp,'Color',[1,0,0])

title('6 pt Avg: Higher Frequency Cosine')

h = get(0,'CurrentFigure');
set(h,'Position',[0,0,1000,600])


