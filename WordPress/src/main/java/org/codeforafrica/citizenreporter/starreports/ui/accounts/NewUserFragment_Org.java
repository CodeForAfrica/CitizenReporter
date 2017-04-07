package org.codeforafrica.citizenreporter.starreports.ui.accounts;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang.NumberUtils;
import org.codeforafrica.citizenreporter.starreports.models.UserLocation;
import org.codeforafrica.citizenreporter.starreports.ui.RequestCodes;
import org.json.JSONException;
import org.json.JSONObject;
import org.codeforafrica.citizenreporter.starreports.BuildConfig;
import org.codeforafrica.citizenreporter.starreports.Constants;
import org.codeforafrica.citizenreporter.starreports.R;
import org.codeforafrica.citizenreporter.starreports.ui.accounts.helpers.APIFunctions;
import org.wordpress.android.util.AlertUtils;
import org.wordpress.android.util.EditTextUtils;
import org.wordpress.android.util.GeocoderUtils;
import org.wordpress.android.util.ToastUtils;
import org.wordpress.android.util.UserEmailUtils;
import org.codeforafrica.citizenreporter.starreports.widgets.WPTextView;
import org.wordpress.android.util.helpers.LocationHelper;
import org.wordpress.emailchecker.EmailChecker;
import org.wordpress.persistentedittext.PersistentEditTextHelper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NewUserFragment_Org extends AbstractFragment implements TextWatcher,  Runnable
        ,View.OnClickListener, TextView.OnEditorActionListener{
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

    public static String address_gps="";
    private CoordinatorLayout mcoordinatorlayout;
    private ViewGroup rootView;

    private String email;
    private String password;
    private String username;
    private String phone;
    private String location="";
    private String address="";
    public NewUserFragment_Org() {
        mEmailChecker = new EmailChecker();
    }
    TelephonyManager telephonyManager;

    private EditText mLocationETDialog;
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
                && EditTextUtils.getText(mPasswordTextField).trim().length() > 0
                && EditTextUtils.getText(mUsernameTextField).trim().length() > 0
                && EditTextUtils.getText(mSiteUrlTextField).trim().length() > 0;
    }

    private String _getPackageName(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return getContext().getPackageName();
        } else {
            return getContext().getPackageName();
        }
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
        final String phone = EditTextUtils.getText(mPhone).trim();

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
        }

        if (password.equals("")) {
            showPasswordError(R.string.required_field);
            retValue = false;
        }

        if (password.length() < 4) {
            showPasswordError(R.string.invalid_password_message);
            retValue = false;
        }

        if(phone.length() < 10){
            showPhoneError(R.string.enter_valid_phone_number);
            retValue = false;
        }

        if(!NumberUtils.isNumber(phone.replace("+", ""))){
            showPhoneError(R.string.enter_valid_phone_number_no_symbols);
            retValue = false;
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
    private void showPhoneError(int messageId) {
        mPhone.setError(getString(messageId));
        mPhone.requestFocus();
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
        new Thread(this).start();
    }
    public void run ()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Log.i("Permissions", "OS Version = M");
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE)!=
                    PackageManager.PERMISSION_GRANTED){
                Log.i("Permissions", "Permissions not granted");
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE)){
                    Log.i("Permissions", "Show context");
                    Snackbar.make(getView(),
                            "Please Grant Permissions",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission
                                                    .READ_PHONE_STATE},
                                            RequestCodes.READ_PHONE_STATE_PERMISSIONS);
                                }
                            }).show();

                } else {
                    Log.i("Permissions", "request permissions");
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE},
                            RequestCodes.READ_PHONE_STATE_PERMISSIONS);
                }
            } else{
                Log.i("Permissions", "Permission was granted");
                registerNewUser();
            }
        } else{
            Log.i("Permissions", "OS less than M");
            registerNewUser();
        }

    }

    public void registerNewUser(){
        Log.i("Permissions", "Register new user");
        /*
        * FUnction to register new user, made it modular to ease requesting for permissions*/

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

        String deviceId = "" + telephonyManager.getDeviceId();
        //get sms serial number
        String serialNumber = "" + telephonyManager.getSimSerialNumber();

        Log.d("Create account issues", "username: " + username);
        Log.d("Create account issues", "password: " + password);

        APIFunctions userFunction = new APIFunctions();
        JSONObject json = userFunction.newUser(username, "", password, email, operatorName, deviceId, serialNumber, location, address, phone, true);
        if(json !=null) {
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
        ToastUtils.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.registration_failed) + ": " + err);

        endProgress();
    }
    private void loginSuccess ()
    {
        ToastUtils.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.registration_success));

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
        rootView = (ViewGroup) inflater.inflate(R.layout.new_account_user_fragment_screen, container, false);

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
        mLocation.setFocusable(false);
        mLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                initLocation();
            }
        });

        mPhone = (EditText) rootView.findViewById(R.id.phone);
        mSiteUrlTextField.setText(BuildConfig.DEFAULT_URL);

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
        return rootView;
    }

    private final OnKeyListener mSiteUrlKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            mAutoCompleteUrl = EditTextUtils.isEmpty(mSiteUrlTextField);
            return false;
        }
    };


    /**
     * Location methods
     */

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.locationText) {
            viewLocation();
        } else if (id == R.id.updateLocation) {
            showLocationSearch();
        } else if (id == R.id.removeLocation) {
            removeLocation();
            showLocationAdd();
        } else if (id == R.id.addLocation) {
            showLocationSearch();
        } else if (id == R.id.searchLocation) {
            searchLocation();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }

    private static enum LocationStatus {NONE, FOUND, NOT_FOUND, SEARCHING}
    /*
     * retrieves and displays the friendly address for a lat/long location
     */
    private class GetAddressTask extends AsyncTask<Double, Void, Address> {
        double latitude;
        double longitude;

        @Override
        protected void onPreExecute() {
            setLocationStatus(LocationStatus.SEARCHING);
            showLocationView();
        }

        @Override
        protected Address doInBackground(Double... args) {
            // args will be the latitude, longitude to look up
            latitude = args[0];
            longitude = args[1];

            return GeocoderUtils.getAddressFromCoords(getActivity(), latitude, longitude);
        }

        protected void onPostExecute(Address address) {
            setLocationStatus(LocationStatus.FOUND);
            if (address == null) {
                // show lat/long when Geocoder fails (ugly, but better than not showing anything
                // or showing an error since the location has been assigned to the post already)
                updateLocationText(Double.toString(latitude) + ", " + Double.toString(longitude));
            } else {
                String locationName = GeocoderUtils.getLocationNameFromAddress(address);
                updateLocationText(locationName);
            }
        }
    }

    private class GetCoordsTask extends AsyncTask<String, Void, Address> {
        @Override
        protected void onPreExecute() {
            setLocationStatus(LocationStatus.SEARCHING);
            showLocationView();
        }

        @Override
        protected Address doInBackground(String... args) {
            String locationName = args[0];

            return GeocoderUtils.getAddressFromLocationName(getActivity(), locationName);
        }

        @Override
        protected void onPostExecute(Address address) {
            setLocationStatus(LocationStatus.FOUND);
            showLocationView();

            if (address != null) {
                double[] coordinates = GeocoderUtils.getCoordsFromAddress(address);
                setLocation(coordinates[0], coordinates[1]);

                String locationName = GeocoderUtils.getLocationNameFromAddress(address);
                updateLocationText(locationName);
            } else {
                showLocationNotAvailableError();
                showLocationSearch();
            }
        }
    }

    private LocationHelper.LocationResult locationResult = new LocationHelper.LocationResult() {
        @Override
        public void gotLocation(final Location location) {
            if (getActivity() == null)
                return;
            // note that location will be null when requesting location fails
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    setLocation(location);
                }
            });
        }
    };

    private View mLocationAddSection;
    private View mLocationSearchSection;
    private View mLocationViewSection;
    private TextView mLocationText;
    private EditText mLocationEditText;
    private Button mButtonSearchLocation;
    private UserLocation mUserLocation;
    private LocationHelper mLocationHelper;

    private TextWatcher mLocationEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            String buttonText;
            if (s.length() > 0) {
                buttonText = getActivity().getApplicationContext().getResources().getString(R.string.search_location);
            } else {
                buttonText = getActivity().getApplicationContext().getResources().getString(R.string.search_current_location);
            }
            mButtonSearchLocation.setText(buttonText);
        }
    };


    /*
     * called when activity is created to initialize the location provider, show views related
     * to location if enabled for this blog, and retrieve the current location if necessary
     */
    private void initLocation() {
        final Dialog locationDialog = new Dialog(getActivity());
        locationDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        locationDialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        locationDialog.setContentView(R.layout.location_dialog);
        locationDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Button enableLocation = (Button) locationDialog.findViewById(R.id.enableLocation);

        mLocationETDialog = (EditText)locationDialog.findViewById(R.id.editTextSummary);
        final Button submitButton = (Button)locationDialog.findViewById(R.id.submitButton);

        locationDialog.show();


        //set default location

        final String prompt = getActivity().getResources().getString(R.string.location_prompt);

        String current_location = mLocation.getText().toString();
        if(!current_location.equals(prompt) && (!current_location.equals(""))){
            mLocationETDialog.setText(current_location);
            submitButton.setEnabled(true);
        }else{
            submitButton.setEnabled(false);
        }

        // show the location views if a provider was found and this is a post on a blog that has location enabled
        if (hasLocationProvider()) {
            enableLocation.setVisibility(View.GONE);

            View locationRootView = ((ViewStub) locationDialog.findViewById(R.id.stub_post_location_settings)).inflate();

            mLocationText = (TextView) locationRootView.findViewById(R.id.locationText);
            mLocationText.setOnClickListener(this);

            mLocationAddSection = locationRootView.findViewById(R.id.sectionLocationAdd);
            mLocationSearchSection = locationRootView.findViewById(R.id.sectionLocationSearch);
            mLocationViewSection = locationRootView.findViewById(R.id.sectionLocationView);

            Button addLocation = (Button) locationRootView.findViewById(R.id.addLocation);
            addLocation.setOnClickListener(this);

            mButtonSearchLocation = (Button) locationRootView.findViewById(R.id.searchLocation);
            mButtonSearchLocation.setOnClickListener(this);

            mLocationEditText = (EditText) locationRootView.findViewById(R.id.searchLocationText);
            mLocationEditText.setOnEditorActionListener(this);
            mLocationEditText.addTextChangedListener(mLocationEditTextWatcher);

            Button updateLocation = (Button) locationRootView.findViewById(R.id.updateLocation);
            Button removeLocation = (Button) locationRootView.findViewById(R.id.removeLocation);
            updateLocation.setOnClickListener(this);
            removeLocation.setOnClickListener(this);

            // if this post has location attached to it, look up the location address
            showLocationAdd();

        }else{
            enableLocation.setVisibility(View.VISIBLE);
            enableLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, RequestCodes.START_LOCATION_SERVICE);
                }
            });
        }

        //close dialog
        locationDialog.findViewById(R.id.closeDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDialog.dismiss();
            }
        });

        //detect
        mLocationETDialog.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String new_answer = "" + mLocationETDialog.getText().toString();
                if (new_answer.length() > 0) {
                    submitButton.setEnabled(true);
                } else {
                    submitButton.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  set_location = mLocationETDialog.getText().toString();

                if(set_location.trim().length() > 0){
                    mLocation.setText(set_location);
                }

                locationDialog.dismiss();
            }
        });
    }

    private boolean hasLocationProvider() {
        boolean hasLocationProvider = false;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if (providers != null) {
            for (String providerName : providers) {
                if (providerName.equals(LocationManager.GPS_PROVIDER)
                        || providerName.equals(LocationManager.NETWORK_PROVIDER)) {
                    hasLocationProvider = true;
                }
            }
        }
        return hasLocationProvider;
    }

    private void showLocationSearch() {
        mLocationAddSection.setVisibility(View.GONE);
        mLocationSearchSection.setVisibility(View.VISIBLE);
        mLocationViewSection.setVisibility(View.GONE);

        EditTextUtils.showSoftInput(mLocationEditText);
    }

    private void showLocationAdd() {
        mLocationAddSection.setVisibility(View.VISIBLE);
        mLocationSearchSection.setVisibility(View.GONE);
        mLocationViewSection.setVisibility(View.GONE);
    }

    private void showLocationView() {
        mLocationAddSection.setVisibility(View.GONE);
        mLocationSearchSection.setVisibility(View.GONE);
        mLocationViewSection.setVisibility(View.VISIBLE);
    }

    private void searchLocation() {
        EditTextUtils.hideSoftInput(mLocationEditText);
        String location = EditTextUtils.getText(mLocationEditText);

        removeLocation();

        if (location.isEmpty()) {
            fetchCurrentLocation();
        } else {
            new GetCoordsTask().execute(location);
        }
    }

    /*
     * get the current location
     */
    private void fetchCurrentLocation() {
        if (mLocationHelper == null) {
            mLocationHelper = new LocationHelper();
        }
        boolean canGetLocation = mLocationHelper.getLocation(getActivity(), locationResult);

        if (canGetLocation) {
            setLocationStatus(LocationStatus.SEARCHING);
            showLocationView();
        } else {
            setLocation(null);
            showLocationNotAvailableError();
            showLocationAdd();
        }
    }

    /*
     * called when location is retrieved/updated for this post - looks up the address to
     * display for the lat/long
     */
    private void setLocation(Location location) {
        if (location != null) {
            setLocation(location.getLatitude(), location.getLongitude());
        } else {
            updateLocationText(getActivity().getApplicationContext().getString(R.string.location_not_found));
            setLocationStatus(LocationStatus.NOT_FOUND);
        }
    }

    private void setLocation(double latitude, double longitude) {
        mUserLocation = new UserLocation(latitude, longitude);
        new GetAddressTask().execute(mUserLocation.getLatitude(), mUserLocation.getLongitude());
    }

    private void removeLocation() {
        mUserLocation = null;
        //mUser.unsetLocation();

        updateLocationText("");
        setLocationStatus(LocationStatus.NONE);
    }

    private void viewLocation() {
        if (mUserLocation != null && mUserLocation.isValid()) {
            String uri = "geo:" + mUserLocation.getLatitude() + "," + mUserLocation.getLongitude();
            getActivity().startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
        } else {
            showLocationNotAvailableError();
            showLocationAdd();
        }
    }

    private void showLocationNotAvailableError() {
        Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getResources().getText(R.string.location_not_found), Toast.LENGTH_SHORT).show();
    }

    private void updateLocationText(String locationName) {
        mLocationText.setText(locationName);
        mLocationEditText.setText(locationName);

        if(!locationName.equals(getActivity().getApplicationContext().getResources().getText(R.string.location_not_found)) && !(locationName.equals(getActivity().getApplicationContext().getResources().getText(R.string.loading)))) {
            //mUser.setStringLocation(locationName);
            mLocation.setText(locationName);
            mLocationETDialog.setText(locationName);
        }
    }

    /*
     * changes the left drawable on the location text to match the passed status
     */

    private void setLocationStatus(LocationStatus status) {

        // animate location text when searching
        if (status == LocationStatus.SEARCHING) {
            updateLocationText(getActivity().getApplicationContext().getString(R.string.loading));

            Animation aniBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
            if (aniBlink != null) {
                mLocationText.startAnimation(aniBlink);
            }
        } else {
            mLocationText.clearAnimation();
        }

        final int drawableId;
        switch (status) {
            case FOUND:
                drawableId = R.drawable.ic_action_location_found;
                break;
            case NOT_FOUND:
                drawableId = R.drawable.ic_action_location_off;
                break;
            case SEARCHING:
                drawableId = R.drawable.ic_action_location_searching;
                break;
            case NONE:
                drawableId = 0;
                break;
            default:
                return;
        }

        mLocationText.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
    }
}