$ErrorActionPreference = "Stop"

$gson = "$env:USERPROFILE\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar"
$src  = Join-Path $PSScriptRoot "src\main\java"
$out  = Join-Path $PSScriptRoot "target\classes"

New-Item -ItemType Directory -Force $out | Out-Null

Write-Host "Compilando..." -ForegroundColor Cyan
$files = Get-ChildItem -Recurse -Filter "*.java" $src | Select-Object -ExpandProperty FullName
& javac -encoding UTF-8 -cp $gson -d $out $files

if ($LASTEXITCODE -ne 0) {
    Write-Host "Erro na compilacao!" -ForegroundColor Red
    exit 1
}

Write-Host "Iniciando sistema...`n" -ForegroundColor Green
& java -cp "$out;$gson" com.estoque.Main
