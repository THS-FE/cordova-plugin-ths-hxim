package cn.com.ths.hx.im;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class thsHxIM extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("loginIM")) {
            String message = args.getString(0);
            this.loginIM(message, callbackContext);
            return true;
        }else if(action.equals("startChat")){
            String message = args.getString(0);
            this.startChat(message, callbackContext);
            return true;
        }
        return false;
    }
   /**
    * 登录即时通讯服务器
    * @param message 
    * @param callbackContext
    */
    private void loginIM(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    private void startChat(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
