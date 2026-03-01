<img align="left" width="80" height="80" src="github/mauth.png"
alt="App icon">

# Mauth

[![CI](https://img.shields.io/github/actions/workflow/status/X1nto/Mauth/build.yml?branch=master&color=blue&style=for-the-badge)](https://github.com/X1nto/Mauth/actions?query=branch%3Amaster)
[![F-Droid](https://img.shields.io/f-droid/v/com.xinto.mauth.svg?logo=F-Droid&color=green&style=for-the-badge)](https://f-droid.org/en/packages/com.xinto.mauth)
[![Releases](https://img.shields.io/github/release/X1nto/Mauth.svg?logo=github&color=171515&style=for-the-badge)](https://github.com/X1nto/Mauth/releases)

Mauth 是一款支持 TOTP 和 HOTP 的两步验证（2FA）应用，完全兼容 Google Authenticator。

Logo 由 [@wingio](https://github.com/wingio) 设计

# 设计灵感
市面上虽然有很多 2FA 验证应用，但大多数 UI/UX 已经过时，功能也比较简单。Mauth 旨在提供尽可能直观、功能丰富的体验，同时拥有精美的 Material You 界面风格。

# 功能特性
- 安全
  - [x] 生物识别
  - [x] PIN 密码
- 添加账户方式
  - [x] 扫描二维码
    - [x] 使用摄像头
    - [x] 从图片导入
  - [x] 手动输入
  - [x] 深度链接（Deeplinks）
- 算法支持
  - [x] TOTP（基于时间的一次性密码）
  - [x] HOTP（基于计数器的一次性密码）
- 账户管理
  - [ ] 搜索
  - [x] 排序
  - [ ] 分组
  - [x] 编辑
  - [x] 删除
- 导出
  - [x] 二维码
    - [x] 单个账户
    - [x] 批量导出（Google Authenticator 格式）
  - [x] otpauth:// URI
  - [ ] 加密数据
- 导入
  - [x] Google Authenticator
  - [ ] Authy
  - [ ] Aegis（可通过"导出为 Google Authenticator 格式"间接导入）
  - [ ] Microsoft Authenticator
  - [x] 其他支持导出为 Google Authenticator 格式的验证器

# 截图
<img width=200 alt="验证界面" 
src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.png?raw=true">
<img width=200 alt="主界面（空）" 
src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.png?raw=true">
<img width=200 alt="添加账户对话框" 
src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.png?raw=true">
<img width=200 alt="添加账户界面" 
src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.png?raw=true">
<img width=200 alt="主界面（有账户）" 
src="fastlane/metadata/android/en-US/images/phoneScreenshots/5.png?raw=true">
<img width=200 alt="编辑账户界面" 
src="fastlane/metadata/android/en-US/images/phoneScreenshots/6.png?raw=true">
<img width=200 alt="排序选项" 
src="fastlane/metadata/android/en-US/images/phoneScreenshots/7.png?raw=true">
<img width=200 alt="账户选择" 
src="fastlane/metadata/android/en-US/images/phoneScreenshots/8.png?raw=true">
<img width=200 alt="设置界面"
src="fastlane/metadata/android/en-US/images/phoneScreenshots/9.png?raw=true">
<img width=200 alt="关于界面"
src="fastlane/metadata/android/en-US/images/phoneScreenshots/10.png?raw=true">

# 下载
Mauth 可在 F-Droid 和 GitHub Releases 页面下载。  
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" height="75">](https://f-droid.org/en/packages/com.xinto.mauth)
[<img src="github/get_it_on_github.png" height="75">](https://github.com/X1nto/Mauth/releases)

# 贡献

翻译请访问 https://toolate.othing.xyz/projects/mauth/

# 开源协议
```
Mauth 是自由软件：您可以根据自由软件基金会发布的 GNU 通用公共许可证（第 3 版或更新版本）的条款重新分发和/或修改本软件。

本程序按"原样"分发，不附带任何明示或暗示的保证，包括但不限于对适销性和特定用途适用性的隐含保证。详情请参阅 GNU 通用公共许可证。

您应该已随本程序收到一份 GNU 通用公共许可证副本。如果没有，请访问 <https://www.gnu.org/licenses/>。
```
