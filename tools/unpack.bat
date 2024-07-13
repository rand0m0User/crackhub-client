@echo off
color 0a
"C:\Program Files\7-Zip\7z" x "*.rar"
"C:\Program Files\7-Zip\7z" x "*.iso"
del "autorun.inf"
arc.exe x *.bin
"C:\Program Files\7-Zip\7z" x "setup.bin"
move *.iso ..\
move *.nfo ..\
del *.r*
del *.s*
move ..\*.nfo .\