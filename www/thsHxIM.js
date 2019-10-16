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
 * 启动聊天界面
 * @param toChat 聊天对象
 */
exports.startChat = function (toChat, success, error) {
    exec(success, error, 'thsHxIM', 'startChat', [toChat]);
};