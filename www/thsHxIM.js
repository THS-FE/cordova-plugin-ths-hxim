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

