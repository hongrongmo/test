@echo off
C:
cd C:\miami\eijava\eilib
cmd.exe /C ant dist
cd C:\miami\eijava\engvillage
cmd.exe /C ant stage
REM pscp.exe -C -i "U:\My Documents\identity.ppk" engvillage\stage\engvillage.war engresources\stage\engresources.war controller\stage\controller.war jmoschet@neptune.elsevier.com:stage
pscp.exe -C -i "U:\My Documents\identity.ppk" engvillage\stage\engvillage.war engresources\stage\engresources.war controller\stage\controller.war umoscja@cpc1796:
cmd.exe /C ant clean

