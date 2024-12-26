@echo off
color 0a
cd dist
if not exist README.TXT (
    exit
)
cd..
"C:\Program Files\7-Zip\7z" a -sdel tmp.null build
del tmp.null
del crackhub_client.jar
cd dist
mkdir rep
cd rep
for %%i in (..\lib\*.jar) do "C:\Program Files\7-Zip\7z" x "%%i"
"C:\Program Files\7-Zip\7z" a -sdel tmp.null META-INF
del tmp.null
"C:\Program Files\7-Zip\7z" x "..\crackhub_client.jar"
"C:\Program Files\7-Zip\7z" a -tzip ..\crackhub_client.jar .\*
cd..
..\..\..\__NBP_tools_dir__\advzip.exe -z -3 crackhub_client.jar
del README.TXT
"C:\Program Files\7-Zip\7z" a -sdel tmp.null rep
del tmp.null
"C:\Program Files\7-Zip\7z" a -sdel tmp.null lib
del tmp.null
move crackhub_client.jar ..\crackhub_client.jar
cd..
"C:\Program Files\7-Zip\7z" a -sdel tmp.null dist
del tmp.null
del .\*.html
del .\*.nfo
del .\*.bin