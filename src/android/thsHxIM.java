package cn.com.ths.hx.im;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMClientListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.DemoApplication;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.HMSPushHelper;
import com.hyphenate.chatuidemo.conference.ConferenceActivity;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsManager;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsResultAction;
import com.hyphenate.chatuidemo.ui.AddContactActivity;
import com.hyphenate.chatuidemo.ui.ChatActivity;
import com.hyphenate.chatuidemo.ui.GroupsActivity;
import com.hyphenate.chatuidemo.ui.LoginActivity;
import com.hyphenate.chatuidemo.ui.NewFriendsMsgActivity;
import com.hyphenate.chatuidemo.ui.PublicChatRoomsActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.DateUtils;
import com.hyphenate.chatuidemo.ui.VideoCallActivity;
import com.hyphenate.chatuidemo.ui.VoiceCallActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
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
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver internalDebugReceiver;
    private BroadcastReceiver  onDisconnectedReceiver;
    private static thsHxIM instance;
    public thsHxIM() {
        instance = this;
    }
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        requestPermissions();
        Log.e(TAG,"initialize");
        DemoHelper.getInstance().initHandler(cordova.getActivity().getMainLooper());
        //注册各种事件监听
        registerBroadcastReceiver();

        EMClient.getInstance().contactManager().setContactListener(new thsHxIM.MyContactListener());
        EMClient.getInstance().addClientListener(clientListener);
        EMClient.getInstance().addMultiDeviceListener(new thsHxIM.MyMultiDeviceListener());
        //debug purpose only
        registerInternalDebugReceiver();

        // 获取华为 HMS 推送 token
        HMSPushHelper.getInstance().getHMSToken(cordova.getActivity());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = cordova.getActivity().getPackageName();
            PowerManager pm = (PowerManager) cordova.getActivity().getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                try {
                    //some device doesn't has activity to handle this intent
                    //so add try catch
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    cordova.getActivity().startActivity(intent);
                } catch (Exception e) {
                }
            }
        }
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(cordova.getActivity(), new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");
        if (DemoHelper.getInstance().isLoggedIn()) {
            EMClient.getInstance().chatManager().loadAllConversations();
            EMClient.getInstance().groupManager().loadAllGroups();
        }
    }
    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        Log.e(TAG,"onPause");
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        EMClient.getInstance().removeClientListener(clientListener);
        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.popActivity(cordova.getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
        unregisterBroadcastReceiver();

        try {
            cordova.getActivity().unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
        instance = null;
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        Log.e(TAG,"onResume");
        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.pushActivity(cordova.getActivity());

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

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
            return true;
            //进入添加联系人页面
        }else if(action.equals("startAddContact")){
            cordova.getActivity().startActivity(new Intent(cordova.getActivity(), AddContactActivity.class));
            callbackContext.success("success");
            return true;
           //进入申请与通知页面
        }else if(action.equals("startNewFriendsMsg")){
            cordova.getActivity().startActivity(new Intent(cordova.getActivity(), NewFriendsMsgActivity.class));
            callbackContext.success("success");
            return true;
            //进入群聊列表页面
        }else if(action.equals("startGroups")){
            cordova.getActivity().startActivity(new Intent(cordova.getActivity(), GroupsActivity.class));
            callbackContext.success("success");
            return true;
            //进入聊天室页面
        }else if(action.equals("startPublicChatRooms")){
            cordova.getActivity().startActivity(new Intent(cordova.getActivity(), PublicChatRoomsActivity.class));
            callbackContext.success("success");
            return true;
            //进入音视频会议页面
        }else if(action.equals("startConferenceCall")){
            ConferenceActivity.startConferenceCall(cordova.getActivity(),null);
            callbackContext.success("success");
            return true;
            //开启音视频会议页面，选完人
        }else if(action.equals("startConferenceCallByMembers")){
            String members = args.getString(0);
            JSONArray array = new JSONArray(members);
            if(array!=null){
                int len = array.length();
                String mA[] = new String[len];
                for (int i = 0; i<len; i++){
                    String member =array.getString(i);
                    mA[i] = member;
                }
                ConferenceActivity.startConferenceCallByMembers(cordova.getActivity(),mA);
                callbackContext.success("success");
            }else{
                callbackContext.error("error");
            }

            return true;
            //获取消息总数
        }else if(action.equals("getUnreadMsgCountTotal")){
            int count = getUnreadMsgCountTotal();
            callbackContext.success(count);
            return true;
            // 获取是否是登录状态
        }else if(action.equals("isLoggedIn")){
           boolean isLoggedIn = DemoHelper.getInstance().isLoggedIn();
            callbackContext.success(isLoggedIn+"");
            return true;
            // 获取当前user
        }else if(action.equals("getCurrentUser")){
            String  currentUser = EMClient.getInstance().getCurrentUser();
            callbackContext.success(currentUser);
            return true;
        }else if(action.equals("saveContactList")){
            List<EaseUser> contactList = new ArrayList<EaseUser>();
            String dataStr = args.getString(0);
            JSONArray arr =new JSONArray(dataStr);
            for (int i = 0;i<arr.length();i++){
              JSONObject obj =   arr.getJSONObject(i);
              String loginName =  obj.getString("loginName");
              String user_name =  obj.getString("user_name");
              EaseUser eu =new EaseUser(loginName);
              eu.setNickname(user_name);
              contactList.add(eu);
            }
            DemoDBManager.getInstance().saveContactList(contactList);
            callbackContext.success("success");
            return true;
            //保存单联系个人
        }else if(action.equals("saveContact")){
           String dataStr = args.getString(0);
             JSONObject obj =   new JSONObject(dataStr);
             String loginName =  obj.getString("loginName");
             String user_name =  obj.getString("user_name");
             EaseUser eu =new EaseUser(loginName);
             eu.setNickname(user_name);
             DemoDBManager.getInstance().saveContact(eu);
             callbackContext.success("success");
            return true;
            //删除单个人
        }else if(action.equals("deleteContact")){
            String loginName = args.getString(0);
            DemoDBManager.getInstance().deleteContact(loginName);
            callbackContext.success("success");
            return true;
            
        }else if(action.equals("startVoiceCall")){
            // 给指定人开启语音电话
            String username = args.getString(0);
            if(username==null||username.equals("")){
                Toast.makeText(cordova.getActivity(), "用户信息为空", Toast.LENGTH_SHORT).show();
               return;
            }
            if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(cordova.getActivity(), com.hyphenate.chatuidemo.R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
            } else {
            startActivity(new Intent(cordova.getActivity(), VoiceCallActivity.class).putExtra("username", username)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
//            inputMenu.hideExtendMenuContainer();
            }
            
        }else if(action.equals("startVideoCall")){
             // 给指定人开启视频电话
            String username = args.getString(0);
            if(username==null||username.equals("")){
                Toast.makeText(cordova.getActivity(), "用户信息为空", Toast.LENGTH_SHORT).show();
               return;
            }
            if (!EMClient.getInstance().isConnected()){
            Toast.makeText(cordova.getActivity(), com.hyphenate.chatuidemo.R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
            }else {
            startActivity(new Intent(cordova.getActivity(), VideoCallActivity.class).putExtra("username", username)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            // inputMenu.hideExtendMenuContainer();
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
            msg.setConversationId(conversation.conversationId());
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

    /**
     * debug purpose only, you can ignore this
     */
    private void registerInternalDebugReceiver() {
        internalDebugReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHelper.getInstance().logout(false,new EMCallBack() {

                    @Override
                    public void onSuccess() {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                finish();
//                                startActivity(new Intent(MsgActivity.this, LoginActivity.class));
//                            }
//                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {}

                    @Override
                    public void onError(int code, String message) {}
                });
            }
        };
        IntentFilter filter = new IntentFilter(cordova.getActivity().getPackageName() + ".em_internal_debug");
        cordova.getActivity().registerReceiver(internalDebugReceiver, filter);
    }
    EMClientListener clientListener = new EMClientListener() {
        @Override
        public void onMigrate2x(boolean success) {
            Toast.makeText(cordova.getActivity(), "onUpgradeFrom 2.x to 3.x " + (success ? "success" : "fail"), Toast.LENGTH_LONG).show();
            if (success) {
                refreshUIWithMessage();
            }
        }
    };

    private void refreshUIWithMessage() {
//        cordova.getActivity().runOnUiThread(new Runnable() {
//            public void run() {
                // refresh unread count
                String format = "cordova.plugins.thsHxIM.refreshUIWithMessageInAndroidCallback(%s);";
                final String js = String.format(format, "'refreshUI'");
                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        instance.webView.loadUrl("javascript:" + js);
                    }
                });

//                updateUnreadLabel();
//                List<EMConversation> msgList = loadConversationList();
                //            Log.i("msgList",getConversationToJSON(msgList));
//                if (currentTabIndex == 0) {
//                    // refresh conversation list
//                    if (conversationListFragment != null) {
//                        conversationListFragment.refresh();
//                    }
//                }
//            }
//        });
    }
    /**
     * update unread message count
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
//        if (count > 0) {
//            unreadLabel.setText(String.valueOf(count));
//            unreadLabel.setVisibility(View.VISIBLE);
//        } else {
//            unreadLabel.setVisibility(View.INVISIBLE);
//        }
    }
    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        return EMClient.getInstance().chatManager().getUnreadMessageCount();
    }
    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(cordova.getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
//                updateUnreadLabel();
//                updateUnreadAddressLable();
//                if (currentTabIndex == 0) {
//                    // refresh conversation list
//                    if (conversationListFragment != null) {
//                        conversationListFragment.refresh();
//                    }
//                } else if (currentTabIndex == 1) {
//                    if(contactListFragment != null) {
//                        contactListFragment.refresh();
//                    }
//                }
                String action = intent.getAction();
                if(action.equals(Constant.ACTION_GROUP_CHANAGED)){
//                    if (EaseCommonUtils.getTopActivity(MsgActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
                }
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        onDisconnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int error = intent.getIntExtra("EMError",0);
                Log.e("EMError",error+"");
                String errorStr = "";
                if (error == EMError.USER_REMOVED) {
                    errorStr = "USER_REMOVED";
                    //onUserException(Constant.ACCOUNT_REMOVED);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    //onUserException(Constant.ACCOUNT_CONFLICT);
                    errorStr = "USER_LOGIN_ANOTHER_DEVICE";
                } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                    //onUserException(Constant.ACCOUNT_FORBIDDEN);
                    errorStr = "SERVER_SERVICE_RESTRICTED";
                } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                    //onUserException(Constant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD);
                    errorStr = "USER_KICKED_BY_CHANGE_PASSWORD";
                } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                    errorStr = "USER_KICKED_BY_OTHER_DEVICE";
                    // onUserException(Constant.ACCOUNT_KICKED_BY_OTHER_DEVICE);
                }
//                String format = "cordova.plugins.thsHxIM.onDisconnectedReceiverInAndroidCallback(%s);";
//                final String js = String.format(format, "'"+errorStr+"'");
//                cordova.getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        instance.webView.loadUrl("javascript:" + js);
//                    }
//                });
                try{
                JSONObject obj = new JSONObject();
                obj.put("action","onDisconnectedReceiver");
                obj.put("err",errorStr);
                sendMsg(obj.toString(),"onDisconnectedReceiver");
                }catch (JSONException e){
                sendMsg("{'action':'onDisconnectedReceiver','err':'jsonErr'}","onDisconnectedReceiver");
                }
            }
        };
        IntentFilter disconnectedReceiverintentFilter = new IntentFilter();
        disconnectedReceiverintentFilter.addAction(Constant.ACTION_DISCONNECTED);
        broadcastManager.registerReceiver(onDisconnectedReceiver,disconnectedReceiverintentFilter);
    }

    public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {
            Log.e("MyContactListener","onContactAdded,username:"+username);
            try {
                JSONObject obj = new JSONObject();
                obj.put("action","onContactAdded");
                obj.put("username",username);
                sendMsg(obj.toString(),"contactUpdate");
            }catch (JSONException e){

            }
        }
        @Override
        public void onContactDeleted(final String username) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Log.e("MyContactListener","onContactDeleted"+username);
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("action","onContactDeleted");
                        obj.put("username",username);
                        sendMsg(obj.toString(),"contactUpdate");
                    }catch (JSONException e){

                    }
//                    if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
//                            username.equals(ChatActivity.activityInstance.toChatUsername)) {
//                        String st10 = getResources().getString(com.hyphenate.chatuidemo.R.string.have_you_removed);
//                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
//                                .show();
//                        ChatActivity.activityInstance.finish();
//                    }
                }
            });
            //updateUnreadAddressLable();
        }
        @Override
        public void onContactInvited(String username, String reason) {
            Log.e("MyContactListener","onContactInvited username:"+username+",reason:"+reason);
            try {
                JSONObject obj = new JSONObject();
                obj.put("action","onContactInvited");
                obj.put("username",username);
                obj.put("reason",reason);
                sendMsg(obj.toString(),"contactUpdate");
            }catch (JSONException e){

            }
        }
        @Override
        public void onFriendRequestAccepted(String username) {
            Log.e("MyContactListener","onFriendRequestAccepted username:"+username);
            try {
                JSONObject obj = new JSONObject();
                obj.put("action","onFriendRequestAccepted");
                obj.put("username",username);
                sendMsg(obj.toString(),"contactUpdate");
            }catch (JSONException e){

            }
        }
        @Override
        public void onFriendRequestDeclined(String username) {
            Log.e("MyContactListener","onFriendRequestDeclined username:"+username);
            try {
                JSONObject obj = new JSONObject();
                obj.put("action","onFriendRequestDeclined");
                obj.put("username",username);
                sendMsg(obj.toString(),"contactUpdate");
            }catch (JSONException e){

            }
        }
    }

    public class MyMultiDeviceListener implements EMMultiDeviceListener {

        @Override
        public void onContactEvent(int event, String target, String ext) {
            Log.e("MyMultiDeviceListener","onContactEvent target:"+target+",ext:"+ext);
        }

        @Override
        public void onGroupEvent(int event, String target, final List<String> username) {
            switch (event) {
                case EMMultiDeviceListener.GROUP_LEAVE:
                    Log.e("MyMultiDeviceListener","onGroupEvent");
                    // ChatActivity.activityInstance.finish();
                    break;
                default:
                    break;
            }
        }
    }

    private void unregisterBroadcastReceiver(){
        broadcastManager.unregisterReceiver(broadcastReceiver);
        broadcastManager.unregisterReceiver(onDisconnectedReceiver);
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message: messages) {
                DemoHelper.getInstance().getNotifier().vibrateAndPlayTone(message);
            }
            Log.e("messageListener","onMessageReceived");
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

            refreshUIWithMessage();
            Log.e("messageListener","onCmdMessageReceived");
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            Log.e("messageListener","onMessageRead");
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            Log.e("messageListener","onMessageDelivered");
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            Log.e("messageListener","onMessageRecalled");
            refreshUIWithMessage();
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            Log.e("messageListener","onMessageChanged");
        }
    };

    /**
     * 发送消息到
     * @param data
     * @param methodStr
     */
    private  void sendMsg(String data,String methodStr){
        String format = "cordova.plugins.thsHxIM."+methodStr+"InAndroidCallback(%s);";
        final String js = String.format(format, "'"+data+"'");
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instance.webView.loadUrl("javascript:" + js);
            }
        });
    }
}
