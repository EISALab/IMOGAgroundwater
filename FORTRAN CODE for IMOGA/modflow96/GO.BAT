rem batch program to run each test
@echo off
cls
rem
rem Available tests.
rem
if exist go del go
IF "%1"=="1" goto test1
IF "%1"=="2" goto test2
IF "%1"=="3" goto test3
IF "%1"=="4" goto test4
IF "%1"=="5" goto test5
IF "%1"=="6" goto test6
IF "%1"=="7" goto test7
rem
echo.
echo Stopping tests
goto stop
rem
:test1
echo ..\data\ibs.nam > go
goto run
rem
:test2
echo ..\data\str.nam > go
goto run
rem
:test3
echo ..\data\twri.nam > go
goto run
rem
:test4
echo ..\data\bcf2ss.nam > go
goto run
rem
:test5
echo ..\data\tlkp1.nam > go
goto run
:test6
echo ..\data\restest.nam > go
goto run
:test7
echo ..\data\fhb.nam > go
rem
:run
..\bin\modflw96 < go
:end
echo.
echo Simulation completed
echo.
pause
test
:stop
prompt $p$g
cls
