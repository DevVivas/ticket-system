@echo off
echo Iniciando todos los microservicios...

start cmd /k "cd /d %~dp0ms-recintos\recintos && mvnw.cmd spring-boot:run"
start cmd /k "cd /d %~dp0ms-eventos\eventos && mvnw.cmd spring-boot:run"
start cmd /k "cd /d %~dp0ms-tickets\tickets && mvnw.cmd spring-boot:run"
start cmd /k "cd /d %~dp0ms-ventas\ventas && mvnw.cmd spring-boot:run"
start cmd /k "cd /d %~dp0ms-validacion\validacion && mvnw.cmd spring-boot:run"
start cmd /k "cd /d %~dp0ms-artistas\artistas && mvnw.cmd spring-boot:run"
start cmd /k "cd /d %~dp0ms-preventa\preventa && mvnw.cmd spring-boot:run"
start cmd /k "cd /d %~dp0ms-devoluciones\devoluciones && mvnw.cmd spring-boot:run"
start cmd /k "cd /d %~dp0ms-promotores\promotores && mvnw.cmd spring-boot:run"
start cmd /k "cd /d %~dp0ms-streaming\streaming && mvnw.cmd spring-boot:run"

echo Todos los microservicios iniciando...