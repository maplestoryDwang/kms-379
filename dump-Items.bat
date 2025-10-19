@echo off
@color 03
@title Dump
set CLASSPATH=.;dist\*;dist\lib\*;
java -server -Dnet.sf.odinms.wzpath=wz objects.wz.sql.DumpItems
pause