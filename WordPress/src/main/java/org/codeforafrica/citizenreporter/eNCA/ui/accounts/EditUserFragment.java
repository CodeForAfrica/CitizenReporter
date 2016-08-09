package org.codeforafrica.citizenreporter.eNCA.ui.accounts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.codeforafrica.citizenreporter.eNCA.BuildConfig;
import org.codeforafrica.citizenreporter.eNCA.Constants;
import org.codeforafrica.citizenreporter.eNCA.R;
import org.codeforafrica.citizenreporter.eNCA.WordPress;
import org.codeforafrica.citizenreporter.eNCA.models.Blog;
import org.codeforafrica.citizenreporter.eNCA.ui.accounts.helpers.APIFunctions;
import org.wordpress.android.util.AlertUtils;
import org.wordpress.android.util.EditTextUtils;
import org.wordpress.android.util.ToastUtils;
import org.wordpress.android.util.UserEmailUtils;
import org.codeforafrica.citizenreporter.eNCA.widgets.WPNetworkImageView;
import org.codeforafrica.citizenreporter.eNCA.widgets.WPTextView;
import org.wordpress.emailchecker.EmailChecker;
import org.wordpress.persistentedittext.PersistentEditTextHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditUserFragment extends AbstractFragment implements TextWatcher,  Runnable
{
    private EditText mSiteUrlTextField;
    private EditText mEmailTextField;
    private EditText mPasswordTextField;
    private EditText mUsernameTextField;
    private EditText mPhone;
    private EditText mLocation;
    private WPTextView mSignupButton;
    private WPTextView mProgressTextSignIn;
    private RelativeLayout mProgressBarSignIn;
    private EmailChecker mEmailChecker;
    private boolean mEmailAutoCorrected;
    private boolean mAutoCompleteUrl;
    private String email;
    private String password;
    private String username;
    private String phone;
    private String location="";
    private String address="";
    private WPNetworkImageView mAvatar;
    private ImageView default_avatar;
    private WPTextView edit_account_label;
    public EditUserFragment() {
        mEmailChecker = new EmailChecker();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (fieldsFilled()) {
            mSignupButton.setEnabled(true);
        } else {
            mSignupButton.setEnabled(false);
        }
    }

    private boolean fieldsFilled() {
        return EditTextUtils.getText(mEmailTextField).trim().length() > 0
                && EditTextUtils.getText(mUsernameTextField).trim().length() > 0;
    }

    protected void startProgress(String message) {
        mProgressBarSignIn.setVisibility(View.VISIBLE);
        mProgressTextSignIn.setVisibility(View.VISIBLE);
        mSignupButton.setVisibility(View.GONE);
        mProgressBarSignIn.setEnabled(false);
        mProgressTextSignIn.setText(message);
        mEmailTextField.setEnabled(false);
        mPasswordTextField.setEnabled(false);
        mUsernameTextField.setEnabled(false);
        mSiteUrlTextField.setEnabled(false);
        mPhone.setEnabled(false);
        mLocation.setEnabled(false);
    }

    protected void updateProgress(String message) {
        mProgressTextSignIn.setText(message);
    }

    protected void endProgress() {
        mProgressBarSignIn.setVisibility(View.GONE);
        mProgressTextSignIn.setVisibility(View.GONE);
        mSignupButton.setVisibility(View.VISIBLE);
        mEmailTextField.setEnabled(true);
        mPasswordTextField.setEnabled(true);
        mUsernameTextField.setEnabled(true);
        mSiteUrlTextField.setEnabled(true);
        mPhone.setEnabled(true);
        mLocation.setEnabled(true);
    }

    protected boolean isUserDataValid() {
        // try to create the user
        final String email = EditTextUtils.getText(mEmailTextField).trim();
        final String password = EditTextUtils.getText(mPasswordTextField).trim();
        final String username = EditTextUtils.getText(mUsernameTextField).trim();
        final String siteUrl = EditTextUtils.getText(mSiteUrlTextField).trim();
        boolean retValue = true;

        if (email.equals("")) {
            showEmailError(R.string.required_field);
            retValue = false;
        }

        final Pattern emailRegExPattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = emailRegExPattern.matcher(email);
        if (!matcher.find() || email.length() > 100) {
            showEmailError(R.string.invalid_email_message);
            retValue = false;
        }

        /*Password check not required
        if (username.equals("")) {
            showUsernameError(R.string.required_field);
            retValue = false;
        }

        if (username.length() < 4) {
            showUsernameError(R.string.invalid_username_too_short);
            retValue = false;
        }

        if (username.length() > 60) {
            showUsernameError(R.string.invalid_username_too_long);
            retValue = false;
        }
        if (siteUrl.length() < 4) {
            showSiteUrlError(R.string.blog_name_must_be_at_least_four_characters);
            retValue = false;
        }*/


        if (!password.equals("")) {
            showPasswordError(R.string.required_field);
            if (password.length() < 4) {
                showPasswordError(R.string.invalid_password_message);
                retValue = false;
            }
        }


        return retValue;
    }

    protected void onDoneAction() {
        validateAndCreateUserAndBlog();
    }

    private final OnClickListener mSignupClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            validateAndCreateUserAndBlog();
        }
    };

    private final TextView.OnEditorActionListener mEditorAction = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return onDoneEvent(actionId, event);
        }
    };

    private String siteUrlToSiteName(String siteUrl) {
        return siteUrl;
    }

    private void finishThisStuff(String username, String password) {
        final Activity activity = getActivity();
        if (activity != null) {
            Intent intent = new Intent();
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            activity.setResult(NewAccountActivity.RESULT_OK, intent);
            activity.finish();
            PersistentEditTextHelper persistentEditTextHelper = new PersistentEditTextHelper(getActivity());
            persistentEditTextHelper.clearSavedText(mEmailTextField, null);
            persistentEditTextHelper.clearSavedText(mUsernameTextField, null);
            persistentEditTextHelper.clearSavedText(mSiteUrlTextField, null);
            persistentEditTextHelper.clearSavedText(mPhone, null);
            persistentEditTextHelper.clearSavedText(mLocation, null);
        }
    }

    protected boolean specificShowError(int messageId) {
        switch (getErrorType(messageId)) {
            case USERNAME:
                showUsernameError(messageId);
                return true;
            case PASSWORD:
                showPasswordError(messageId);
                return true;
            case EMAIL:
                showEmailError(messageId);
                return true;
            case SITE_URL:
                showSiteUrlError(messageId);
                return true;
        }
        return false;
    }

    private void showPasswordError(int messageId) {
        mPasswordTextField.setError(getString(messageId));
        mPasswordTextField.requestFocus();
    }

    private void showEmailError(int messageId) {
        mEmailTextField.setError(getString(messageId));
        mEmailTextField.requestFocus();
    }

    private void showUsernameError(int messageId) {
        mUsernameTextField.setError(getString(messageId));
        mUsernameTextField.requestFocus();
    }

    private void showSiteUrlError(int messageId) {
        mSiteUrlTextField.setError(getString(messageId));
        mSiteUrlTextField.requestFocus();
    }

    private void validateAndCreateUserAndBlog() {
        if (mSystemService.getActiveNetworkInfo() == null) {
            AlertUtils.showAlert(getActivity(), R.string.no_network_title, R.string.no_network_message);
            return;
        }
        if (!isUserDataValid()) {
            return;
        }

        // Prevent double tapping of the "done" btn in keyboard for those clients that don't dismiss the keyboard.
        // Samsung S4 for example
        if (View.VISIBLE == mProgressBarSignIn.getVisibility()) {
            return;
        }

        startProgress(getString(R.string.validating_user_data));

        final String siteUrl = EditTextUtils.getText(mSiteUrlTextField).trim();
        email = EditTextUtils.getText(mEmailTextField).trim();
        password = EditTextUtils.getText(mPasswordTextField).trim();
        username = EditTextUtils.getText(mUsernameTextField).trim();
        phone = EditTextUtils.getText(mPhone).trim();
        address = EditTextUtils.getText(mLocation).trim();

        handleLogin();
    }

    private void handleLogin ()
    {
       // txtStatus.setText("Connecting to server...");
        if(WordPress.getCurrentBlog().getUsername() == BuildConfig.DEFAULT_USERNAME){
            Toast.makeText(getActivity().getApplicationContext(), "You are not allowed to edit as demouser", Toast.LENGTH_LONG).show();
        }else{
            new Thread(this).start();
        }
    }
    public void run ()
    {
        TelephonyManager telephonyManager = ((TelephonyManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().TELEPHONY_SERVICE));
        //get mobile carrier
        String operatorName = "";
        int simState = telephonyManager.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_READY:
                // do something
                operatorName = "" + telephonyManager.getNetworkOperatorName();
                break;
        }
        //get device IMEI number
        String deviceId = "" + telephonyManager.getDeviceId();
        //get sms serial number
        String serialNumber = "" + telephonyManager.getSimSerialNumber();

        APIFunctions userFunction = new APIFunctions();
        Blog blog = WordPress.getCurrentBlog();

        JSONObject json = userFunction.newUser(blog.getUsername(), username, password, email, operatorName, deviceId, serialNumber, location, address, phone, false);
        if(json != null ) {
            try {
                String res = json.getString("result");
                if (res.equals("OK")) {
                    mHandler.sendEmptyMessage(0);
                } else {
                    Message msgErr = mHandler.obtainMessage(1);
                    msgErr.getData().putString("err", json.getString("message"));
                    mHandler.sendMessage(msgErr);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private Handler mHandler = new Handler ()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    loginSuccess();
                    break;
                case 1:
                    loginFailed(msg.getData().getString("err"));
                default:
            }
        }
    };

    private void loginFailed (String err)
    {
        ToastUtils.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.update_profile_fail) + ": " + err);

        endProgress();
    }
    private void loginSuccess ()
    {
        ToastUtils.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.update_profile_success));

        getActivity().finish();
    }

    private void autocorrectEmail() {
        if (mEmailAutoCorrected) {
            return;
        }
        final String email = EditTextUtils.getText(mEmailTextField).trim();
        String suggest = mEmailChecker.suggestDomainCorrection(email);
        if (suggest.compareTo(email) != 0) {
            mEmailAutoCorrected = true;
            mEmailTextField.setText(suggest);
            mEmailTextField.setSelection(suggest.length());
        }
    }

    private void initInfoButton(View rootView) {
        ImageView infoBUtton = (ImageView) rootView.findViewById(R.id.info_button);
        infoBUtton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newAccountIntent = new Intent(getActivity(), HelpActivity.class);
                startActivity(newAccountIntent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.edit_account_user_fragment_screen, container, false);

        WPTextView termsOfServiceTextView = (WPTextView) rootView.findViewById(R.id.l_agree_terms_of_service);
        termsOfServiceTextView.setText(Html.fromHtml(String.format(getString(R.string.agree_terms_of_service), "<u>",
                "</u>")));
        termsOfServiceTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(Constants.URL_TOS);
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                }
        );

        mSignupButton = (WPTextView) rootView.findViewById(R.id.signup_button);
        edit_account_label = (WPTextView) rootView.findViewById(R.id.edit_account_label);
        edit_account_label.setText(WordPress.getCurrentBlog().getUsername());
        mSignupButton.setOnClickListener(mSignupClickListener);
        mSignupButton.setEnabled(false);

        mProgressTextSignIn = (WPTextView) rootView.findViewById(R.id.nux_sign_in_progress_text);
        mProgressBarSignIn = (RelativeLayout) rootView.findViewById(R.id.nux_sign_in_progress_bar);

        mEmailTextField = (EditText) rootView.findViewById(R.id.email_address);
        mEmailTextField.setText(UserEmailUtils.getPrimaryEmail(getActivity()));
        mEmailTextField.setSelection(EditTextUtils.getText(mEmailTextField).length());
        mPasswordTextField = (EditText) rootView.findViewById(R.id.password);
        mUsernameTextField = (EditText) rootView.findViewById(R.id.username);
        mSiteUrlTextField = (EditText) rootView.findViewById(R.id.site_url);
        mLocation = (EditText) rootView.findViewById(R.id.location);
        mPhone = (EditText) rootView.findViewById(R.id.phone);
        mSiteUrlTextField.setText(BuildConfig.DEFAULT_URL);
        mAvatar = (WPNetworkImageView)rootView.findViewById(R.id.nux_fragment_icon);
        default_avatar = (ImageView)rootView.findViewById(R.id.default_avatar);
        default_avatar.setVisibility(View.GONE);
        mEmailTextField.addTextChangedListener(this);
        mPasswordTextField.addTextChangedListener(this);
        mUsernameTextField.addTextChangedListener(this);
        //mSiteUrlTextField.addTextChangedListener(this);
        //mSiteUrlTextField.setOnKeyListener(mSiteUrlKeyListener);
        //mSiteUrlTextField.setOnEditorActionListener(mEditorAction);

        mUsernameTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // auto fill blog address
                mSiteUrlTextField.setError(null);
                if (mAutoCompleteUrl) {
                    mSiteUrlTextField.setText(EditTextUtils.getText(mUsernameTextField));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mUsernameTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAutoCompleteUrl = EditTextUtils.getText(mUsernameTextField)
                            .equals(EditTextUtils.getText(mSiteUrlTextField))
                            || EditTextUtils.isEmpty(mSiteUrlTextField);
                }
            }
        });

        mEmailTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    autocorrectEmail();
                }
            }
        });
        initPasswordVisibilityButton(rootView, mPasswordTextField);
        initInfoButton(rootView);

        new getValues().execute();

        return rootView;
    }

    private final OnKeyListener mSiteUrlKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            mAutoCompleteUrl = EditTextUtils.isEmpty(mSiteUrlTextField);
            return false;
        }
    };

    class getValues extends AsyncTask<JSONObject, JSONObject, JSONObject> {


        protected JSONObject doInBackground(JSONObject... args) {

            APIFunctions userFunction = new APIFunctions();
            String username = WordPress.getCurrentBlog().getUsername();

            JSONObject json = userFunction.getUser(username);
            JSONObject user = null;
            if(json != null) {
                try {
                    String res = json.getString("result");

                    if (res.equals("OK")) {

                        user = new JSONObject(json.getString("user"));

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return user;
        }
        protected void onPostExecute(JSONObject user){

            try {
                String fullname = "";

                String s_first_name = "" + user.get("first_name");
                if(s_first_name.equals("false") || s_first_name.equals("null")) {
                    s_first_name = "";
                }else{
                    fullname += s_first_name;
                }

                String s_last_name = "" + user.get("last_name");
                if(s_last_name.equals("false") || s_last_name.equals("null")){
                    s_first_name = "";
                }else{
                    fullname += " "+s_last_name;
                }

                String s_email = "" + user.get("email");
                if(s_email.equals("false") || s_email.equals("null"))
                    s_email = "";

                String s_phone_number = "" + user.get("phone_number");
                if(s_phone_number.equals("false") || s_phone_number.equals("null"))
                    s_phone_number = "";

                String s_address = "" + user.get("address");
                if(s_address.equals("false") || s_address.equals("null"))
                    s_address = "";



                mUsernameTextField.setText(fullname);
                mEmailTextField.setText(s_email);
                mPhone.setText(s_phone_number);
                mLocation.setText(s_address);

                //set avatar
                String avatar = "" + user.getString("avatar");
                if(avatar.equals("")){
                    default_avatar.setVisibility(View.VISIBLE);
                    mAvatar.setVisibility(View.GONE);
                }else {
                    default_avatar.setVisibility(View.GONE);
                    mAvatar.setImageUrl(avatar, WPNetworkImageView.ImageType.AVATAR);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
