@echo off
@color F0
@title SpreadSheet CONSOLE
set CLASSPATH=.;dist\*;dist\lib\*;dist\lib\api\*;
java api.SpreadSheetAPI
pause