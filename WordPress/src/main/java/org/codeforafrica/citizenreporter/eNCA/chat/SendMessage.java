package org.codeforafrica.citizenreporter.eNCA.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.codeforafrica.citizenreporter.eNCA.WordPress;
import org.codeforafrica.citizenreporter.eNCA.ui.accounts.helpers.APIFunctions;


public class SendMessage extends AsyncTask<Void, Void, String> {

    private Context ctx;
    private String message;
    public SendMessage(Context ctx, String message){
        this.ctx = ctx;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... arg0) {
        APIFunctions userFunction = new APIFunctions();
        String username = WordPress.getCurrentBlog().getUsername();

        JSONObject json = userFunction.sendMessage(username, message);

        String result="";
        if(json!=null){
            try {
                result  = json.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("OK")){
            Toast.makeText(ctx, "Message sent!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(ctx, "Message failed!", Toast.LENGTH_SHORT).show();
        }

    }
}