<img align="left" width="80" height="80" src="github/mauth.png" alt="App icon">

# Mauth

[![CI](https://img.shields.io/github/actions/workflow/status/Piktowo/Mauth/build.yml?branch=master&color=blue&style=for-the-badge)](https://github.com/Piktowo/Mauth/actions?query=branch%3Amaster)
[![Releases](https://img.shields.io/github/release/Piktowo/Mauth.svg?logo=github&color=171515&style=for-the-badge)](https://github.com/Piktowo/Mauth/releases)

Mauth 是一款支持 TOTP / HOTP 的 2FA 验证器，兼容 Google Authenticator 协议。  
这是基于原项目持续修改的分支版本，UI、备份能力和本地数据安全策略均已做较大调整。

Logo 由 [@wingio](https://github.com/wingio) 设计。

## 与原版的主要差异
- 界面风格由原版的 Material You 方向，调整为以 Miuix 风格为主的交互与视觉。
- 新增 `WebDAV` 备份与恢复能力。
- 新增本地文件备份与恢复能力（导出文件 / 从文件导入）。
- 本地数据库改为 SQLCipher 加密存储，并增加明文数据库自动加密迁移逻辑。

## 功能特性
- 安全
- [x] 生物识别解锁
- [x] PIN 保护
- [x] 应用内防截屏（Secure mode）
- [x] SQLCipher 数据库加密（AES-256）

- 添加账户
- [x] 扫描二维码（摄像头）
- [x] 从图片识别二维码
- [x] 手动输入
- [x] 解析 `otpauth://` 深度链接

- OTP 能力
- [x] TOTP
- [x] HOTP
- [x] 兼容 Google Authenticator 导入/导出格式

- 备份与恢复
- [x] 本地文件备份（导出到文件）
- [x] 本地文件恢复（从文件导入）
- [x] WebDAV 备份
- [x] WebDAV 恢复
- [x] 保留二维码导出（用于迁移到其他验证器）

## 本地安全说明
- 数据库使用 SQLCipher，密钥为每台设备首次启动时随机生成的 32 字节密钥。
- 数据库密钥保存在 `EncryptedSharedPreferences` 中，并由 Android Keystore 保护。
- 对旧版本可能存在的明文 SQLite 数据库，应用会在启动时尝试自动迁移为加密数据库。
- 目标是避免数据库以明文形式长期存储在设备上。

## 截图
<img width=200 alt="验证界面" src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.png?raw=true">
<img width=200 alt="主界面（空）" src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.png?raw=true">
<img width=200 alt="添加账户对话框" src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.png?raw=true">
<img width=200 alt="添加账户界面" src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.png?raw=true">
<img width=200 alt="主界面（有账户）" src="fastlane/metadata/android/en-US/images/phoneScreenshots/5.png?raw=true">
<img width=200 alt="编辑账户界面" src="fastlane/metadata/android/en-US/images/phoneScreenshots/6.png?raw=true">
<img width=200 alt="排序选项" src="fastlane/metadata/android/en-US/images/phoneScreenshots/7.png?raw=true">
<img width=200 alt="账户选择" src="fastlane/metadata/android/en-US/images/phoneScreenshots/8.png?raw=true">
<img width=200 alt="设置界面" src="fastlane/metadata/android/en-US/images/phoneScreenshots/9.png?raw=true">
<img width=200 alt="关于界面" src="fastlane/metadata/android/en-US/images/phoneScreenshots/10.png?raw=true">


## 开源协议
本项目基于 GNU General Public License v3.0（或更高版本）发布。  
详见仓库中的 [LICENSE](LICENSE)。
