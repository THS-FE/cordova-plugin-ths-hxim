# cordova-plugin-ths-hxim
环信IM cordova插件
## 支持平台

Android

## 安装插件

```
# 通过npm 安装插件
cordova plugin add cordova-plugin-ths-hxim --variable EASEMOB_APPKEY=easemob-demo#chatdemoui
# 通过github安装
cordova plugin add https://github.com/THS-FE/cordova-plugin-ths-hxim  --variable EASEMOB_APPKEY=easemob-demo#chatdemoui
# 通过本地文件路径安装
cordova plugin add 文件路径
```

参数说明：

1. EASEMOB_APPKEY  环信IM注册的APP KEY

**说明： ionic 项目命令前加上ionic，即ionic cordova plugin xxxxx**

## 配置文件修改

在config.xml文件中**platform name="android"**节点下添加以下配置

````xml

````

## 使用方法

登录即时通讯IM

```javascript
  /**
   * 登录即时通讯IM的方法
   * @param userName 用户名
   * @param pwd 密码
   * @param success 成功的回调函数
   */
  loginIM(userName, pwd, success) {
    try {
      cordova.plugins.thsHxIM.loginIM(userName, pwd, success, (err) => {
        console.log('err', err);
        this.commUtilProvider.showToast('即时通讯登录失败！')
      })
    }
    catch (err) {
      console.log(err);
    }
  };
```

退出登录

```java
 /**
   *退出登录
   * @param success 成功的回调函数
   * @param err 失败的回调函数
   */
  logout(success, err) {
    try {
      cordova.plugins.thsHxIM.logout(success, err);
    }
    catch (err) {
      console.log(err);
    }
  };
```

启动聊天界面

```javascript
  /**
   * 启动聊天界面
   * @param toChat 聊天对象
   * @param chattype  * 打开聊天页面，包含单聊，群聊，聊天室，讨论组(暂时不可用)，客服
   * @param success 成功的回调函数
   * public static final int CHATTYPE_SINGLE = 1;
   *     public static final int CHATTYPE_GROUP = 2;
   *     public static final int CHATTYPE_CHATROOM = 3;
   */
  startChat(toChat, chattype, success) {
    cordova.plugins.thsHxIM.startChat(toChat, chattype, success, () => {
      this.commUtilProvider.showToast('聊天界面启动失败！')
    })
  };
```

获取会话列表数据

```javascript
  /**
   * 获取会话列表数据
   * @param success 成功的回调函数
   */
  getConversation(success) {
    cordova.plugins.thsHxIM.getConversation(success, (error) => {
      this.commUtilProvider.showToast('获取会话列表数据失败！')
    });
  };
```

进入添加联系人页面

```javascript
  /**
   * 进入添加联系人页面
   * @param success 成功的回调函数
   */
  startAddContact(success) {
    cordova.plugins.thsHxIM.startAddContact(success, () => {
      this.commUtilProvider.showToast(' 进入添加联系人页面失败！')
    })
  };
```

进入申请与通知页面

```javascript
  /**
   * 进入申请与通知页面
   * @param success 成功的回调函数
   */
  startNewFriendsMsg(success) {
    cordova.plugins.thsHxIM.startNewFriendsMsg(success, () => {
      this.commUtilProvider.showToast('进入申请与通知失败！')
    })
  };
```

进入群聊列表页面

```javascript
  /**
   * 进入群聊列表页面
   * @param success 成功的回调函数
   */
  startGroups(success) {
    cordova.plugins.thsHxIM.startGroups(success, () => {
      this.commUtilProvider.showToast('进入群聊失败！')
    })
  };
```

进入聊天室页面

```javascript
  /**
   * 进入聊天室页面
   * @param success 成功的回调函数
   */
  startPublicChatRooms(success) {
    cordova.plugins.thsHxIM.startPublicChatRooms(success, () => {
      this.commUtilProvider.showToast('进入聊天室失败！')
    })
  };
```

进入音视频会议页面

```javascript
  /**
   * 进入音视频会议页面
   * @param success 成功的回调函数
   */
  startConferenceCall(success) {
    cordova.plugins.thsHxIM.startConferenceCall(success, () => {
      this.commUtilProvider.showToast('进入音视频会议失败！')
    })
  };
```

通过联系人列表进入音视频会议页面

```javascript
  /**
   * 通过联系人列表进入音视频会议页面
   * @param success 成功的回调函数
   * @param members 联系人数组的JSON字符串
   */
  startConferenceCallByMembers(members, success) {
    cordova.plugins.thsHxIM.startConferenceCallByMembers(members, success, () => {
      this.commUtilProvider.showToast('进入音视频会议失败！')
    })
  };
```

获取登录信息

```javascript
  /**
   * 获取登录信息
   */
  getCurrentUser(success) {
    cordova.plugins.thsHxIM.getCurrentUser(success, () => {
      this.commUtilProvider.showToast('获取登录信息失败！')
    });
  };
```

获取未读消息总数

```javascript
  /**
   * 获取未读消息总数
   */
  getUnreadMsgCountTotal(success) {
    cordova.plugins.thsHxIM.getUnreadMsgCountTotal(success, () => {
      this.commUtilProvider.showToast('获取未读消息总数失败！')
    })
  };
```

获取登录状态

```javascript
  /**
   * 获取登录状态
   */
  isLoggedIn(success) {
    cordova.plugins.thsHxIM.isLoggedIn(success, () => {
      this.commUtilProvider.showToast('获取登录状态失败！');
    })
  };
```

将联系人数据写入数据库

```javascript
  /**
   * 将联系人数据写入数据库
   * @param data 待写入的数据
   */
  saveContactList(data, success) {
    cordova.plugins.thsHxIM.saveContactList(data, success, () => {
      this.commUtilProvider.showToast('存入数据失败');
    });
  }
```

存储单个人数据

```javascript
  /**
   * 存储单个人数据
   */
  saveContact(data, success) {
    cordova.plugins.thsHxIM.saveContact(data, success, () => {
      this.commUtilProvider.showToast('联系人数据存入失败');
    });
  };
```

删除单个人数据

```javascript
  /**
   * 删除单个人数据
   */
  deleteContact(data, success) {
    cordova.plugins.thsHxIM.deleteContact(data, success, () => {
      this.commUtilProvider.showToast('联系人数据删除失败');
    });
  };
```







**说明：使用ts 进行开发时，需要在文件上变声明下declare const cordova，不然会报错;**

```typescript
import { Component, OnInit, Input } from '@angular/core';
import { WebIntent } from '@ionic-native/web-intent/ngx';
declare let cordova;
@Component({
  selector: 'app-explore-container',
  templateUrl: './explore-container.component.html',
  styleUrls: ['./explore-container.component.scss'],
})
```

## 常见错误

### 打包报错  unbound prefix.

```
Execution failed for task ':app:mergeDebugResources'.
> java.util.concurrent.ExecutionException: com.android.builder.internal.aapt.v2.Aapt2Exception: Android resource compilation failed   
  D:\training\20200521\Test20200521\platforms\android\app\src\main\res\xml\config.xml:46: error: unbound prefix.
```

修改config.xml,添加 xmlns:android="http://schemas.android.com/apk/res/android"

```xml
<widget id="io.ionic.starter" version="0.0.1" xmlns="http://www.w3.org/ns/widgets" xmlns:android="http://schemas.android.com/apk/res/android" xmlns:cdv="http://cordova.apache.org/ns/1.0">
```
