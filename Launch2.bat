@echo off
chcp 949 >nul
@title ���� ������Ʈ
set CLASSPATH=.;dist\*;
set HOUR=%time:~0,2%
set MINUTE=%time:~3,2%
set FolderName=%DATE% %HOUR%�� %MINUTE%��


timeout /t 5
java -Xms24G -Xmx56G -XX:NewRatio=2 -XX:SurvivorRatio=5 -XX:+ShowCodeDetailsInExceptionMessages -Dnashorn.args=--no-deprecation-warning -server -Dnet.sf.odinms.wzpath=wz network.Start
pause