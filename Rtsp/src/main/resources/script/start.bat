@echo off
chcp 65001

:loop

REM cvediart 프로세스가 실행 중인지 확인
tasklist | findstr "cvediart.exe"
if errorlevel 1 (
  E:
  cd Tools\Cvedia-2023.5.1
  start CVEDIA-RT
  echo Cvedia 서버를 실행합니다.
) else (
  echo Cvedia 서버가 이미 실행 중입니다.
)

REM 60초 대기
timeout /t 10 /nobreak
echo 대기중 ...

goto loop