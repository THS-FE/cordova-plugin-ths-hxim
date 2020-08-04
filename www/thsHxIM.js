var exec = require('cordova/exec');

/**
 * 登录即时通讯IM
 * @param {String} userName 用户名
 * @param {String} pwd 密码
 * @param {Function} success 成功回调
 * @param {Function} error 失败回调
 */
exports.loginIM = function (userName, pwd, success, error) {
    exec(success, error, 'thsHxIM', 'loginIM', [userName,pwd]);
};

/**
 * 退出登录
 * @param {Function} success 
 * @param {Function} error 
 */
exports.logout = function ( success, error) {
    exec(success, error, 'thsHxIM', 'logout', []);
};

/**
 * 启动聊天界面
 * @param toChat 聊天对象
 * @param chattype  * 打开聊天页面，包含单聊，群聊，聊天室，讨论组(暂时不可用)，客服
     * public static final int CHATTYPE_SINGLE = 1;
     *     public static final int CHATTYPE_GROUP = 2;
     *     public static final int CHATTYPE_CHATROOM = 3;
 */
/**
 * 启动聊天面板界面
 * @param {String} toChat 聊天对象
 * @param {Int} chattype 打开聊天页面，包含单聊，群聊，聊天室，讨论组(暂时不可用)，客服
     * public static final int CHATTYPE_SINGLE = 1;
     *     public static final int CHATTYPE_GROUP = 2;
     *     public static final int CHATTYPE_CHATROOM = 3;
 * @param {Function} success 
 * @param {Function} error 
 */
exports.startChat = function (toChat,chattype, success, error) {
    exec(success, error, 'thsHxIM', 'startChat', [toChat,chattype]);
};

/**
 * 获取会话
 * @param {Function} success 
 * @param {Function} error 
 */
exports.getConversation = function (success, error) {
    exec(success, error, 'thsHxIM', 'getConversation', []);
};

/**
 * 进入添加联系人页面
 * @param {Function} success 
 * @param {Function} error 
 */
exports.startAddContact = function (success, error) {
    exec(success, error, 'thsHxIM', 'startAddContact', []);
};

/**
 * 进入申请与通知页面
 * @param {Function} success 
 * @param {Function} error 
 */
exports.startNewFriendsMsg = function (success, error) {
    exec(success, error, 'thsHxIM', 'startNewFriendsMsg', []);
};

/**
 * 进入群聊列表页面
 * @param {Function} success 
 * @param {Function} error 
 */
exports.startGroups = function (success, error) {
    exec(success, error, 'thsHxIM', 'startGroups', []);
};

/**
 * 进入聊天室页面
 * @param {Function} success 
 * @param {Function} error 
 */
exports.startPublicChatRooms = function (success, error) {
    exec(success, error, 'thsHxIM', 'startPublicChatRooms', []);
};

/**
 * 进入音视频会议页面
 * @param {Function} success 
 * @param {Function} error 
 */
exports.startConferenceCall = function (success, error) {
    exec(success, error, 'thsHxIM', 'startConferenceCall', []);
};

/**
 * 进入音视频会议页面
 * @param {*} members 成员列表
 * @param {*} success 
 * @param {*} error 
 */
exports.startConferenceCallByMembers = function (members,success, error) {
    exec(success, error, 'thsHxIM', 'startConferenceCallByMembers', [members]);
};

/**
 * 获取未读消息总数
 * @param {*} success 
 * @param {*} error 
 */
exports.getUnreadMsgCountTotal  = function (success, error) {
    exec(success, error, 'thsHxIM', 'getUnreadMsgCountTotal', []);
};
/**
 * 获取登录状态
 * @param {*} success 
 * @param {*} error 
 */
exports.isLoggedIn  = function (success, error) {
    exec(success, error, 'thsHxIM', 'isLoggedIn', []);
};

/**
 * 获取当前登录用户
 * @param {*} success 
 * @param {*} error 
 */
exports.getCurrentUser = function(success, error){
exec(success, error, 'thsHxIM', 'getCurrentUser', []);
};


/**
 * 存储联系人数据
 * @param {*} data  多个联系人数据
 * @param {*} success 
 * @param {*} error 
 */
exports.saveContactList = function(data,success, error){
  exec(success, error, 'thsHxIM', 'saveContactList', [data]);
};

/**
 *  存储单个人数据
 * @param {*} data 单个联系人数据
 * @param {*} success 
 * @param {*} error 
 */
exports.saveContact = function(data,success, error){
  exec(success, error, 'thsHxIM', 'saveContact', [data]);
};

/**
 * 删除单个联系人
 * @param {*} data 
 * @param {*} success 
 * @param {*} error 
 */
exports.deleteContact = function(data,success, error){
  exec(success, error, 'thsHxIM', 'deleteContact', [data]);
};

/**
 * 跟指定人发起语音电话
 * @param {String} username 注册进入环信的用户ID
 * @param {Function} success 　启动成功
 * @param {Function} error 启动失败
 */
exports.startVoiceCall = function(username,success, error){
    exec(success, error, 'thsHxIM', 'startVoiceCall', [username]);
};

/**
 * 跟指定人发起视频电话
 * @param {String} username 注册进入环信的用户ID
 * @param {Function} success 　启动成功
 * @param {Function} error 启动失败
 */
exports.startVideoCall = function(username,success, error){
    exec(success, error, 'thsHxIM', 'startVideoCall', [username]);
};



//收到消息刷新UI界面
exports.refreshUIWithMessageInAndroidCallback = function(data) {
    //data = JSON.stringify(data)
   //console.log('refreshUIWithMessageInAndroidCallback' + data);
   cordova.fireDocumentEvent('hxim.refreshUIWithMessage', data);
};
// 网路状态监听回调
exports.onDisconnectedReceiverInAndroidCallback = function(data) {
    data = JSON.stringify(data);
    data = JSON.parse(data);
   //console.log('onDisconnectedReceiverInAndroidCallback' + data);
   cordova.fireDocumentEvent('hxim.onDisconnectedReceiver', data);
};
// 联系人更新
exports.contactUpdateInAndroidCallback = function(data) {
   //data = JSON.stringify(data)
  // console.log('contactUpdateInAndroidCallback' + data);
   cordova.fireDocumentEvent('hxim.contactUpdate', data);
};








