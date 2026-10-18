$ErrorActionPreference = 'Stop'
$projectRoot = $PSScriptRoot
$workspaceRoot = Split-Path $projectRoot -Parent
$env:JAVA_HOME = 'C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot'
$env:ANDROID_HOME = Join-Path $workspaceRoot '.tools\android-sdk'
$env:GRADLE_USER_HOME = Join-Path $workspaceRoot '.tools\gradle-cache'
& (Join-Path $workspaceRoot '.tools\gradle-8.7\bin\gradle.bat') -p $projectRoot assembleDebug --console=plain
if ($LASTEXITCODE -ne 0) { throw '编译失败，请检查上方日志。' }
Write-Host "安装包：$projectRoot\app\build\outputs\apk\debug\app-debug.apk"
