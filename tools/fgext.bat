@echo off
color 0a
for /r %%i in ("*.part01.rar") do "C:\Program Files\7-Zip\7z" x "%%i"
for /r %%i in ("*.part1.rar") do "C:\Program Files\7-Zip\7z" x "%%i"
del *.rar
del "Verify BIN files before installation.bat"
cd /d "%~dp0"
cd MD5
start QuickSFV.EXE fitgirl-bins.md5