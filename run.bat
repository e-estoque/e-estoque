@echo off
chcp 65001 > nul
set "PROJECT_DIR=%~dp0"
set "GSON=%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.11.0\gson-2.11.0.jar"
powershell -NoProfile -Command "& { $gson = $env:GSON; $out = Join-Path $env:PROJECT_DIR 'target\classes'; $src = Join-Path $env:PROJECT_DIR 'src\main\java'; New-Item -Force -ItemType Directory $out | Out-Null; $files = (Get-ChildItem -Recurse -Filter '*.java' $src).FullName; Write-Host 'Compilando...' -ForegroundColor Cyan; & javac -encoding UTF-8 --release 21 -cp $gson -d $out $files; if ($LASTEXITCODE -eq 0) { Write-Host 'Iniciando sistema...' -ForegroundColor Green; & java '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' -cp ($out + ';' + $gson) com.estoque.Main } else { Write-Host 'Erro na compilacao!' -ForegroundColor Red } }"
pause
