package org.codeforafrica.citizenreporter.eNCA.ui.accounts;

import android.os.AsyncTask;
import android.util.Log;

import org.codeforafrica.citizenreporter.eNCA.WordPress;
import org.codeforafrica.citizenreporter.eNCA.datasets.AccountTable;
import org.codeforafrica.citizenreporter.eNCA.models.Account;
import org.codeforafrica.citizenreporter.eNCA.ui.accounts.helpers.APIFunctions;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ahereza on 3/7/17.
 */

public class UserDetails extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        APIFunctions userFunction = new APIFunctions();
        String username = WordPress.getCurrentBlog().getUsername();
        Account account = new Account();

        JSONObject json = userFunction.getUser(username);
        Log.d("CITIZEN", "get user json   " + json.toString());

        String result="";
        String user_id="";
        String email="";
        if(json!=null){
            try {
                result  = json.getString("result");
                JSONObject user = json.getJSONObject("user");
                user_id = user.getString("user_id");
                String uname = user.getString("email");

                Log.d("CITIZEN", "User ID" + user_id);

                account.setUserId(Long.valueOf(user_id));
                account.setUserName(username);

                AccountTable accountTable = new AccountTable();
                accountTable.save(account);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return result;
    }
}
