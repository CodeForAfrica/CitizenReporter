package org.codeforafrica.citizenreporter.starreports.wallet;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.codeforafrica.citizenreporter.starreports.ui.accounts.helpers.APIFunctions;

/**
 * Created by nick on 19/08/15.
 */
public class ConfirmPayment extends AsyncTask<String, String, String> {

    private String post_id;
    private String confirm;
    private String remote_id;

    public ConfirmPayment(String _post_id, String _remote_id, String _confirm){
        this.post_id = _post_id;
        this.confirm = _confirm;
        this.remote_id = _remote_id;
    }

    @Override
    protected String doInBackground(String... strings) {
        APIFunctions userFunction = new APIFunctions();
        JSONObject json = userFunction.confirmPayment(post_id, remote_id, confirm);

        if(json!=null) {
            String responseMessage = "";
            try {
                String res = json.getString("result");
                if (res.equals("OK")) {
                    responseMessage = json.getString("message");

                } else {
                    responseMessage = json.getString("error");
                }

                Log.d("Confirm payment", responseMessage + "");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}