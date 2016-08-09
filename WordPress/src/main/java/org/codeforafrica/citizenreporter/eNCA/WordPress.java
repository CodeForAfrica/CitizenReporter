package org.codeforafrica.citizenreporter.eNCA;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wordpress.rest.RestClient;
import com.wordpress.rest.RestRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.wordpress.android.analytics.AnalyticsTracker;
import org.wordpress.android.analytics.AnalyticsTracker.Stat;
import org.wordpress.android.analytics.AnalyticsTrackerMixpanel;
import org.wordpress.android.analytics.AnalyticsTrackerNosara;
import org.codeforafrica.citizenreporter.eNCA.datasets.ReaderDatabase;
import org.codeforafrica.citizenreporter.eNCA.datasets.SuggestionTable;
import org.codeforafrica.citizenreporter.eNCA.models.AssignmentsListPost;
import org.codeforafrica.citizenreporter.eNCA.models.Blog;
import org.codeforafrica.citizenreporter.eNCA.models.Post;
import org.codeforafrica.citizenreporter.eNCA.networking.OAuthAuthenticator;
import org.codeforafrica.citizenreporter.eNCA.networking.OAuthAuthenticatorFactory;
import org.codeforafrica.citizenreporter.eNCA.networking.RestClientUtils;
import org.codeforafrica.citizenreporter.eNCA.networking.SelfSignedSSLCertsManager;
import org.codeforafrica.citizenreporter.eNCA.overlaycamera.OverlayCameraActivity;
import org.codeforafrica.citizenreporter.eNCA.ui.ActivityId;
import org.codeforafrica.citizenreporter.eNCA.ui.RequestCodes;
import org.codeforafrica.citizenreporter.eNCA.ui.accounts.helpers.APIFunctions;
import org.codeforafrica.citizenreporter.eNCA.ui.accounts.helpers.UpdateBlogListTask.GenericUpdateBlogListTask;
import org.codeforafrica.citizenreporter.eNCA.ui.notifications.utils.NotificationsUtils;
import org.codeforafrica.citizenreporter.eNCA.ui.notifications.utils.SimperiumUtils;
import org.codeforafrica.citizenreporter.eNCA.ui.posts.StoryBoard;
import org.codeforafrica.citizenreporter.eNCA.ui.prefs.AppPrefs;
import org.codeforafrica.citizenreporter.eNCA.ui.stats.datasets.StatsDatabaseHelper;
import org.codeforafrica.citizenreporter.eNCA.ui.stats.datasets.StatsTable;
import org.wordpress.android.util.ABTestingUtils;
import org.wordpress.android.util.ABTestingUtils.Feature;
import org.codeforafrica.citizenreporter.eNCA.models.AccountHelper;
import org.wordpress.android.util.AnalyticsUtils;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.AppLog.T;
import org.wordpress.android.util.BitmapLruCache;
import org.wordpress.android.util.CoreEvents;
import org.wordpress.android.util.CoreEvents.UserSignedOutCompletely;
import org.wordpress.android.util.CoreEvents.UserSignedOutWordPressCom;
import org.wordpress.android.util.DateTimeUtils;
import org.wordpress.android.util.DeviceUtils;
import org.wordpress.android.util.HelpshiftHelper;
import org.wordpress.android.util.NetworkUtils;
import org.wordpress.android.util.PackageUtils;
import org.wordpress.android.util.ProfilingUtils;
import org.wordpress.android.util.RateLimitedTask;
import org.wordpress.android.util.ToastUtils;
import org.wordpress.android.util.VolleyUtils;
import org.wordpress.passcodelock.AbstractAppLock;
import org.wordpress.passcodelock.AppLockManager;
import org.xmlrpc.android.ApiHelper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;

public class WordPress extends Application {
    public static String versionName;
    public static Blog currentBlog;
    public static Post currentPost;
    public static AssignmentsListPost currentAssignment;
    public static WordPressDB wpDB;
    public static RestClientUtils mRestClientUtils;
    public static RestClientUtils mRestClientUtilsVersion1_1;
    public static RequestQueue requestQueue;
    public static ImageLoader imageLoader;

    private static final int SECONDS_BETWEEN_OPTIONS_UPDATE = 10 * 60;
    private static final int SECONDS_BETWEEN_BLOGLIST_UPDATE = 6 * 60 * 60;
    private static final int SECONDS_BETWEEN_DELETE_STATS = 5 * 60; // 5 minutes

    private static Context mContext;
    private static BitmapLruCache mBitmapCache;

    /**
     *  Updates Options for the current blog in background.
     */
    public static RateLimitedTask sUpdateCurrentBlogOption = new RateLimitedTask(SECONDS_BETWEEN_OPTIONS_UPDATE) {
        protected boolean run() {
            Blog currentBlog = WordPress.getCurrentBlog();
            if (currentBlog != null) {
                new ApiHelper.RefreshBlogContentTask(currentBlog, null).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, false);
                return true;
            }
            return false;
        }
    };

    /**
     *  Update blog list in a background task. Broadcast WordPress.BROADCAST_ACTION_BLOG_LIST_CHANGED if the
     *  list changed.
     */
    public static RateLimitedTask sUpdateWordPressComBlogList = new RateLimitedTask(SECONDS_BETWEEN_BLOGLIST_UPDATE) {
        protected boolean run() {
            if (AccountHelper.isSignedInWordPressDotCom()) {
                new GenericUpdateBlogListTask(getContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            return true;
        }
    };

    /**
     *  Delete stats cache that is already expired
     */
    public static RateLimitedTask sDeleteExpiredStats = new RateLimitedTask(SECONDS_BETWEEN_DELETE_STATS) {
        protected boolean run() {
            // Offload to a separate thread. We don't want to slown down the app on startup/resume.
            new Thread(new Runnable() {
                public void run() {
                    // subtracts to the current time the cache TTL
                    long timeToDelete = System.currentTimeMillis() - (StatsTable.CACHE_TTL_MINUTES * 60 * 1000);
                    StatsTable.deleteOldStats(WordPress.getContext(), timeToDelete);
                }
            }).start();
            return true;
        }
    };

    public static BitmapLruCache getBitmapCache() {
        if (mBitmapCache == null) {
            // The cache size will be measured in kilobytes rather than
            // number of items. See http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxMemory / 16;  //Use 1/16th of the available memory for this memory cache.
            mBitmapCache = new BitmapLruCache(cacheSize);
        }
        return mBitmapCache;
    }

    public void checkQuickCapture(Activity activity, Context mContext){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean quickCapture = pref.getBoolean("quickCapture", false);
        String mediaType = pref.getString("mediaType", "0");

        if(quickCapture){
            //has pending quick capture
            if(mediaType.equals("1")){
                capturePic(activity, mContext);
            }else if(mediaType.equals("2")){
                captureVid(activity, mContext);
            }else if(mediaType.equals("3")){
                captureAudio(activity, mContext);
            }

            SharedPreferences.Editor editor = pref.edit();

            editor.putBoolean("quickCapture", false);
            editor.putString("mediaType", "0");
            editor.commit();
        }
    }

    public void giveFeedbackDialog(final Activity mActivity, Context mContext){
        final Dialog mDialog = new Dialog(mActivity);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.feedback_dialog);
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.show();

        final String os_version = getAndroidVersion(mContext);
        final String device_model = getDeviceName(mContext);

        final TextView os_versionTV = (TextView)mDialog.findViewById(R.id.os_version);
        os_versionTV.setText(os_version);

        final TextView device_modelTV = (TextView)mDialog.findViewById(R.id.device_model);
        device_modelTV.setText(device_model);

        final EditText feedbackDesc = (EditText)mDialog.findViewById(R.id.feedback_desc);

        final Spinner accounts_spinner = (Spinner)mDialog.findViewById(R.id.accounts_spinner);
        ArrayAdapter dataAdapter = new ArrayAdapter(mActivity,android.R.layout.simple_spinner_item, getUserEmails(mContext));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accounts_spinner.setAdapter(dataAdapter);

        mDialog.findViewById(R.id.submit_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    submitFeedback(mActivity, mDialog, accounts_spinner.getSelectedItem().toString(), feedbackDesc.getText().toString(), os_version, device_model);
            }
        });
    }

    public void submitFeedback(Activity mActivity, Dialog mDialog, String email, String feedback, String os_version, String model){
        if (!NetworkUtils.isNetworkAvailable(mActivity)) {
            ToastUtils.showToast(mActivity, R.string.error_feedback_no_network, ToastUtils.Duration.SHORT);
        }else{
            mDialog.dismiss();

            new SubmitFeedback(mActivity, email, feedback, os_version, model).execute();
        }
    }

    class SubmitFeedback extends AsyncTask<String, String, String>{
        private ProgressDialog progressDialog;
        private Activity mActivity;
        private String mEmail;
        private String mFeedback;
        private String mVersion;
        private String mModel;

        private SubmitFeedback(Activity _activity, String email, String feedback, String os_version, String model){
            this.mActivity = _activity;
            this.mEmail = email;
            this.mFeedback = feedback;
            this.mModel = model;
            this.mVersion = os_version;
        }
        @Override
        protected void onPreExecute(){
            ToastUtils.showToast(mActivity, R.string.submitting_feedback, ToastUtils.Duration.LONG);

            //progressDialog = new ProgressDialog(mActivity);
            //progressDialog.setMessage("Submitting feedback");
            //progressDialog.setIndeterminate(false);
            //progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {


            String username = "";
                    if(WordPress.getCurrentBlog()!=null){
                        WordPress.getCurrentBlog().getUsername();
                    }

            APIFunctions userFunction = new APIFunctions();
            JSONObject json = userFunction.submitFeedback(username, mEmail, mFeedback, mVersion, mModel);
            String responseMessage = "";
            if(json!=null) {
                try {
                    String res = json.getString("result");
                    if (res.equals("OK")) {
                        responseMessage = json.getString("message");
                    } else {
                        responseMessage = json.getString("error");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            //progressDialog.dismiss();
            ToastUtils.showToast(mActivity, R.string.feedback_submitted, ToastUtils.Duration.SHORT);
        }
    }

    public String getAndroidVersion(Context mContext) {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        String versionPrompt = mContext.getResources().getString(R.string.os_version);
        return versionPrompt + " : " + sdkVersion + " (" + release +")";
    }

    public String getDeviceName(Context mContext) {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String modelPrompt = mContext.getResources().getString(R.string.device_model);
        if (model.startsWith(manufacturer)) {
            return modelPrompt + capitalize(model);
        } else {
            return modelPrompt + " : " + capitalize(manufacturer) + " " + model;
        }
    }

    public List<String> getUserEmails(Context mContext){

        List<String> userEmails = new ArrayList<>();

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(mContext).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                if(!userEmails.contains(possibleEmail))
                    userEmails.add(possibleEmail);
            }
        }

        userEmails.add(mContext.getString(R.string.submit_anonymously));

        return userEmails;
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public void startOverlayCamera(final Activity activity, Context context, final int mode){

        final Dialog mDialog = new Dialog(activity);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.list_pick_scene);
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.setTitle(context.getResources().getString(R.string.pick_scene));
        mDialog.show();

        String[] sceneTitles = context.getResources().getStringArray(R.array.scenes);
        String[] sceneDescriptions = context.getResources().getStringArray(R.array.scenes_descriptions);
        TypedArray sceneImages = context.getResources().obtainTypedArray(R.array.scenes_images);
        ListView sceneslist = (ListView)mDialog.findViewById(R.id.listView);

        ScenesAdapter scenesAdapter = new ScenesAdapter(activity, sceneTitles, sceneDescriptions, sceneImages, R.layout.row_pick_scene);
        sceneslist.setAdapter(scenesAdapter);

        sceneslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {

                Intent i = new Intent(activity, OverlayCameraActivity.class);
                i.putExtra("mode", mode);
                i.putExtra("group", j);
                activity.startActivityForResult(i, RequestCodes.OVERLAY_CAMERA);

                mDialog.dismiss();
            }
        });
    }

    public class ScenesAdapter extends ArrayAdapter<String> {

        private String[] sceneTitles;
        private String[] sceneDescriptions;
        private TypedArray sceneImages;
        private Context context;

        public ScenesAdapter(Context _context, String[] _sceneTitles, String[] _sceneDescriptions, TypedArray _sceneImages, int row_pick_scene) {
            super(_context, row_pick_scene);
            this.sceneTitles = _sceneTitles;
            this.sceneDescriptions = _sceneDescriptions;
            this.sceneImages = _sceneImages;
            this.context = _context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.row_pick_scene, null);
            }

            TextView tt1 = (TextView) v.findViewById(R.id.scene_head);
            TextView tt2 = (TextView) v.findViewById(R.id.scene_sub);
            ImageView tt3 = (ImageView) v.findViewById(R.id.scene_image);

            if (tt1 != null) {
                tt1.setText(sceneTitles[position]);
            }

            if (tt2 != null) {
                tt2.setText(sceneDescriptions[position]);
            }

            if (tt3 != null) {
                tt3.setImageResource(sceneImages.getResourceId(position, -1));
            }

            return v;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return sceneTitles.length;
        }

    }

    public void capturePic(Activity activity, Context context){
            boolean mShouldFinish = false;
            Intent intent = new Intent(activity, StoryBoard.class);
            intent.putExtra("quick-media", DeviceUtils.getInstance().hasCamera(context)
                    ? Constants.QUICK_POST_PHOTO_CAMERA
                    : Constants.QUICK_POST_PHOTO_LIBRARY);
            intent.putExtra("isNew", true);
            intent.putExtra("shouldFinish", mShouldFinish);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
    }

    public void captureVid(Activity activity, Context context){
            boolean mShouldFinish = false;
            Intent intent = new Intent(activity, StoryBoard.class);
            intent.putExtra("quick-media", DeviceUtils.getInstance().hasCamera(context)
                    ? Constants.QUICK_POST_VIDEO_CAMERA
                    : Constants.QUICK_POST_PHOTO_LIBRARY);
            intent.putExtra("isNew", true);
            intent.putExtra("shouldFinish", mShouldFinish);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void captureAudio(Activity activity, Context context){

            boolean mShouldFinish = false;
            Intent intent = new Intent(activity, StoryBoard.class);
            intent.putExtra("quick-media", Constants.QUICK_POST_AUDIO_MIC);
            intent.putExtra("isNew", true);
            intent.putExtra("shouldFinish", mShouldFinish);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        ProfilingUtils.start("WordPress.onCreate");
        // Enable log recording
        AppLog.enableRecording(true);
        if (!PackageUtils.isDebugBuild()) {
            Fabric.with(this, new Crashlytics());
        }

        versionName = PackageUtils.getVersionName(this);
        HelpshiftHelper.init(this);
        initWpDb();
        enableHttpResponseCache(mContext);

        // EventBus setup
        EventBus.TAG = "WordPress-EVENT";
        EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .throwSubscriberException(true)
                .installDefaultEventBus();
        EventBus.getDefault().register(this);

        RestClientUtils.setUserAgent(getUserAgent());

        configureSimperium();

        // Volley networking setup
        setupVolleyQueue();

        // Refresh account informations
        if (AccountHelper.isSignedInWordPressDotCom()) {
            AccountHelper.getDefaultAccount().fetchAccountDetails();
        }

        ABTestingUtils.init();

        AppLockManager.getInstance().enableDefaultAppLockIfAvailable(this);
        if (AppLockManager.getInstance().isAppLockFeatureEnabled()) {
            AppLockManager.getInstance().getCurrentAppLock().setDisabledActivities(
                    new String[]{"org.codeforafrica.citizenreporter.starreports.ui.ShareIntentReceiverActivity"});
        }

        HelpshiftHelper.init(this);

        AnalyticsTracker.registerTracker(new AnalyticsTrackerMixpanel(getContext(), BuildConfig.MIXPANEL_TOKEN));
        AnalyticsTracker.registerTracker(new AnalyticsTrackerNosara(getContext()));
        AnalyticsTracker.init(getContext());
        AnalyticsUtils.refreshMetadata();
        AnalyticsTracker.track(Stat.APPLICATION_STARTED);

        registerForCloudMessaging(this);

        ApplicationLifecycleMonitor pnBackendMonitor = new ApplicationLifecycleMonitor();
        registerComponentCallbacks(pnBackendMonitor);
        registerActivityLifecycleCallbacks(pnBackendMonitor);

        // we want to reset the suggestion table in every launch so we can get a fresh list
        SuggestionTable.reset(wpDB.getDatabase());
    }

    // Configure Simperium and start buckets if we are signed in to WP.com
    private void configureSimperium() {
        if (AccountHelper.isSignedInWordPressDotCom()) {
            AppLog.i(T.NOTIFS, "Configuring Simperium");
            SimperiumUtils.configureSimperium(this, AccountHelper.getDefaultAccount().getAccessToken());
        }
    }

    public static void setupVolleyQueue() {
        requestQueue = Volley.newRequestQueue(mContext, VolleyUtils.getHTTPClientStack(mContext));
        imageLoader = new ImageLoader(requestQueue, getBitmapCache());
        VolleyLog.setTag(AppLog.TAG);
        // http://stackoverflow.com/a/17035814
        imageLoader.setBatchedResponseDelay(0);
    }

    private void initWpDb() {
        if (!createAndVerifyWpDb()) {
            AppLog.e(T.DB, "Invalid database, sign out user and delete database");
            currentBlog = null;
            if (wpDB != null) {
                wpDB.updateLastBlogId(-1);
            }
            // Force DB deletion
            WordPressDB.deleteDatabase(this);
            wpDB = new WordPressDB(this);
        }
    }

    private boolean createAndVerifyWpDb() {
        try {
            wpDB = new WordPressDB(this);
            // verify account data
            List<Map<String, Object>> accounts = wpDB.getAllBlogs();
            for (Map<String, Object> account : accounts) {
                if (account == null || account.get("blogName") == null || account.get("url") == null) {
                    return false;
                }
            }
            return true;
        } catch (SQLiteException sqle) {
            AppLog.e(T.DB, sqle);
            return false;
        } catch (RuntimeException re) {
            AppLog.e(T.DB, re);
            return false;
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public static RestClientUtils getRestClientUtils() {
        if (mRestClientUtils == null) {
            OAuthAuthenticator authenticator = OAuthAuthenticatorFactory.instantiate();
            mRestClientUtils = new RestClientUtils(requestQueue, authenticator, mOnAuthFailedListener);
        }
        return mRestClientUtils;
    }

    private static RestRequest.OnAuthFailedListener mOnAuthFailedListener = new RestRequest.OnAuthFailedListener() {
        @Override
        public void onAuthFailed() {
            if (getContext() == null) return;
            // If this is called, it means the WP.com token is no longer valid.
            EventBus.getDefault().post(new CoreEvents.RestApiUnauthorized());
        }
    };

    public static RestClientUtils getRestClientUtilsV1_1() {
        if (mRestClientUtilsVersion1_1 == null) {
            OAuthAuthenticator authenticator = OAuthAuthenticatorFactory.instantiate();
            mRestClientUtilsVersion1_1 = new RestClientUtils(requestQueue, authenticator, mOnAuthFailedListener, RestClient.REST_CLIENT_VERSIONS.V1_1);
        }
        return mRestClientUtilsVersion1_1;
    }

    /**
     * enables "strict mode" for testing - should NEVER be used in release builds
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void enableStrictMode() {
        // return if the build is not a debug build
        if (!BuildConfig.DEBUG) {
            AppLog.e(T.UTILS, "You should not call enableStrictMode() on a non debug build");
            return;
        }

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .penaltyFlashScreen()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects() // <-- requires Jelly Bean
                .penaltyLog()
                .build());

        AppLog.w(T.UTILS, "Strict mode enabled");
    }

    /**
     * Register the device to Google Cloud Messaging service or return registration id if it's already registered.
     *
     * @return registration id or empty string if it's not registered.
     */
    private static String gcmRegisterIfNot(Context context) {
        String regId = "";
        try {
            GCMRegistrar.checkDevice(context);
            GCMRegistrar.checkManifest(context);
            regId = GCMRegistrar.getRegistrationId(context);
            String gcmId = BuildConfig.GCM_ID;
            if (gcmId != null && TextUtils.isEmpty(regId)) {
                GCMRegistrar.register(context, gcmId);
            }
        } catch (UnsupportedOperationException e) {
            // GCMRegistrar.checkDevice throws an UnsupportedOperationException if the device
            // doesn't support GCM (ie. non-google Android)
            AppLog.e(T.NOTIFS, "Device doesn't support GCM: " + e.getMessage());
        } catch (IllegalStateException e) {
            // GCMRegistrar.checkManifest or GCMRegistrar.register throws an IllegalStateException if Manifest
            // configuration is incorrect (missing a permission for instance) or if GCM dependencies are missing
            AppLog.e(T.NOTIFS, "APK (manifest error or dependency missing) doesn't support GCM: " + e.getMessage());
        } catch (Exception e) {
            // SecurityException can happen on some devices without Google services (these devices probably strip
            // the AndroidManifest.xml and remove unsupported permissions).
            AppLog.e(T.NOTIFS, e);
        }
        return regId;
    }

    public static void registerForCloudMessaging(Context context) {
        String regId = gcmRegisterIfNot(context);

        // Register to WordPress.com notifications
        if (AccountHelper.isSignedInWordPressDotCom()) {
            if (!TextUtils.isEmpty(regId)) {
                // Send the token to WP.com in case it was invalidated
                NotificationsUtils.registerDeviceForPushNotifications(context, regId);
                AppLog.v(T.NOTIFS, "Already registered for GCM");
            }
        }

        // Register to Helpshift notifications
        if (ABTestingUtils.isFeatureEnabled(Feature.HELPSHIFT)) {
            HelpshiftHelper.getInstance().registerDeviceToken(context, regId);
        }
        AnalyticsTracker.registerPushNotificationToken(regId);
    }

    /**
     * Get the currently active blog.
     * <p/>
     * If the current blog is not already set, try and determine the last active blog from the last
     * time the application was used. If we're not able to determine the last active blog, just
     * select the first one.
     */
    public static Blog getCurrentBlog() {
        if (currentBlog == null || !wpDB.isDotComBlogVisible(currentBlog.getRemoteBlogId())) {
            attemptToRestoreLastActiveBlog();
        }

        return currentBlog;
    }

    public static Blog getCurrentBlogEvenIfNotVisible() {
        if (currentBlog == null) {
            attemptToRestoreLastActiveBlog();
        }

        return currentBlog;
    }

    /**
     * Get the blog with the specified ID.
     *
     * @param id ID of the blog to retrieve.
     * @return the blog with the specified ID, or null if blog could not be retrieved.
     */
    public static Blog getBlog(int id) {
        try {
            return wpDB.instantiateBlogByLocalId(id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Set the last active blog as the current blog.
     *
     * @return the current blog
     */
    public static Blog setCurrentBlogToLastActive() {
        List<Map<String, Object>> accounts = WordPress.wpDB.getVisibleBlogs();

        int lastBlogId = WordPress.wpDB.getLastBlogId();
        if (lastBlogId != -1) {
            for (Map<String, Object> account : accounts) {
                int id = Integer.valueOf(account.get("id").toString());
                if (id == lastBlogId) {
                    setCurrentBlog(id);
                    return currentBlog;
                }
            }
        }
        // Previous active blog is hidden or deleted
        currentBlog = null;
        return null;
    }

    /**
     * Set the blog with the specified id as the current blog.
     *
     * @param id id of the blog to set as current
     * @return the current blog
     */
    public static Blog setCurrentBlog(int id) {
        currentBlog = wpDB.instantiateBlogByLocalId(id);
        return currentBlog;
    }

    /**
     * returns the blogID of the current blog or -1 if current blog is null
     */
    public static int getCurrentRemoteBlogId() {
        return (getCurrentBlog() != null ? getCurrentBlog().getRemoteBlogId() : -1);
    }

    public static int getCurrentLocalTableBlogId() {
        return (getCurrentBlog() != null ? getCurrentBlog().getLocalTableBlogId() : -1);
    }

    public static void signOutWordPressComAsyncWithProgressBar(Context context) {
        new SignOutWordPressComAsync(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Sign out from wpcom account
     */
    public static void WordPressComSignOut(Context context) {
        removeWpComUserRelatedData(context);

        // broadcast an event: wpcom user signed out
        EventBus.getDefault().post(new UserSignedOutWordPressCom());

        // broadcast an event only if the user is completly signed out
        if (!AccountHelper.isSignedIn()) {
            EventBus.getDefault().post(new UserSignedOutCompletely());
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(UserSignedOutCompletely event) {
        try {
            SelfSignedSSLCertsManager.getInstance(getContext()).emptyLocalKeyStoreFile();
        } catch (GeneralSecurityException e) {
            AppLog.e(T.UTILS, "Error while cleaning the Local KeyStore File", e);
        } catch (IOException e) {
            AppLog.e(T.UTILS, "Error while cleaning the Local KeyStore File", e);
        }

        flushHttpCache();

        // Analytics resets
        AnalyticsTracker.endSession(false);
        AnalyticsTracker.clearAllData();

        // disable passcode lock
        AbstractAppLock appLock = AppLockManager.getInstance().getCurrentAppLock();
        if (appLock != null) {
            appLock.setPassword(null);
        }

        // dangerously delete all content!
        wpDB.dangerouslyDeleteAllContent();
    }

    public static class SignOutWordPressComAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog mProgressDialog;
        WeakReference<Context> mWeakContext;

        public SignOutWordPressComAsync(Context context) {
            mWeakContext = new WeakReference<Context>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Context context = mWeakContext.get();
            if (context != null) {
                mProgressDialog = ProgressDialog.show(context, null, context.getText(R.string.signing_out));
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            Context context = mWeakContext.get();
            if (context != null) {
                WordPressComSignOut(context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    public static void removeWpComUserRelatedData(Context context) {
        // cancel all Volley requests - do this before unregistering push since that uses
        // a Volley request
        VolleyUtils.cancelAllRequests(requestQueue);

        NotificationsUtils.unregisterDevicePushNotifications(context);
        try {
            GCMRegistrar.checkDevice(context);
            GCMRegistrar.unregister(context);
        } catch (Exception e) {
            AppLog.v(T.NOTIFS, "Could not unregister for GCM: " + e.getMessage());
        }

        // delete wpcom blogs
        wpDB.deleteWordPressComBlogs(context);

        // reset default account
        AccountHelper.getDefaultAccount().signout();

        // reset all reader-related prefs & data
        AppPrefs.reset();
        ReaderDatabase.reset();

        // Reset Stats Data
        StatsDatabaseHelper.getDatabase(context).reset();

        // Reset Simperium buckets (removes local data)
        SimperiumUtils.resetBucketsAndDeauthorize();
    }

    public static String getLoginUrl(Blog blog) {
        String loginURL = null;
        Gson gson = new Gson();
        Type type = new TypeToken<Map<?, ?>>() {
        }.getType();
        Map<?, ?> blogOptions = gson.fromJson(blog.getBlogOptions(), type);
        if (blogOptions != null) {
            Map<?, ?> homeURLMap = (Map<?, ?>) blogOptions.get("login_url");
            if (homeURLMap != null)
                loginURL = homeURLMap.get("value").toString();
        }
        // Try to guess the login URL if blogOptions is null (blog not added to the app), or WP version is < 3.6
        if (loginURL == null) {
            if (blog.getUrl().lastIndexOf("/") != -1) {
                return blog.getUrl().substring(0, blog.getUrl().lastIndexOf("/"))
                        + "/wp-login.php";
            } else {
                return blog.getUrl().replace("xmlrpc.php", "wp-login.php");
            }
        }

        return loginURL;
    }

    /**
     * User-Agent string when making HTTP connections, for both API traffic and WebViews.
     * Follows the format detailed at http://tools.ietf.org/html/rfc2616#section-14.43,
     * ie: "AppName/AppVersion (OS Version; Locale; Device)"
     *    "wp-android/2.6.4 (Android 4.3; en_US; samsung GT-I9505/jfltezh)"
     *    "wp-android/2.6.3 (Android 4.4.2; en_US; LGE Nexus 5/hammerhead)"
     * Note that app versions prior to 2.7 simply used "wp-android" as the user agent
     **/
    private static final String USER_AGENT_APPNAME = "wp-android";
    private static String mUserAgent;
    public static String getUserAgent() {
        if (mUserAgent == null) {
            mUserAgent = USER_AGENT_APPNAME + "/" + PackageUtils.getVersionName(getContext())
                       + " (Android " + Build.VERSION.RELEASE + "; "
                       + Locale.getDefault().toString() + "; "
                       + Build.MANUFACTURER + " " + Build.MODEL + "/" + Build.PRODUCT + ")";
        }
        return mUserAgent;
    }

    /*
     * enable caching for HttpUrlConnection
     * http://developer.android.com/training/efficient-downloads/redundant_redundant.html
     */
    private static void enableHttpResponseCache(Context context) {
        try {
            long httpCacheSize = 5 * 1024 * 1024; // 5MB
            File httpCacheDir = new File(context.getCacheDir(), "http");
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            AppLog.w(T.UTILS, "Failed to enable http response cache");
        }
    }

    private static void flushHttpCache() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    private static void attemptToRestoreLastActiveBlog() {
        if (setCurrentBlogToLastActive() == null) {
            // fallback to just using the first blog
            List<Map<String, Object>> accounts = WordPress.wpDB.getVisibleBlogs();
            if (accounts.size() > 0) {
                int id = Integer.valueOf(accounts.get(0).get("id").toString());
                setCurrentBlog(id);
                wpDB.updateLastBlogId(id);
            }
        }
    }

    /**
     * Detect when the app goes to the background and come back to the foreground.
     *
     * Turns out that when your app has no more visible UI, a callback is triggered.
     * The callback, implemented in this custom class, is called ComponentCallbacks2 (yes, with a two).
     *
     * This class also uses ActivityLifecycleCallbacks and a timer used as guard,
     * to make sure to detect the send to background event and not other events.
     *
     */
    private class ApplicationLifecycleMonitor implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
        private final int DEFAULT_TIMEOUT = 2 * 60; // 2 minutes
        private Date lastPingDate;
        private Date mApplicationOpenedDate;
        boolean isInBackground = true;

        @Override
        public void onConfigurationChanged(final Configuration newConfig) {
        }

        @Override
        public void onLowMemory() {
        }

        @Override
        public void onTrimMemory(final int level) {
            if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
                // We're in the Background
                isInBackground = true;
                String lastActivityString = AppPrefs.getLastActivityStr();
                ActivityId lastActivity = ActivityId.getActivityIdFromName(lastActivityString);
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("last_visible_screen", lastActivity.toString());
                if (mApplicationOpenedDate != null) {
                    Date now = new Date();
                    properties.put("time_in_app", DateTimeUtils.secondsBetween(now, mApplicationOpenedDate));
                    mApplicationOpenedDate = null;
                }
                AnalyticsTracker.track(AnalyticsTracker.Stat.APPLICATION_CLOSED, properties);
                AnalyticsTracker.endSession(false);
            } else {
                isInBackground = false;
            }

            boolean evictBitmaps = false;
            switch (level) {
                case TRIM_MEMORY_COMPLETE:
                case TRIM_MEMORY_MODERATE:
                case TRIM_MEMORY_RUNNING_MODERATE:
                case TRIM_MEMORY_RUNNING_CRITICAL:
                case TRIM_MEMORY_RUNNING_LOW:
                    evictBitmaps = true;
                    break;
                default:
                    break;
            }

            if (evictBitmaps && mBitmapCache != null) {
                mBitmapCache.evictAll();
            }
        }

        private boolean isPushNotificationPingNeeded() {
            if (lastPingDate == null) {
                // first startup
                return false;
            }

            Date now = new Date();
            if (DateTimeUtils.secondsBetween(now, lastPingDate) >= DEFAULT_TIMEOUT) {
                lastPingDate = now;
                return true;
            }
            return false;
        }

        /**
         * Check if user has valid credentials, and that at least 2 minutes are passed
         * since the last ping, then try to update the PN token.
         */
        private void updatePushNotificationTokenIfNotLimited() {
            // Synch Push Notifications settings
            if (isPushNotificationPingNeeded() && AccountHelper.isSignedInWordPressDotCom()) {
                String token = null;
                try {
                    // Register for Google Cloud Messaging
                    GCMRegistrar.checkDevice(mContext);
                    GCMRegistrar.checkManifest(mContext);
                    token = GCMRegistrar.getRegistrationId(mContext);
                    String gcmId = BuildConfig.GCM_ID;
                    if (gcmId == null || token == null || token.equals("") ) {
                        AppLog.e(T.NOTIFS, "Could not ping the PNs backend, Token or gmcID not found");
                    } else {
                        // Send the token to WP.com
                        NotificationsUtils.registerDeviceForPushNotifications(mContext, token);
                    }
                } catch (Exception e) {
                    AppLog.e(T.NOTIFS, "Could not ping the PNs backend: " + e.getMessage());
                }
            }
        }

        /**
         * This method is called when:
         * 1. the app starts (but it's not opened by a service, i.e. an activity is resumed)
         * 2. the app was in background and is now foreground
         */
        public void onFromBackground() {
            AnalyticsUtils.refreshMetadata();
            mApplicationOpenedDate = new Date();
            AnalyticsTracker.track(AnalyticsTracker.Stat.APPLICATION_OPENED);
            if (NetworkUtils.isNetworkAvailable(mContext)) {
                // Rate limited PN Token Update
                updatePushNotificationTokenIfNotLimited();

                // Rate limited WPCom blog list Update
                sUpdateWordPressComBlogList.runIfNotLimited();

                // Rate limited blog options Update
                sUpdateCurrentBlogOption.runIfNotLimited();
            }

            sDeleteExpiredStats.runIfNotLimited();
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (isInBackground) {
                // was in background before
                onFromBackground();
            }
            isInBackground = false;
        }

        @Override
        public void onActivityCreated(Activity arg0, Bundle arg1) {
        }

        @Override
        public void onActivityDestroyed(Activity arg0) {
        }

        @Override
        public void onActivityPaused(Activity arg0) {
            lastPingDate = new Date();
        }

        @Override
        public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {
        }

        @Override
        public void onActivityStarted(Activity arg0) {
        }

        @Override
        public void onActivityStopped(Activity arg0) {
        }
    }


    //GCM stuff
    private  final int MAX_ATTEMPTS = 5;
    private  final int BACKOFF_MILLI_SECONDS = 2000;
    private  final Random random = new Random();


    // Register this account with the server.
    public void register(final Context context, final String regId) {

        Log.i(GCMConfigORG.TAG, "registering device (regId = " + regId + ")");

        String serverUrl = GCMConfigORG.YOUR_SERVER_URL;

        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {

            Log.d(GCMConfigORG.TAG, "Attempt #" + i + " to register");

            //Send Broadcast to Show message on screen
            //displayMessageOnScreen(context, context.getString( R.string.server_registering, i, MAX_ATTEMPTS));

            // Post registration values to web server
            //post(serverUrl, params);

            //update user profile with device id
            if(WordPress.currentBlog!=null) {
                String username = WordPress.getCurrentBlog().getUsername();

                APIFunctions userFunction = new APIFunctions();
                JSONObject json = userFunction.updateUserDevice(regId, username);
                String responseMessage = "";
                if(json!=null) {
                    try {
                        String res = json.getString("result");
                        if (res.equals("OK")) {
                            responseMessage = json.getString("message");

                            //set device registered in preferences
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("rD", "1");
                            editor.commit();

                        } else {
                            responseMessage = json.getString("error");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    GCMRegistrar.setRegisteredOnServer(context, true);

                    //Send Broadcast to Show message on screen
                    String message = context.getString(R.string.server_registered);
                    //displayMessageOnScreen(context, message);
                }
            }
            return;
        }

        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);

        //Send Broadcast to Show message on screen
        //ageOnScreen(context, message);
    }
    //Function to display simple Alert Dialog
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Set Dialog Title
        alertDialog.setTitle(title);

        // Set Dialog Message
        alertDialog.setMessage(message);

        if(status != null)
            // Set alert dialog icon
            alertDialog.setIcon( R.drawable.app_icon);

        // Set OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Show Alert Message
        alertDialog.show();
    }



    // Unregister this account/device pair within the server.
    public void unregister(final Context context, final String regId) {

        Log.i(GCMConfigORG.TAG, "unregistering device (regId = " + regId + ")");

        String serverUrl = GCMConfigORG.YOUR_SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);

        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            //ageOnScreen(context, message);
        } catch (IOException e) {

            // At this point the device is unregistered from GCM, but still
            // registered in the our server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.

            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            //ageOnScreen(context, message);
        }
    }

    // Issue a POST request to the server.
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        try {

            url = new URL(endpoint);

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }

        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();

        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }

        String body = bodyBuilder.toString();

        Log.v(GCMConfigORG.TAG, "Posting '" + body + "' to " + url);

        byte[] bytes = body.getBytes();

        HttpURLConnection conn = null;
        try {

            Log.e("URL", "> " + url);

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();

            // handle the response
            int status = conn.getResponseCode();

            // If response is not success
            if (status != 200) {

                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }



    // Checking for all possible internet providers
    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    // Notifies UI to display a message.
    public void displayMessageOnScreen(Context context, String message) {

        Intent intent = new Intent(GCMConfigORG.DISPLAY_MESSAGE_ACTION);
        intent.putExtra(GCMConfigORG.EXTRA_MESSAGE, message);

        // Send Broadcast to Broadcast receiver with message
        context.sendBroadcast(intent);

    }
    private PowerManager.WakeLock wakeLock;

    public  void acquireWakeLock(Context context) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "WakeLock");

        wakeLock.acquire();
    }

    public  void releaseWakeLock() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }
}
