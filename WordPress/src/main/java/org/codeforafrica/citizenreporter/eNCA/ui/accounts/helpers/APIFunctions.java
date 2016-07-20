package org.codeforafrica.citizenreporter.eNCA.ui.accounts.helpers;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.codeforafrica.citizenreporter.eNCA.BuildConfig;

import android.util.Log;

public class APIFunctions {

    private JSONParser jsonParser;
    private static final String registerURL = BuildConfig.API_URL + "/register/";
    private static final String updateURL = BuildConfig.API_URL + "/editprofile/";
    private static final String userURL = BuildConfig.API_URL + "/user/";
    private static final String updateDeviceURL = BuildConfig.API_URL + "/edit_user_device/";
    private static final String sendMessageUrl = BuildConfig.API_URL + "/send_message/";
    private static final String confirmPaymentUrl = BuildConfig.API_URL + "/confirm_payment/";
    private static final String submitFeedback = BuildConfig.API_URL + "/submit_feedback/";

    // constructor
    public APIFunctions(){
        jsonParser = new JSONParser();
    }

    public JSONObject newUser(String username, String full_name, String password, String email, String operatorName, String deviceId, String serialNumber, String location, String address, String phone_number, boolean newUser){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("tag", "register"));
        params.add(new BasicNameValuePair("operatorName", operatorName));
        params.add(new BasicNameValuePair("deviceId", deviceId));
        params.add(new BasicNameValuePair("serialNumber", serialNumber));
        params.add(new BasicNameValuePair("location", location));
        params.add(new BasicNameValuePair("phone_number", phone_number));
        params.add(new BasicNameValuePair("address", address));

        String url;
        if(newUser){
            url = registerURL;
        }else{
            url = updateURL;
            params.add(new BasicNameValuePair("full_name", full_name));
        }

        JSONObject json = jsonParser.getJSONFromUrl(url, params);
        // return json
        return json;
    }

    public JSONObject updateUser(String vemail, String vfirst_name, String vlast_name,
                                 String vlocation, String vphone_number, String vaddress, String username) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));

        params.add(new BasicNameValuePair("first_name", vfirst_name));
        params.add(new BasicNameValuePair("last_name", vlast_name));
        params.add(new BasicNameValuePair("location", vlocation));
        params.add(new BasicNameValuePair("phone_number", vphone_number));
        params.add(new BasicNameValuePair("address", vaddress));

        if(vemail!=null){
            params.add(new BasicNameValuePair("email", vemail));

        }

        Log.d("update", updateURL + " params: " + params.toString());

        JSONObject json = jsonParser.getJSONFromUrl(updateURL, params);
        // return json
        return json;
    }

    public JSONObject getUser(String username) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));

        JSONObject json = jsonParser.getJSONFromUrl(userURL, params);
        // return json
        return json;
    }


    public JSONObject updateUserDevice(String regId, String username) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("regId", regId));

        JSONObject json = jsonParser.getJSONFromUrl(updateDeviceURL, params);
        // return json
        return json;
    }

    public JSONObject submitFeedback(String username, String email, String feedback, String os_version, String model) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("feedback", feedback));
        params.add(new BasicNameValuePair("os_version", os_version));
        params.add(new BasicNameValuePair("model", model));

        JSONObject json = jsonParser.getJSONFromUrl(submitFeedback, params);
        // return json
        return json;
    }

    public JSONObject confirmPayment(String post_id, String remoteID, String confirm) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("post_id", post_id));
        params.add(new BasicNameValuePair("confirm", confirm));
        params.add(new BasicNameValuePair("remote_id", remoteID));

        JSONObject json = jsonParser.getJSONFromUrl(confirmPaymentUrl, params);
        // return json
        return json;
    }

    public JSONObject sendMessage(String username, String message) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("message_string", message));
        Log.d("message", username + message + sendMessageUrl + ": params");
        JSONObject json = jsonParser.getJSONFromUrl(sendMessageUrl, params);
        // return json
        return json;
    }
}