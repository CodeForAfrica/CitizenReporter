package org.codeforafrica.citizenreporter.eNCA.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Ahereza on 12/14/16.
 */
public class CRFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FIREBASE TOKEN: ", " " + token);

        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
    }
}
