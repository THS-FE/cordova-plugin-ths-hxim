package cn.com.ths.hx.im;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.DemoApplication;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.ui.ChatActivity;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.DateUtils;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class echoes a string called from JavaScript.
 */
public class thsHxIM extends CordovaPlugin {
    public static final  String TAG = "thsHxIM-CordovaPlugin";
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // 登录即时通讯服务器
        if (action.equals("loginIM")) {
            //userName,pwd
            String userName = args.getString(0);
            String pwd = args.getString(1);
            this.loginIM( userName, pwd, callbackContext);
            return true;
         // 退出登录
        }else  if(action.equals("logout")){
            this.logout(callbackContext);
            return true;
         // 开启聊天界面
        }else if(action.equals("startChat")){
            String toChat = args.getString(0);
            int chatType = args.getInt(1);
            this.startChat(toChat, chatType, callbackContext);
            return true;
         // 获取会话列表
        }else if(action.equals("getConversation")){
            List<EMConversation> msgList = loadConversationList();
            if(msgList.size() ==0){
                callbackContext.success("[]");
            }else {
                String  cvs = getConversationToJSON(msgList);
                callbackContext.success(cvs);
            }
        }
        return false;
    }

    /**
     * 打开聊天页面，包含单聊，群聊，聊天室，讨论组(暂时不可用)，客服
     * public static final int CHATTYPE_SINGLE = 1;
     *     public static final int CHATTYPE_GROUP = 2;
     *     public static final int CHATTYPE_CHATROOM = 3;
     */
    public void  startChat(String toChat,int chatType, CallbackContext callbackContext){
        if (toChat.equals(EMClient.getInstance().getCurrentUser())) {
            callbackContext.success(com.hyphenate.chatuidemo.R.string.Cant_chat_with_yourself);
            // Toast.makeText(cordova.getActivity(), com.hyphenate.chatuidemo.R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
        } else {
            // start chat acitivity
            Intent intent = new Intent(cordova.getActivity(), ChatActivity.class);
//            if(conversation.isGroup()){
//                if(conversation.getType() == EMConversation.EMConversationType.ChatRoom){
//                    // it's group chat
//                    intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
//                }else{
//                    intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
//                }
//
//            }
//            if(type == EMConversation.EMConversationType.ChatRoom){
//                intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
//            }else if(type == EMConversation.EMConversationType.GroupChat){
//                intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
//            }else {
//                intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE);
//            }
            intent.putExtra(Constant.EXTRA_CHAT_TYPE, chatType);
            // it's single chat
            intent.putExtra(Constant.EXTRA_USER_ID, toChat);
            cordova.getActivity().startActivity(intent);
            callbackContext.success("success");
        }
    }
    /**
     * 退出登录
     */
    private  void logout(CallbackContext callbackContext){
        DemoHelper.getInstance().logout(true,new EMCallBack() {

            @Override
            public void onSuccess() {
                callbackContext.success("success");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                try {
                    JSONObject errObj = new JSONObject();
                    errObj.put("message",message);
                    errObj.put("code",code);
                    callbackContext.error(errObj.toString());
                }catch (JSONException e){
                    callbackContext.error(e.getMessage());
                }

            }
        });
    }
    /**
     * 登录IM服务器
     */
    private void loginIM(String currentUsername, String currentPassword,CallbackContext callbackContext){
        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(currentUsername);

        final long start = System.currentTimeMillis();
        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");


                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().pushManager().updatePushNickname(
                        DemoApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

//                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
//                    pd.dismiss();
//                }

                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

//                Intent intent = new Intent(LoginActivity.this,
//                        com.hyphenate.chatuidemo.ui.MainActivity.class);
//                startActivity(intent);
//
//                finish();
                callbackContext.success("success");
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                try {
                    JSONObject errObj = new JSONObject();
                    errObj.put("message",message);
                    errObj.put("code",code);
                    callbackContext.error(errObj.toString());
                }catch (JSONException e){
                    callbackContext.error(e.getMessage());
                }


            }
        });
    }
    private String getConversationToJSON(List<EMConversation> msgList) throws  JSONException{
        JSONArray jsonArray =new JSONArray();
        for (EMConversation conversation: msgList){

            cn.com.ths.hxchattest.MsgModel msg =new cn.com.ths.hxchattest.MsgModel();
            // get username or group id
            String username = conversation.conversationId();
            msg.setType(conversation.getType());
            if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                String groupId = conversation.conversationId();
                msg.setMotioned(EaseAtMessageHelper.get().hasAtMeMsg(groupId));
                // group message, show group avatar
                EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                msg.setName(group != null ? group.getGroupName() : username);
            } else if(conversation.getType() == EMConversation.EMConversationType.ChatRoom){

                EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
                msg.setName(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
                msg.setMotioned(false);
            }else {
//                EaseUserUtils.setUserAvatar(getContext(), username, holder.avatar);
                msg.setName(EaseUserUtils.getThsUserNick(username));
                msg.setMotioned(false);
            }
            if (conversation.getUnreadMsgCount() > 0) {
                // show unread message count
                msg.setUnreadLabel(String.valueOf(conversation.getUnreadMsgCount()));
            }

            if (conversation.getAllMsgCount() != 0) {
                // show the content of latest message
                EMMessage lastMessage = conversation.getLastMessage();
                String content = null;
//                if(cvsListHelper != null){
//                    content = cvsListHelper.onSetItemSecondaryText(lastMessage);
//                }
                msg.setMessage(EaseCommonUtils.getMessageDigest(lastMessage, cordova.getActivity()));
//                holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))),
//                        TextView.BufferType.SPANNABLE);
                msg.setTime(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
                msg.setMsgState(lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL);
            }
                JSONObject obj = new JSONObject(msg.toString());
                jsonArray.put(obj);

        }
        return  jsonArray.toString();
    }
    /**
     * load conversation list
     *
     * @return
    +    */
    protected List<EMConversation> loadConversationList(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }
    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
}
