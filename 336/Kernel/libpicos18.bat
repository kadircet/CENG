REM  ______________________________________________________________________
REM >                                                                      <
REM >                         PICos18 release 2.10                         <
REM >                                                                      <
REM >             PICos18 - Real-time kernel for PIC18 family              <
REM >                                                                      <
REM >                                                                      <
REM > www.picos18.com                                    www.pragmatec.net <
REM >______________________________________________________________________<

@echo off 
cls 

REM -- point to the source of the kernel 
set PATHTOKERNEL=c:\PICos18

REM -- the automatic variant 
REM cd .. 
REM set PATHTOKERNEL=%cd% 
REM cd Kernel 

REM -- what pic18 chip are you using 
set CHIP=18cxx
REM set CHIP=18F452 

if not exist %PATHTOKERNEL% goto _invalid_path 

REM ------------------------------------------------------------- 
REM ------------------------------------------------------------- 
REM ------------------------------------------------------------- 

REM -- do not edit this 

set MCC18=c:\mcc18
set CFLAGS=-Ou- -Ot- -Ob- -Op- -Or- -Od- -Opa- -On- -w 2
REM set CFLAGS=-O 

echo Trying to build the PICos18 kernel library 
echo ... 

IF EXIST %PATHTOKERNEL%\Kernel\picos18.lib del %PATHTOKERNEL%\Kernel\picos18.lib 
%MCC18%\bin\mplib.exe /c %PATHTOKERNEL%\Kernel\picos18.lib 

echo 1...

for %%f in (%PATHTOKERNEL%\Kernel\*.o) do del %%f 
for %%f in (%PATHTOKERNEL%\Kernel\*.err) do del %%f 
for %%f in (%PATHTOKERNEL%\Kernel\*.lst) do del %%f 
for %%f in (%PATHTOKERNEL%\Kernel\*.i) do del %%f 
for %%i in (%PATHTOKERNEL%\Kernel\*.asm) do %MCC18%\mpasm\mpasmwin.exe /rDEC /l- /o /q /d__LARGE__  /p%CHIP% %%i 
for %%i in (%PATHTOKERNEL%\Kernel\*.c) do %MCC18%\bin\mcc18.exe %CFLAGS%  /i"%MCC18%\h" /i"%PATHTOKERNEL%\Include" -p%CHIP% %%i 

echo 2...

del PICos18.o 
del PICos18i.o 
del PICos18iz.o 
for %%i in (%PATHTOKERNEL%\Kernel\*.o) do %MCC18%\bin\mplib.exe /r %PATHTOKERNEL%\Kernel\picos18.lib %%i 

echo 3...

%MCC18%\bin\mcc18.exe %CFLAGS% /i"%MCC18%\h" /i"%PATHTOKERNEL%\Include" -p%CHIP% PICos18.c 
%MCC18%\bin\mcc18.exe %CFLAGS% /i"%MCC18%\h" /i"%PATHTOKERNEL%\Include" -p%CHIP% PICos18i.c 
%MCC18%\bin\mcc18.exe %CFLAGS% /i"%MCC18%\h" /i"%PATHTOKERNEL%\Include" -p%CHIP% PICos18iz.c 


echo ================================================== 
echo           PICos18 Library has been rebuilt    
echo ================================================== 
dir %PATHTOKERNEL%\Kernel\*.lib 

goto _end 

:_invalid_path 

echo ERROR: Path PATHTOKERNEL does not exists: %PATHTOKERNEL%. 
goto _end 

:_no_chip_set 

echo ERROR: Variable CHIP has not been set. 
goto _end 

:_end 
