@echo off
@color 03
@title ���� ������Ʈ
set HOUR=%time:~0,2%
set MINUTE=%time:~3,2%
set FolderName=%DATE% %HOUR%�� %MINUTE%��

:: MariaDB �����
net stop MariaDB
net start MariaDB

set CLASSPATH=.;dist\*;dist\lib\*;
:: java -Xms32G -Xmx32G -XX:NewRatio=2 -XX:SurvivorRatio=5 -XX:+ShowCodeDetailsInExceptionMessages -XX:-OmitStackTraceInFastThrow -Dnashorn.args=--no-deprecation-warning -server -Dnet.sf.odinms.wzpath=wz network.Start
java -Xms64G -Xmx64G -XX:NewRatio=2 -XX:SurvivorRatio=5 -XX:+ShowCodeDetailsInExceptionMessages -Dnashorn.args=--no-deprecation-warning -server -Dnet.sf.odinms.wzpath=wz network.Start
pause