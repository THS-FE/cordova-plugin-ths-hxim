package cn.com.ths.hxchattest;
import com.hyphenate.chat.EMConversation;

public class MsgModel {
    // 会话ID
    private String  conversationId;
    /** 聊天对象，可能是群组、聊天室、人 */
    private String name;
    /** 未读消息个数 */
    private String  unreadLabel;
    /** 最近消息内容 */
    private String  message;
    /** 最近的消息的时间 */
    private String  time;
    /** if the input groupId in atMeGroupList */
    private boolean  motioned;
    /**消息类型*/
    private EMConversation.EMConversationType type;
    /**消息状态*/
    private boolean msgState;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnreadLabel() {
        return unreadLabel;
    }

    public void setUnreadLabel(String unreadLabel) {
        this.unreadLabel = unreadLabel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isMotioned() {
        return motioned;
    }

    public void setMotioned(boolean motioned) {
        this.motioned = motioned;
    }

    public EMConversation.EMConversationType getType() {
        return type;
    }

    public void setType(EMConversation.EMConversationType type) {
        this.type = type;
    }

    public boolean isMsgState() {
        return msgState;
    }

    public void setMsgState(boolean msgState) {
        this.msgState = msgState;
    }
//
//    @Override
//    public String toString() {
//        return "{" +
//                "name:'" + name + '\'' +
//                ", unreadLabel:'" + unreadLabel + '\'' +
//                ", message:'" + message + '\'' +
//                ", time:'" + time + '\'' +
//                ", motioned:" + motioned +
//                ", type:" + type +
//                ", msgState:" + msgState +
//                '}';
//    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "{" +
                "conversationId:'" + conversationId + '\'' +
                ", name:'" + name + '\'' +
                ", unreadLabel:'" + unreadLabel + '\'' +
                ", message:'" + message + '\'' +
                ", time:'" + time + '\'' +
                ", motioned:" + motioned +
                ", type:" + type +
                ", msgState:" + msgState +
                '}';
    }
}
