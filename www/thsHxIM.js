var exec = require('cordova/exec');
/**
 * 登录即时通讯IM的方法
 * @param userName 用户名
 * @param pwd 密码
 */
exports.loginIM = function (userName, pwd, success, error) {
    exec(success, error, 'thsHxIM', 'loginIM', [userName,pwd]);
};

/**
 *退出登录
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
exports.startChat = function (toChat,chattype, success, error) {
    exec(success, error, 'thsHxIM', 'startChat', [toChat,chattype]);
};

/**
 * 获取会话列表数据
 */
exports.getConversation = function (success, error) {
    exec(success, error, 'thsHxIM', 'getConversation', []);
};
/**
 * 进入添加联系人页面
 */
exports.startAddContact = function (success, error) {
    exec(success, error, 'thsHxIM', 'startAddContact', []);
};
/**
 * 进入申请与通知页面
 */
exports.startNewFriendsMsg = function (success, error) {
    exec(success, error, 'thsHxIM', 'startNewFriendsMsg', []);
};
/**
 * 进入群聊列表页面
 */
exports.startGroups = function (success, error) {
    exec(success, error, 'thsHxIM', 'startGroups', []);
};
/**
 * 进入聊天室页面
 */
exports.startPublicChatRooms = function (success, error) {
    exec(success, error, 'thsHxIM', 'startPublicChatRooms', []);
};
/**
 * 进入音视频会议页面
 */
exports.startConferenceCall = function (success, error) {
    exec(success, error, 'thsHxIM', 'startConferenceCall', []);
};
/**
 * 进入音视频会议页面
 */
exports.startConferenceCallByMembers = function (members,success, error) {
    exec(success, error, 'thsHxIM', 'startConferenceCallByMembers', [members]);
};

/**
 * 获取未读消息总数
 */
exports.getUnreadMsgCountTotal  = function (success, error) {
    exec(success, error, 'thsHxIM', 'getUnreadMsgCountTotal', []);
};
/**
 * 获取登录状态
 */
exports.isLoggedIn  = function (success, error) {
    exec(success, error, 'thsHxIM', 'isLoggedIn', []);
};

exports.getCurrentUser = function(success, error){
exec(success, error, 'thsHxIM', 'getCurrentUser', []);
};
/**
 * 存储数据
 */
exports.saveContactList = function(data,success, error){
  exec(success, error, 'thsHxIM', 'saveContactList', [data]);
};

/**
 * 存储单个人数据
 */
exports.saveContact = function(data,success, error){
  exec(success, error, 'thsHxIM', 'saveContact', [data]);
};
/**
 * 删除单个人数据
 */
exports.deleteContact = function(data,success, error){
  exec(success, error, 'thsHxIM', 'deleteContact', [data]);
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








