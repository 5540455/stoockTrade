$ErrorActionPreference = 'Stop'
$workspaceRoot = Split-Path $PSScriptRoot -Parent
$env:ANDROID_HOME = Join-Path $workspaceRoot '.tools\android-sdk'
$env:ANDROID_AVD_HOME = Join-Path $workspaceRoot '.tools\avd'
$adb = Join-Path $env:ANDROID_HOME 'platform-tools\adb.exe'
$apk = Join-Path $PSScriptRoot 'app\build\outputs\apk\debug\app-debug.apk'
if (!(Test-Path -LiteralPath $apk)) { throw '请先运行构建.ps1。' }
& $adb start-server
$devices = & $adb devices
if (!($devices -match 'emulator-5554\s+device$')) {
    if (!($devices -match 'emulator-5554\s+')) {
        Start-Process -FilePath (Join-Path $env:ANDROID_HOME 'emulator\emulator.exe') -ArgumentList '-avd InvestmentLedger_API35 -port 5554 -no-snapshot -gpu swiftshader_indirect -no-boot-anim' -WindowStyle Hidden
    }
    Write-Host '正在等待模拟器启动……'
}
$ready = $false
for ($i = 0; $i -lt 120; $i++) {
    $boot = & $adb -s emulator-5554 shell getprop sys.boot_completed 2>$null
    if ($boot -match '^1') { $ready = $true; break }
    Start-Sleep -Seconds 2
}
if (!$ready) { throw '模拟器启动超时。' }
& $adb -s emulator-5554 install -r $apk
if ($LASTEXITCODE -ne 0) { throw '安装失败。' }
& $adb -s emulator-5554 shell input keyevent 82
& $adb -s emulator-5554 shell am start -W -n com.example.investmentledger/.MainActivity
if ($LASTEXITCODE -ne 0) { throw '启动失败。' }
