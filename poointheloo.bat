@echo off
del .\*.html
del .\*.nfo
del .\*.bin

"..\..\__NBP_tools_dir__\sonar-scanner-5.0.1.3006-windows\bin\sonar-scanner.bat" -D"sonar.projectKey=default2" -D"sonar.sources=." -D"sonar.host.url=http://127.0.0.1:9000" -D"sonar.token=sqp_e49c48c9d8e8558a6625b967f967a9930dac0727"
pause