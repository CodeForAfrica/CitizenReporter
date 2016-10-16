package org.codeforafrica.citizenreporter.eNCA.ui.accounts.helpers;

import android.annotation.SuppressLint;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.wordpress.rest.Oauth;
import com.wordpress.rest.Oauth.ErrorListener;
import com.wordpress.rest.Oauth.Listener;

import org.json.JSONException;
import org.json.JSONObject;
import org.codeforafrica.citizenreporter.eNCA.R;
import org.codeforafrica.citizenreporter.eNCA.WordPress;
import org.codeforafrica.citizenreporter.eNCA.models.Account;
import org.codeforafrica.citizenreporter.eNCA.models.Blog;
import org.codeforafrica.citizenreporter.eNCA.ui.notifications.utils.SimperiumUtils;
import org.codeforafrica.citizenreporter.eNCA.models.AccountHelper;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.AppLog.T;
import org.wordpress.android.util.VolleyUtils;

public class LoginWPCom extends LoginAbstract {

    private String mTwoStepCode;
    private boolean mShouldSendTwoStepSMS;
    private Blog mJetpackBlog;

    public LoginWPCom(String username, String password, String twoStepCode, boolean shouldSendTwoStepSMS, Blog blog) {
        super(username, password);
        mTwoStepCode = twoStepCode;
        mShouldSendTwoStepSMS = shouldSendTwoStepSMS;
        mJetpackBlog = blog;
    }

    public static int restLoginErrorToMsgId(JSONObject errorObject) {
        // Default to generic error message
        int errorMsgId = org.codeforafrica.citizenreporter.eNCA.R.string.nux_cannot_log_in;

        // Map REST errors to local error codes
        if (errorObject != null) {
            try {
                String error = errorObject.optString("error", "");
                String errorDescription = errorObject.getString("error_description");
                if (error.equals("invalid_request")) {
                    if (errorDescription.contains("Incorrect username or password.")) {
                        errorMsgId = org.codeforafrica.citizenreporter.eNCA.R.string.username_or_password_incorrect;
                    }
                } else if (error.equals("needs_2fa")) {
                    errorMsgId = org.codeforafrica.citizenreporter.eNCA.R.string.account_two_step_auth_enabled;
                } else if (error.equals("invalid_otp")) {
                    errorMsgId = org.codeforafrica.citizenreporter.eNCA.R.string.invalid_verification_code;
                }
            } catch (JSONException e) {
                AppLog.e(T.NUX, e);
            }
        }
        return errorMsgId;
    }

    private Request makeOAuthRequest(final String username, final String password, final Listener listener,
                                     final ErrorListener errorListener) {
        Oauth oauth = new Oauth(org.codeforafrica.citizenreporter.eNCA.BuildConfig.OAUTH_APP_ID,
                org.codeforafrica.citizenreporter.eNCA.BuildConfig.OAUTH_APP_SECRET,
                org.codeforafrica.citizenreporter.eNCA.BuildConfig.OAUTH_REDIRECT_URI);
        Request oauthRequest;
        oauthRequest = oauth.makeRequest(username, password, mTwoStepCode, mShouldSendTwoStepSMS, listener, errorListener);
        return oauthRequest;
    }

    protected void login() {
        // Get OAuth token for the first time and check for errors
        WordPress.requestQueue.add(makeOAuthRequest(mUsername, mPassword, new Oauth.Listener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(final Oauth.Token token) {
                if (mJetpackBlog != null) {
                    // Store token in blog object for Jetpack sites
                    mJetpackBlog.setApi_key(token.toString());
                    mJetpackBlog.setDotcom_username(mUsername);
                    WordPress.wpDB.saveBlog(mJetpackBlog);
                }

                Account account = AccountHelper.getDefaultAccount();

                if (mJetpackBlog == null) {
                    // Store token in global account
                    account.setAccessToken(token.toString());
                    account.setUserName(mUsername);
                    account.save();
                    account.fetchAccountDetails();
                }

                // Once we have a token, start up Simperium
                SimperiumUtils.configureSimperium(WordPress.getContext(), token.toString());

                mCallback.onSuccess();
            }
        }, new Oauth.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                JSONObject errorObject = VolleyUtils.volleyErrorToJSON(volleyError);
                int errorMsgId = restLoginErrorToMsgId(errorObject);
                mCallback.onError(errorMsgId, errorMsgId == R.string.account_two_step_auth_enabled, false, false);
            }
        }));
    }
}
