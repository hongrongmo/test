@echo off

cmd.exe /C ant clean

pushd eilib
cmd.exe /C ant dist
popd

pushd engvillage

copy stage.properties.row1 stage.properties
if errorlevel 1 goto end

cmd.exe /C ant stage

rmdir /S /Q "%USERPROFILE%"\Desktop\build

mkdir "%USERPROFILE%"\Desktop\build\row1
copy engvillage\stage\engvillage.war "%USERPROFILE%"\Desktop\build\row1
copy engresources\stage\engresources.war "%USERPROFILE%"\Desktop\build\row1
copy controller\stage\controller.war "%USERPROFILE%"\Desktop\build\row1
copy docview\stage\docview.war "%USERPROFILE%"\Desktop\build\row1

cmd.exe /C ant clean

copy stage.properties.row2 stage.properties
if errorlevel 1 goto end

cmd.exe /C ant stage

mkdir "%USERPROFILE%"\Desktop\build\row2
copy engvillage\stage\engvillage.war "%USERPROFILE%"\Desktop\build\row2
copy engresources\stage\engresources.war "%USERPROFILE%"\Desktop\build\row2
copy controller\stage\controller.war "%USERPROFILE%"\Desktop\build\row2
copy docview\stage\docview.war "%USERPROFILE%"\Desktop\build\row2

cmd.exe /C ant clean


:end
echo End of batch program.

popd
