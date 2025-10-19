@echo off
@color 03
@title 강림 프로젝트
set HOUR=%time:~0,2%
set MINUTE=%time:~3,2%
set FolderName=%DATE% %HOUR%시 %MINUTE%분

:: MariaDB 재시작
net stop MariaDB
net start MariaDB

set CLASSPATH=.;dist\*;dist\lib\*;
:: java -Xms32G -Xmx32G -XX:NewRatio=2 -XX:SurvivorRatio=5 -XX:+ShowCodeDetailsInExceptionMessages -XX:-OmitStackTraceInFastThrow -Dnashorn.args=--no-deprecation-warning -server -Dnet.sf.odinms.wzpath=wz network.Start
java -Xms64G -Xmx64G -XX:NewRatio=2 -XX:SurvivorRatio=5 -XX:+ShowCodeDetailsInExceptionMessages -Dnashorn.args=--no-deprecation-warning -server -Dnet.sf.odinms.wzpath=wz network.Start
pause