# GPS速度插件
## 功能列表
1. GPS车速显示（桌面插件，悬浮窗口）
2. 行车轨迹记录（可关闭）
3. 智能巡航（电子狗，语音提醒，实时路况，可关闭）
4. 悬浮窗口速度显示（可关闭）

## 使用方法
### 桌面插件
   1. 拖放GPS速度插件到桌面
   2. 在弹出的配置窗口，允许程序权限
   3. 按顺序勾选启动功能
   4. 点ok，
   5. 点桌面插件可以启动关闭程序功能
   
   桌面插件可以开机自动启动
   
 ### 只使用悬浮窗口
 1. 打开GPS速度插件主程序
 2. 点开配置 窗口，允许程序权限
 3. 按顺序勾选启动功能
 4. 关闭悬浮窗口到主程序配置窗口关闭
 悬浮窗口可以选择是否在桌面显示
 
 
 ## GPS轨迹记录后台服务
 
 ###程序： 
后台源码文件位置：
/sdcard/GPS/GPSHistory.js

部署方法：
1. 在服务器安装 nodejs
2. 使用nodejs 启动服务
3. 服务器开放8090端口
4. 在主窗口的配置窗口设置 后台服务器地址

### 后台服务启动方法
```$bash
npm install express querystring node-cache sqlite3 moment
//node GPSHistory.js
pm2 start GPSHistory.js 
```

## 改动记录
### 20180313
1. 增加配置后台服务器地址
2. 增加在桌面也显示悬浮窗口选项
3. 在apk上传后台服务器程序
4. 桌面插件超速时数字变红色
5. 语音改为3G，4G，wifi使用在线，其它使用离线
6. 配置窗口bug修复 @%飞翔%
7. 改桌面插件界面 只显示kmh 

### 20180314 1.2
1. 增加新的桌面插件 数字显示
2. 新增悬浮窗口设置项
3. 完善仪表插件效果
4. 修改后台服务，在主窗口可以输入机器ID查询其它设备的行车轨迹
5. 优化轨迹显示逻辑
6. 没有启动数据上传功能时，轨迹数据从本机数据库提取   

### 20180315 1.3
1. 增加速度调整设置
2. 悬浮窗口支持自由移动
3. 悬浮窗口可以选择否显限速或速度窗口
4. 超速单独语音提醒：超速每分钏提醒一次
5. 修复语音重复的bug
6. 语音设置成与音乐同时播放
7. 数字表插件美化
8. 修复两个插件开机启动异常
