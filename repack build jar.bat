@echo off
color 0a
"C:\Program Files\7-Zip\7z" a -sdel tmp.null build
del tmp.null
del crackhub_client.jar
cd dist
if not exist README.TXT (
    exit
)
..\..\..\__NBP_tools_dir__\advzip.exe -z -3 crackhub_client.jar
move crackhub_client.jar ..\crackhub_client.jar
cd..
"C:\Program Files\7-Zip\7z" a -sdel tmp.null dist
del tmp.null
del .\*.html
del .\*.nfo
del .\*.bin