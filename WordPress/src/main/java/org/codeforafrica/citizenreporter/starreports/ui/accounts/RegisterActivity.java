package org.codeforafrica.citizenreporter.starreports.ui.accounts;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import org.codeforafrica.citizenreporter.starreports.R;
import org.codeforafrica.citizenreporter.starreports.ui.RequestCodes;

/**
 * Created by john on 7/15/2015.
 */
public class RegisterActivity extends ActionBarActivity {
    private View parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_account_activity);
        parentLayout = findViewById(R.id.new_user_page_fragment);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RequestCodes.READ_PHONE_STATE_PERMISSIONS: {
                Log.i("Permissions", "case of phone state permissions");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i("Permissions", "Permission has been accepted do activity");

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try  {
                                //Your code goes here
                                NewUserFragment_Org fragmentOrg = (NewUserFragment_Org) getFragmentManager()
                                        .findFragmentById(R.id.new_user_page_fragment);
                                fragmentOrg.registerNewUser();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();



                } else {
                    // since the user doesn't want to allow the app to use this permission,
                    // exit the app
                    // TODO exit app if the user doesn't accept this permission
                    Log.i("Permissions", "Permission has been denied");
                    Snackbar.make(parentLayout, "You can not create an account " +
                            "without enabling this Permission(s) from settings",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        }
    }


}