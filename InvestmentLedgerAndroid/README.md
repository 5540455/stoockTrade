# Android 投资账本

## 本机已验证的运行方式

2026年10月18日已完成命令行开发环境安装、APK 编译和 Android 35 模拟器交互验证。Android Studio 图形编辑器尚未安装；本机已具备构建和模拟器调试所需工具。

在本目录打开 PowerShell：

```powershell
powershell -ExecutionPolicy Bypass -File .\构建.ps1
powershell -ExecutionPolicy Bypass -File .\运行.ps1
```

工具安装在上级目录的 `.tools/`，未修改系统全局环境变量。脚本中的 Java 路径对应本机已安装的 Microsoft JDK 21。Java 与 Kotlin 字节码目标统一为 17。

安装包位于 `app/build/outputs/apk/debug/app-debug.apk`。详细结果见上级目录 `docs/开发环境与效果验证.md`，实际截图见 `docs/验证截图/`。

以下 Android Studio 步骤是可选的编辑器使用方式。

## 本机已验证的运行方式

2026年10月18日已完成命令行开发环境安装、APK 编译和 Android 35 模拟器交互验证。Android Studio 图形编辑器尚未安装；本机已具备构建和模拟器调试所需工具。

在本目录打开 PowerShell：

```powershell
powershell -ExecutionPolicy Bypass -File .\构建.ps1
powershell -ExecutionPolicy Bypass -File .\运行.ps1
```

工具安装在上级目录的 `.tools/`，未修改系统全局环境变量。脚本中的 Java 路径对应本机已安装的 Microsoft JDK 21。Java 与 Kotlin 字节码目标统一为 17。

安装包位于 `app/build/outputs/apk/debug/app-debug.apk`。详细结果见上级目录 `docs/开发环境与效果验证.md`，实际截图见 `docs/验证截图/`。

以下 Android Studio 步骤是可选的编辑器使用方式。

这是可在 Windows + Android Studio 中运行的 Kotlin + Jetpack Compose Android 版本。

## 运行方式

1. 安装 Android Studio Ladybug 或更高版本，并安装 Android SDK 35。
2. 在 Android Studio 中打开本目录 `InvestmentLedgerAndroid`。
3. 等待 Gradle 同步完成。
4. 创建或启动 Android Emulator（建议 Pixel 6，API 35）。
5. 点击 Run 运行 `app`。

应用包含首页、记账、持仓、分析、我的五个页面；记账页右下角可以新增交易，数据在当前运行期间保存在内存中。
