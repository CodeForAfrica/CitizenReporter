package org.codeforafrica.citizenreporter.starreports.ui.posts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.codeforafrica.citizenreporter.starreports.BuildConfig;
import org.codeforafrica.citizenreporter.starreports.Constants;
import org.codeforafrica.citizenreporter.starreports.R;
import org.codeforafrica.citizenreporter.starreports.WordPress;
import org.w3c.dom.Text;
import org.wordpress.android.analytics.AnalyticsTracker;
import org.codeforafrica.citizenreporter.starreports.chat.ChatActivity;
import org.codeforafrica.citizenreporter.starreports.models.Blog;
import org.codeforafrica.citizenreporter.starreports.models.Post;
import org.codeforafrica.citizenreporter.starreports.models.PostLocation;
import org.codeforafrica.citizenreporter.starreports.models.PostStatus;
import org.codeforafrica.citizenreporter.starreports.ui.RequestCodes;
import org.codeforafrica.citizenreporter.starreports.ui.media.WordPressMediaUtils;
import org.codeforafrica.citizenreporter.starreports.ui.media.services.MediaUploadService;
import org.codeforafrica.citizenreporter.starreports.ui.posts.adapters.GuideArrayAdapter;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.EditTextUtils;
import org.wordpress.android.util.GeocoderUtils;
import org.wordpress.android.util.MediaUtils;
import org.wordpress.android.util.NetworkUtils;
import org.wordpress.android.util.StringUtils;
import org.wordpress.android.util.ToastUtils;
import org.wordpress.android.util.helpers.LocationHelper;
import org.wordpress.android.util.helpers.MediaFile;
import org.wordpress.passcodelock.AppLockManager;
import org.codeforafrica.citizenreporter.starreports.wallet.ConfirmPayment;
import org.codeforafrica.citizenreporter.starreports.wallet.Payment;

import info.hoang8f.widget.FButton;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class StoryBoard extends ActionBarActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener, View.OnClickListener, TextView.OnEditorActionListener{

    private SliderLayout mDemoSlider;

    private LinearLayout summaryPane;
    private LinearLayout guidePane;

    private TextView displaySummary;
    private ImageView thumbSummary;
    private TextView displayDate;
    private TextView displayDate_Calendar;
    private ImageView thumbDate;
    private TextView displayWho;
    private ImageView thumbWho;
    private TextView displayLocation;
    private ImageView thumbLocation;
    private TextView displayCause;
    private ImageView thumbCause;

    private RelativeLayout yesMedia;
    private RelativeLayout noMedia;

    private TextView text_summary;
    private TextView text_template;


    private FButton enableLocation;

    private Dialog questionDialog;
    private FButton submitButton;
    private EditText editTextSummary;

    public static final String EXTRA_POSTID = "selectedId";
    public static final String EXTRA_IS_PAGE = "isPage";
    public static final String EXTRA_IS_NEW_POST = "isNewPost";
    public static final String EXTRA_IS_QUICKPRESS = "isQuickPress";
    public static final String EXTRA_QUICKPRESS_BLOG_ID = "quickPressBlogId";
    public static final String EXTRA_SAVED_AS_LOCAL_DRAFT = "savedAsLocalDraft";
    public static final String EXTRA_SHOULD_REFRESH = "shouldRefresh";
    public static final String STATE_KEY_CURRENT_POST = "stateKeyCurrentPost";
    public static final String STATE_KEY_ORIGINAL_POST = "stateKeyOriginalPost";
    private Post mOriginalPost;
    private boolean mIsNewPost;
    private boolean mIsPage;
    private LinearLayout button_camera;
    private LinearLayout button_video;
    private LinearLayout button_mic;
    private String mMediaCapturePath = "";

    private HashMap<String,File> media_map;
    private HashMap<String, String> media_map_remote;

    private boolean hasMedia = false;

    private Post mPost;
    public static final String NEW_MEDIA_GALLERY = "NEW_MEDIA_GALLERY";
    public static final String NEW_MEDIA_POST = "NEW_MEDIA_POST";
    private boolean mMediaUploadServiceStarted;

    private TextView mPayment;
    private String own_price;

    private Payment payment;
    private LinearLayout confirmLayout ;
    private ImageView confirmIcon;
    private TextView confirmText;

    private LinearLayout disputeLayout;
    private ImageView disputeIcon;
    private TextView disputeText;
    private RelativeLayout followUpLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_board_icons);
        //get post
        long selectedId = getIntent().getLongExtra("selectedId", 0);
        Bundle extras = getIntent().getExtras();
        String action = getIntent().getAction();
        if (savedInstanceState == null) {
            if (Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action)
                    || NEW_MEDIA_GALLERY.equals(action)
                    || NEW_MEDIA_POST.equals(action)
                    || getIntent().hasExtra(EXTRA_IS_QUICKPRESS)
                    || (extras != null && extras.getInt("quick-media", -1) > -1)) {
                if (getIntent().hasExtra(EXTRA_QUICKPRESS_BLOG_ID)) {
                    // QuickPress might want to use a different blog than the current blog
                    int blogId = getIntent().getIntExtra(EXTRA_QUICKPRESS_BLOG_ID, -1);
                    Blog quickPressBlog = WordPress.wpDB.instantiateBlogByLocalId(blogId);
                    if (quickPressBlog == null) {
                        showErrorAndFinish(R.string.blog_not_found);
                        return;
                    }
                    if (quickPressBlog.isHidden()) {
                        showErrorAndFinish(R.string.error_blog_hidden);
                        return;
                    }
                    WordPress.currentBlog = quickPressBlog;
                }

                // Create a new post for share intents and QuickPress
                mPost = new Post(WordPress.getCurrentLocalTableBlogId(), false);
                WordPress.wpDB.savePost(mPost);
                mIsNewPost = true;

            } else if (extras != null) {
                // Load post from the postId passed in extras
                long localTablePostId = extras.getLong(EXTRA_POSTID, -1);
                mIsPage = extras.getBoolean(EXTRA_IS_PAGE);
                mIsNewPost = extras.getBoolean(EXTRA_IS_NEW_POST);
                mPost = WordPress.wpDB.getPostForLocalTablePostId(localTablePostId, false);
                mOriginalPost = WordPress.wpDB.getPostForLocalTablePostId(localTablePostId, false);
            } else {
                // A postId extra must be passed to this activity
                showErrorAndFinish(R.string.post_not_found);
                return;
            }
        } else {
            if (savedInstanceState.containsKey(STATE_KEY_ORIGINAL_POST)) {
                try {
                    mPost = (Post) savedInstanceState.getSerializable(STATE_KEY_CURRENT_POST);
                    mOriginalPost = (Post) savedInstanceState.getSerializable(STATE_KEY_ORIGINAL_POST);
                } catch (ClassCastException e) {
                    mPost = null;
                }
            }
        }

        //check for quick capture actions
        int quickMediaType = getIntent().getIntExtra("quick-media", -1);

        if (quickMediaType >= 0) {
            // User selected 'Quick Photo' in the menu drawer
            if (quickMediaType == Constants.QUICK_POST_PHOTO_CAMERA) {
                launchCamera();
            } else if(quickMediaType == Constants.QUICK_POST_VIDEO_CAMERA) {
                launchVideoCamera();
            }else if(quickMediaType == Constants.QUICK_POST_AUDIO_MIC) {
                launchMic();
            }else if (quickMediaType == Constants.QUICK_POST_PHOTO_LIBRARY) {
                WordPressMediaUtils.launchPictureLibrary(this);
            }
            if (mPost != null) {
                mPost.setQuickPostType(Post.QUICK_MEDIA_TYPE_PHOTO);
            }
        }

        //is this from assignment
        int assignmentID = getIntent().getIntExtra("assignment_id", 0);
        if(assignmentID != 0){
            mPost.setAssignment_id(assignmentID);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getResources().getString(R.string.story_board));

        media_map = new HashMap<String, File>();
        media_map_remote = new HashMap<String, String>();
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        summaryPane = (LinearLayout)findViewById(R.id.summaryPane);
        guidePane = (LinearLayout)findViewById(R.id.guidePane);

        displaySummary = (TextView)findViewById(R.id.displaySummary);
        thumbSummary = (ImageView)findViewById(R.id.thumbSummary);
        displayWho = (TextView)findViewById(R.id.displayWho);
        thumbWho = (ImageView)findViewById(R.id.thumbWho);
        displayLocation = (TextView)findViewById(R.id.displayLocation);
        thumbLocation = (ImageView)findViewById(R.id.thumbLocation);
        displayDate = (TextView)findViewById(R.id.displayDate);
        displayDate_Calendar = (TextView)findViewById(R.id.displayDate_Calendar);
        thumbDate = (ImageView)findViewById(R.id.thumbDate);
        displayCause = (TextView)findViewById(R.id.displayCause);
        thumbCause = (ImageView)findViewById(R.id.thumbCause);

        yesMedia = (RelativeLayout)findViewById(R.id.yesMediaPane);
        noMedia = (RelativeLayout)findViewById(R.id.noMediaPane);

        text_summary = (TextView)findViewById(R.id.text_summary);
        text_template= (TextView)findViewById(R.id.text_template);

        if(mPost !=null)
            payment = WordPress.wpDB.getPostPayment(mPost.getRemotePostId());

        mPayment = (TextView)findViewById(R.id.payment);
        //TODO: if assignment set bounty & disable click
        //set own price as custom field

        mPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(mPost.isLocalDraft()){

                        setPriceDialog();

                    }else{

                        showPaymentDialog();
                    }
            }
        });
        /*
        text_summary.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean showTemplate = false;
                togglePanes(showTemplate);
            }
        });
        text_template.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean showTemplate = true;
                togglePanes(showTemplate);
            }
        });
        */


        //quick capture icons
        button_camera = (LinearLayout)findViewById(R.id.button_camera);

        button_video = (LinearLayout)findViewById(R.id.button_video);
        button_mic = (LinearLayout)findViewById(R.id.button_mic);
        //
        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        button_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchVideoCamera();
            }
        });

        button_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchMic();

            }
        });

        //set up questionnaire
        CardView summaryButton = (CardView)findViewById(R.id.summaryButton);
        summaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswerQuestionDialog(0, displaySummary, thumbSummary);
            }
        });
        CardView locationButton = (CardView)findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswerQuestionDialog(1, displayLocation, thumbLocation);
            }
        });
        CardView entitiesButton = (CardView)findViewById(R.id.entitiesButton);
        entitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswerQuestionDialog(2, displayWho, thumbWho);
            }
        });
        CardView dateButton = (CardView)findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswerQuestionDialog(3, displayDate, thumbDate);
            }
        });
        CardView howButton = (CardView)findViewById(R.id.howButton);
        howButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswerQuestionDialog(4, displayCause, thumbCause);
            }
        });



        //TODO: need for checking twice?
         if (mPost != null) {
            WordPress.currentPost = mPost;
            loadPost(WordPress.currentPost);
        }else{
             mPost = new Post(WordPress.getCurrentLocalTableBlogId(), false);
             WordPress.currentPost = mPost;
             WordPress.wpDB.savePost(mPost);
             mIsNewPost = true;
         }

        /*
        TODO: doesn't work, but will come back to it
        ArrayList<MediaFile> postMedia = WordPress.wpDB.getMediaFilesForPost(mPost);

        if(postMedia.size()>0){
            for(int i = 0; i<postMedia.size(); i++){
                generateThumbAndAddToSlider(postMedia.get(i));
            }
        }
        */
        if(!mPost.isLocalDraft()) {
            hideEditFeatures();

        }
        setUpPayment();

        getAndSetThumbnails();

        //setUpQuestionnaire();
    }

    public void setPriceDialog(){
        final Dialog setPriceDialog = new Dialog(StoryBoard.this);
        setPriceDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setPriceDialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        setPriceDialog.setContentView(R.layout.fivew_fragment);
        setPriceDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        final FButton submitButton = (FButton)setPriceDialog.findViewById(R.id.submitButton);
        submitButton.setEnabled(false);

        final EditText editTextSummary = (EditText)setPriceDialog.findViewById(R.id.editTextSummary);
        editTextSummary.setText("0.00");

        //find current value of summary
        own_price = "" + mPost.getOwn_price();
        //if it's not default & not empty edit editTextSummary
        if(!own_price.equals("")){
            editTextSummary.setText(own_price);
            submitButton.setEnabled(true);
        }

        editTextSummary.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String newSummary = "" + editTextSummary.getText().toString();
                if (newSummary.length() > 0) {
                    own_price = newSummary;
                    submitButton.setEnabled(true);
                } else {
                    own_price = "";
                    submitButton.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        setPriceDialog.findViewById(R.id.closeDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPriceDialog.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (own_price.trim().length() > 0) {
                    mPayment.setText(own_price);
                    if (!own_price.equals("")) {
                        mPost.setOwn_price(own_price);
                    }
                } else {
                    mPayment.setText(getResources().getString(R.string.own_price));
                }
                setPriceDialog.dismiss();
            }
        });


        setPriceDialog.show();
    }

    public void setUpPayment(){
        if(!mPost.isLocalDraft()){
            if(payment == null){
                mPayment.setText(getResources().getString(R.string.no_payment_yet));
            }else{
                String confirmed = getApplicationContext().getResources().getString(R.string.not_confirmed); ;
                if(payment.getConfirmed().equals("1")){
                    confirmed = getApplicationContext().getResources().getString(R.string.confirmed);
                }else if(payment.getConfirmed().equals("-1")){
                    confirmed = getApplicationContext().getResources().getString(R.string.disputed);
                }
                mPayment.setText(payment.getAmount()+" : "+confirmed);
            }
        }else{
            if(mPost.getOwn_price()!=null)
                if(mPost.getOwn_price().trim().length()>0) {
                    mPayment.setText(mPost.getOwn_price());
                }
        }
    }

    public void hideEditFeatures(){
        findViewById(R.id.bottom_action_buttons).setVisibility(View.GONE);

    }

    public void getAndSetThumbnails(){
        String mediaPaths;

        if(mPost.isLocalDraft()){
            mediaPaths = StringUtils.notNullStr(mPost.getMediaPaths());
        }else{
            mediaPaths = StringUtils.notNullStr(mPost.getRemoteMediaPaths());
        }

        if(mediaPaths!=""){
            String[] mediaPaths_parts = mediaPaths.split("-:-");
            //for(int i = 0; i<mediaPaths_parts.length; i++){
            for(String mediaPath : mediaPaths_parts)
                if(!mediaPath.trim().equals("") && !mediaPath.trim().equals("null")){
                    //TODO: set caption
                    Random randomGenerator = new Random();

                    if(mPost.isLocalDraft()){
                        File thumb = new File(mediaPath);
                        if (thumb.exists()) {
                            media_map.put(String.valueOf(randomGenerator.nextInt(10000)), thumb);
                            setUpSlider();
                        }
                    }else{
                            media_map_remote.put(String.valueOf(randomGenerator.nextInt(10000)), mediaPath);
                            setUpSlider();
                    }

                }
            }
    }


    public void showPaymentDialog(){
        final Dialog mDialog = new Dialog(StoryBoard.this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialog.setContentView(R.layout.payment_row);


        confirmLayout = (LinearLayout) mDialog.findViewById(R.id.confirm_layout);
        confirmIcon = (ImageView) mDialog.findViewById(R.id.confirm_icon);
        confirmText = (TextView) mDialog.findViewById(R.id.confirm_text);

        disputeLayout = (LinearLayout) mDialog.findViewById(R.id.dispute_layout);
        disputeIcon = (ImageView) mDialog.findViewById(R.id.dispute_icon);
        disputeText = (TextView) mDialog.findViewById(R.id.dispute_text);
        followUpLayout = (RelativeLayout) mDialog.findViewById(R.id.followup_layout);

        if(payment == null){
            //payment hasn't been made yet!
            ((TextView)mDialog.findViewById(R.id.message_text)).setText(getResources().getString(R.string.no_payment_yet));

            //hide dispute and confirm
            confirmLayout.setVisibility(View.GONE);
            disputeLayout.setVisibility(View.GONE);

            //show followUp
            followUpLayout.setVisibility(View.VISIBLE);
        }else{
            ((TextView)mDialog.findViewById(R.id.message_text)).setText(StringUtils.notNullStr(payment.getMessage()));
            if(payment.getConfirmed().equals("1")){
                paymentConfirmed(payment, true, false);
            }else if(payment.getConfirmed().equals("-1")){
                paymentConfirmed(payment, true, false);
            }else{
                followUpLayout.setVisibility(View.VISIBLE);
                disputeLayout.setVisibility(View.GONE);
                confirmLayout.setVisibility(View.GONE);
            }
        }

        confirmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentConfirmed(payment, true, true);
            }
        });

        disputeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentConfirmed(payment, false, true);

            }
        });

        followUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryBoard.this, ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mDialog.show();
    }

    public void paymentConfirmed(Payment payment, boolean isConfirmed, boolean update){

        if(isConfirmed){
            confirmIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.color_primary), android.graphics.PorterDuff.Mode.MULTIPLY);
            confirmText.setText(getApplicationContext().getResources().getString(R.string.confirmed));
            disputeLayout.setVisibility(View.GONE);
            followUpLayout.setVisibility(View.GONE);
        }else{
            disputeIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.alert_red), android.graphics.PorterDuff.Mode.MULTIPLY);
            disputeText.setText(getApplicationContext().getResources().getString(R.string.disputed));
            confirmLayout.setVisibility(View.GONE);
            //show follow up button: takes user to chatactivity
            followUpLayout.setVisibility(View.VISIBLE);
        }

        if(update){

            String confirm;

            if(isConfirmed){
                confirm = "1";
                payment.setConfirmed("1");
                WordPress.wpDB.updatePayment(payment);
            }else{
                confirm = "0";
                payment.setConfirmed("-1");
                WordPress.wpDB.updatePayment(payment);
            }

            //send query
            new ConfirmPayment(payment.getPost(), payment.getRemoteID(), confirm).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mPost.isLocalDraft()){
            getMenuInflater().inflate(R.menu.storyboard_menu, menu);
        }
        return true;
    }


    private void launchMic(){
        WordPressMediaUtils.launchMic(this, new WordPressMediaUtils.LaunchRecorderCallback() {
            @Override
            public void onMediaRecorderPathReady(String mediaCapturePath) {
                mMediaCapturePath = mediaCapturePath;
                AppLockManager.getInstance().setExtendedTimeout();
            }
        });
    }
    private void launchCamera() {
        startOverlayCamera(1);
    }
    private void launchVideoCamera() {
        startOverlayCamera(2);
    }
    public void startOverlayCamera(int type){

        (new WordPress()).startOverlayCamera(StoryBoard.this, getApplicationContext(), type);

    }
    /*
    private void launchVideoCamera() {

        WordPressMediaUtils.launchVideoCamera(this);
        AppLockManager.getInstance().setExtendedTimeout();
    }
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null || ((requestCode == RequestCodes.TAKE_PHOTO ||
                requestCode == RequestCodes.TAKE_VIDEO || requestCode == RequestCodes.OVERLAY_CAMERA))) {

            Log.d("result", "" + data + ":" + requestCode + ":" + resultCode);

            switch (requestCode) {

                case RequestCodes.OVERLAY_CAMERA:
                    if(resultCode == 1){
                        WordPressMediaUtils.launchCamera(this, new WordPressMediaUtils.LaunchCameraCallback() {
                            @Override
                            public void onMediaCapturePathReady(String mediaCapturePath) {
                                mMediaCapturePath = mediaCapturePath;
                                AppLockManager.getInstance().setExtendedTimeout();
                            }
                        });
                    }else if(resultCode == 2) {
                        WordPressMediaUtils.launchVideoCamera_SD(this, new WordPressMediaUtils.LaunchVideoCameraCallback() {
                            @Override
                            public void onMediaCapturePathReady(String mediaCapturePath) {
                                mMediaCapturePath = mediaCapturePath;
                                AppLockManager.getInstance().setExtendedTimeout();
                            }
                        });
                    }
                    break;

                case RequestCodes.TAKE_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            File f = new File(mMediaCapturePath);
                            if (!addMedia(f)) {
                                ToastUtils.showToast(this, R.string.gallery_error, ToastUtils.Duration.SHORT);
                            }
                            //this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                             //       + Environment.getExternalStorageDirectory())));
                            AnalyticsTracker.track(AnalyticsTracker.Stat.EDITOR_ADDED_PHOTO_VIA_LOCAL_LIBRARY);
                        } catch (RuntimeException e) {
                            AppLog.e(AppLog.T.POSTS, e);
                        } catch (OutOfMemoryError e) {
                            AppLog.e(AppLog.T.POSTS, e);
                        }
                    } /*else if (TextUtils.isEmpty(mEditorFragment.getContent())) {
                        // TODO: check if it was mQuickMediaType > -1
                        // Quick Photo was cancelled, delete post and finish activity
                        WordPress.wpDB.deletePost(getPost());
                        finish();
                    }*/
                    break;
                case RequestCodes.TAKE_VIDEO:
                    if (resultCode == Activity.RESULT_OK) {
                        //Uri capturedVideoUri = MediaUtils.getLastRecordedVideoUri(this);
                        File f = new File(mMediaCapturePath);
                        if (!addMedia(f)) {
                            ToastUtils.showToast(this, R.string.gallery_error, ToastUtils.Duration.SHORT);
                        }
                    }/* else if (TextUtils.isEmpty(mEditorFragment.getContent())) {
                        // TODO: check if it was mQuickMediaType > -1
                        // Quick Photo was cancelled, delete post and finish activity
                        WordPress.wpDB.deletePost(getPost());
                        finish();
                    }*/
                    break;
                case RequestCodes.TAKE_AUDIO:
                     if(resultCode == Activity.RESULT_OK){
                         File f = new File(mMediaCapturePath);
                         if (!addMedia(f)) {
                             ToastUtils.showToast(this, R.string.gallery_error, ToastUtils.Duration.SHORT);
                         }
                     }
                    break;
            }
        }else if(requestCode == RequestCodes.START_LOCATION_SERVICE){
            initLocation();
        }
    }
    private void queueFileForUpload(File file, boolean isVideoThumb) {
        // Invalid file path
        if (TextUtils.isEmpty(file.getAbsolutePath())) {
            Toast.makeText(this, R.string.editor_toast_invalid_path, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!file.exists()) {
            Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();

        }else{

            Blog blog = WordPress.getCurrentBlog();
            long currentTime = System.currentTimeMillis();
            String mimeType = MediaUtils.getMediaFileMimeType(file);
            String fileName = MediaUtils.getMediaFileName(file, mimeType);
            MediaFile mediaFile = new MediaFile();

            mediaFile.setBlogId(String.valueOf(blog.getLocalTableBlogId()));
            mediaFile.setFileName(fileName);
            mediaFile.setFilePath(file.getAbsolutePath());
            mediaFile.setUploadState("queued");
            mediaFile.setDateCreatedGMT(currentTime);
            mediaFile.setMediaId(String.valueOf(currentTime));
            mediaFile.setPostID(mPost.getLocalTablePostId());
            if (mimeType != null && mimeType.startsWith("image")) {
                // get width and height
                BitmapFactory.Options bfo = new BitmapFactory.Options();
                bfo.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), bfo);
                mediaFile.setWidth(bfo.outWidth);
                mediaFile.setHeight(bfo.outHeight);
            }

            if (!TextUtils.isEmpty(mimeType)) {
                mediaFile.setMimeType(mimeType);
            }

                //attach object to post
                attachObjectToPost(mimeType, file);

            if(!isVideoThumb){
                //Add thumbnail to slider and refresh
                generateThumbAndAddToSlider(mediaFile);

            }

            WordPress.wpDB.saveMediaFile(mediaFile);
            startMediaUploadService();
        }
    }

    /*
        Generate Thumbnail
     */
    public void generateThumbAndAddToSlider(MediaFile mediaFile){


        String mimeType = mediaFile.getMimeType();

        File file = new File(mediaFile.getFilePath());

        //Generate Thumbnail
        File thumb = null;

        if(mimeType.startsWith("image") || (mimeType.startsWith("video"))) {
            String thumbnailURL = generateThumb(file, mimeType, mediaFile.getDateCreatedGMT());

            thumb = new File(thumbnailURL);
            if(thumb.exists()) {
                mediaFile.setThumbnailURL(thumbnailURL);
            }

        }else{
            //it's audio
            String app_name = getResources().getString(R.string.app_name);
            thumb = new File(Environment.getExternalStorageDirectory(), app_name+"/thumbnails/thumb_audio.png");
            if(!thumb.exists()) {
                try {
                    InputStream inputStream = getResources().openRawResource(R.raw.thumb_audio);
                    OutputStream out = new FileOutputStream(thumb);
                    byte buf[] = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0)
                        out.write(buf, 0, len);
                    out.close();
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }

        if (thumb.exists()) {
            //TODO:
            Random randomGenerator = new Random();
            media_map.put(String.valueOf(randomGenerator.nextInt(10000)), thumb);
            setUpSlider();
            //add thumb to story mediapaths so as to display before upload
            mPost.setMediaPaths(mPost.getMediaPaths() + "-:-" + thumb.getAbsolutePath());
        }
    }
    public String generateThumb(File file, String mimeType, long mediaCreationTime){
        String thumbnailUri = "";
        //create thumbnails folder if not exists
        String app_name = getResources().getString(R.string.app_name);

        File mThumbsDir = new File(Environment.getExternalStorageDirectory(), app_name+"/thumbnails");
        if (!mThumbsDir.exists()) {
            if (!mThumbsDir.mkdirs()) {
                return thumbnailUri;
            }
        }

        //create thumb according to type
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String mt = dateFormat.format(mediaCreationTime);

        thumbnailUri = mThumbsDir.getAbsolutePath() + "/" + mt+".jpg";

        File thisThumb = new File(thumbnailUri);

        //if thumbnail does not exist
        if(!thisThumb.exists()){

            //Create thumbnail
            Bitmap bitThumb = null;
            String filename=null;

            if(mimeType.startsWith("video")){
                bitThumb = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            }else if(mimeType.startsWith("image")){
                bitThumb = BitmapFactory.decodeFile(file.getAbsolutePath());
            }

            try{
                filename = mThumbsDir.getAbsolutePath() + "/" + mt+".jpg";

                FileOutputStream out = new FileOutputStream(filename);
                bitThumb.compress(Bitmap.CompressFormat.JPEG, 30, out);
                out.close();

                //upload video thumbnail
                if(mimeType.startsWith("video")){
                    queueFileForUpload(new File(filename), true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return thumbnailUri;
    }

    /**
     * Starts the upload service to upload selected media.
     */
    private void startMediaUploadService() {
        if (!mMediaUploadServiceStarted) {
            startService(new Intent(this, MediaUploadService.class));
            mMediaUploadServiceStarted = true;
        }
    }
    private boolean addMedia(File file) {
        Uri imageUri = Uri.fromFile(file);

        if (!MediaUtils.isInMediaStore(imageUri) && !imageUri.toString().startsWith("/")) {
            imageUri = MediaUtils.downloadExternalMedia(this, imageUri);
        }

        if (imageUri == null) {
            return false;
        }

        queueFileForUpload(file, false);

        //mEditorFragment.appendMediaFile(mediaFile, mediaFile.getFilePath(), WordPress.imageLoader);
        return true;
    }

    private void showErrorAndFinish(int errorMessageId) {
        Toast.makeText(this, getResources().getText(errorMessageId), Toast.LENGTH_LONG).show();
        finish();
    }

    public void saveDialog(){
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(StoryBoard.this);
        saveDialog.setPositiveButton(getApplicationContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(mPost.isLocalDraft()) {
                saveAndFinish();
            }else{
                finish();
            }
            return true;
        }
        if(item.getItemId()== R.id.save){
            justSave();
        }
        if(item.getItemId()== R.id.publish){
            publishAndFinish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasEmptyContentFields() {
        boolean hasEmpty = false;
        if(TextUtils.isEmpty(mPost.getTitle())){
            thumbSummary.setColorFilter(getResources().getColor(R.color.alert_red), android.graphics.PorterDuff.Mode.MULTIPLY);
            hasEmpty = true;
        }
        if(TextUtils.isEmpty(mPost.getStringLocation())){
            thumbLocation.setColorFilter(getResources().getColor(R.color.alert_red), android.graphics.PorterDuff.Mode.MULTIPLY);
            hasEmpty = true;
        }
        if(TextUtils.isEmpty(mPost.getQwhen()) && (TextUtils.isEmpty(mPost.getQwhen_date()))){
            thumbDate.setColorFilter(getResources().getColor(R.color.alert_red), android.graphics.PorterDuff.Mode.MULTIPLY);
            hasEmpty = true;
        }
        /*
        if(TextUtils.isEmpty(mPost.getKeywords())) {
            hasEmpty = true;
        }

        if(TextUtils.isEmpty(mPost.getQwhy())){
            hasEmpty = true;
        }
        */
        return hasEmpty;
    }
    private boolean allEmptyContentFields() {
        boolean allEmpty;
        if ((TextUtils.isEmpty(mPost.getTitle())) && (TextUtils.isEmpty(mPost.getStringLocation())) && (TextUtils.isEmpty(mPost.getQhow())) && (TextUtils.isEmpty(mPost.getQwhy()) || TextUtils.isEmpty(mPost.getQwhen_date())) && (TextUtils.isEmpty(mPost.getQwhen())) && (TextUtils.isEmpty(mPost.getKeywords()))) {
            allEmpty = true;
        }else{
            allEmpty = false;
        }
        return allEmpty;
    }

    private void saveAndFinish() {
        //savePost(true);
        if (allEmptyContentFields() && !hasMedia) {
            // new and empty post? delete it
            //if (mIsNewPost) {
                WordPress.wpDB.deletePost(mPost);
            //}

        } /*else if (mOriginalPost != null && !mPost.hasChanges(mOriginalPost)) {
            // if no changes have been made to the post, set it back to the original don't save it
            WordPress.wpDB.updatePost(mOriginalPost);
            WordPress.currentPost = mOriginalPost;
        } */else {

            justSave();

        }
        closePost();
    }

    public void closePost(){
        finish();

    }

    public void justSave(){
        //savePost(true);
        if (allEmptyContentFields()) {
            // new and empty post? delete it
            /*if (mIsNewPost) {
                WordPress.wpDB.deletePost(mPost);
            }*/
            ToastUtils.showToast(getApplication(), getResources().getString(R.string.title_required), ToastUtils.Duration.SHORT);

        } /*else if (mOriginalPost != null && !mPost.hasChanges(mOriginalPost)) {
            // if no changes have been made to the post, set it back to the original don't save it
            WordPress.wpDB.updatePost(mOriginalPost);
            WordPress.currentPost = mOriginalPost;
        } */else {
            // changes have been made, save the post and ask for the post list to refresh.
            // We consider this being "manual save", it will replace some Android "spans" by an html
            // or a shortcode replacement (for instance for images and galleries)
            savePost(true);
            WordPress.currentPost = mPost;
            /*Intent i = new Intent();
            i.putExtra(EXTRA_SHOULD_REFRESH, true);
            i.putExtra(EXTRA_SAVED_AS_LOCAL_DRAFT, false);
            i.putExtra(EXTRA_IS_PAGE, mIsPage);
            setResult(RESULT_OK, i);
            */
            ToastUtils.showToast(getApplication(), getResources().getString(R.string.editor_toast_changes_saved), ToastUtils.Duration.SHORT);


        }

    }

    public void publishAndFinish(){

        //ToastUtils.showToast(this, R.string.editor_toast_changes_saved);
        if (hasEmptyContentFields()) {
            ToastUtils.showToast(this, R.string.missing_required_fields, ToastUtils.Duration.SHORT);

        }else if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.showToast(this, R.string.error_publish_no_network, ToastUtils.Duration.SHORT);

            savePost(true);
            WordPress.currentPost = mPost;

        }else{
            savePost(true);
            WordPress.currentPost = mPost;

            PostUploadService.addPostToUpload(mPost);
            startService(new Intent(this, PostUploadService.class));
            Intent i = new Intent();
            i.putExtra(EXTRA_SHOULD_REFRESH, true);
            setResult(RESULT_OK, i);

            finish();
        }

    }
    private void savePost(boolean isAutosave) {
        savePost(isAutosave, true);
    }

    private void savePost(boolean isAutosave, boolean updatePost) {
        if (updatePost) {
            updatePostContent();
            updatePostSettings();
        }
        WordPress.wpDB.updatePost(mPost);
    }

    private void updatePostContent(){
        Post post = mPost;
        post.setTitle(mPost.getTitle());
        if (!post.isLocalDraft()) {
            post.setLocalChange(true);
        }
    }

    private void updatePostSettings(){
        String status = PostStatus.toString(PostStatus.DRAFT);
        mPost.setPostStatus(status);

        if (mPost.supportsLocation()) {
            mPost.setLocation(mPostLocation);
        }

        //mPost.setKeywords(tags);

        mPost.setPostStatus(status);
    }

    public void loadPost(Post p){
        //setTitle(p.getTitle());
        if(p != null){
            if(!p.getTitle().equals(""))
                displaySummary.setText("" + p.getTitle());

            if(!(p.getKeywords() == null) && !p.getKeywords().equals(""))
                displayWho.setText(p.getKeywords());

            if(!(p.getStringLocation() == null) && !p.getStringLocation().equals(""))
                displayLocation.setText(p.getStringLocation());

            if(!(p.getQwhy() == null) && !p.getQwhy().equals(""))
                displayCause.setText(p.getQwhy());

            if(!(p.getQwhen() == null) && !p.getQwhen().equals("")){
                displayDate.setText(p.getQwhen());
            }

            if(!(p.getQwhen_date() == null) && !p.getQwhen_date().equals("")) {
                displayDate_Calendar.setText(p.getQwhen_date());
                displayDate_Calendar.setVisibility(View.VISIBLE);
                if(p.getQwhen() == null || p.getQwhen().equals("")){
                    displayDate.setText("");

                }
            }

        }
    }

    public void setUpQuestionnaire(){

        String[] myResArray = getResources().getStringArray(R.array.fivew_and_h);
        List<String> myResArrayList = Arrays.asList(myResArray);

        String tags = mPost.getKeywords();

        Question questions[] = new Question[]
                {
                        new Question(myResArrayList.get(0), mPost.getTitle()),
                        new Question(myResArrayList.get(1), mPost.getQwhy()),
                        new Question(myResArrayList.get(2), mPost.getKeywords()),
                        new Question(myResArrayList.get(3), mPost.getStringLocation()),
                        new Question(myResArrayList.get(4), mPost.getQwhen()),
                        new Question(myResArrayList.get(5), mPost.getQhow())
                };
        ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.guideListview);

        GuideArrayAdapter arrayAdapter = new GuideArrayAdapter(getApplicationContext(), StoryBoard.this,
                R.layout.view_row, questions, mPost, expandableLayoutListView);

        expandableLayoutListView.setAdapter(arrayAdapter);

    }

    public void togglePanes(boolean showTemplate){
        LinearLayout.LayoutParams activeParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        activeParam.setMargins(3, 3, 3, 3);

        LinearLayout.LayoutParams inActiveParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        inActiveParam.setMargins(5, 5, 5, 5);

        if (showTemplate){
            summaryPane.setVisibility(View.GONE);
            guidePane.setVisibility(View.VISIBLE);

            text_summary.setTextColor(getResources().getColor(R.color.reader_hyperlink));
            text_template.setTextColor(getResources().getColor(R.color.black));

            text_summary.setLayoutParams(inActiveParam);
            text_template.setLayoutParams(activeParam);

            text_summary.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            text_template.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            text_template.setBackgroundColor(getResources().getColor(R.color.grey_lighten_10));
            text_summary.setBackgroundColor(getResources().getColor(R.color.grey_lighten_20));

        }else{
            summaryPane.setVisibility(View.VISIBLE);
            guidePane.setVisibility(View.GONE);

            text_summary.setTextColor(getResources().getColor(R.color.black));
            text_template.setTextColor(getResources().getColor(R.color.reader_hyperlink));

            text_summary.setLayoutParams(activeParam);
            text_template.setLayoutParams(inActiveParam);

            text_template.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            text_summary.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            text_summary.setBackgroundColor(getResources().getColor(R.color.grey_lighten_10));
            text_template.setBackgroundColor(getResources().getColor(R.color.grey_lighten_20));
        }
    }

    public void setUpSlider(){
        mDemoSlider.removeAllSliders();

        if(mPost.isLocalDraft()){
            for(String name : media_map.keySet()){
                TextSliderView textSliderView = new TextSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .description("")
                        .image(media_map.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", name);

                mDemoSlider.addSlider(textSliderView);

            }
        }else{

            for(String name : media_map_remote.keySet()){
                TextSliderView textSliderView = new TextSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .description("")
                        .image(media_map_remote.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra",name);

                mDemoSlider.addSlider(textSliderView);
            }
        }


        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        if(media_map.size()>0 || media_map_remote.size()>0){
            hasMedia = true;
            if(media_map.size()<2 && media_map_remote.size()<2){
                mDemoSlider.setDuration(100000);
            }
        }

        toggleMediaPane(hasMedia);
    }

    public void toggleMediaPane(boolean hasMedia){
        if(hasMedia){
            noMedia.setVisibility(View.GONE);
            yesMedia.setVisibility(View.VISIBLE);
        }else{
            yesMedia.setVisibility(View.GONE);
            noMedia.setVisibility(View.VISIBLE);
        }
    }

    public void showAnswerQuestionDialog(final int question_id, final TextView textView, final ImageView questionThumb){

        questionDialog = new Dialog(StoryBoard.this);
        questionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        questionDialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        questionDialog.setContentView(R.layout.summary_fragment);
        questionDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        submitButton = (FButton)questionDialog.findViewById(R.id.submitButton);
        submitButton.setEnabled(false);

        editTextSummary = (EditText)questionDialog.findViewById(R.id.editTextSummary);

        //show location button
        final LinearLayout locationGroup = (LinearLayout)questionDialog.findViewById(R.id.locationGroup);
        if(question_id == 1){
            locationGroup.setVisibility(View.VISIBLE);

            enableLocation = (FButton)questionDialog.findViewById(R.id.enableLocation);
            enableLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, RequestCodes.START_LOCATION_SERVICE);
                }
            });

            initLocation();
        }
        //show date button
        final DatePicker datePicker = (DatePicker)questionDialog.findViewById(R.id.datePicker);
        final CheckBox useDatePicker = (CheckBox)questionDialog.findViewById(R.id.use_datepicker);
        if(question_id == 3){

            useDatePicker.setVisibility(View.VISIBLE);
            useDatePicker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//                        editTextSummary.setVisibility(View.GONE);
                        datePicker.setVisibility(View.VISIBLE);
                        submitButton.setEnabled(true);
                    } else {
                        datePicker.setVisibility(View.GONE);
                        if (editTextSummary.getText().toString().length() > 0) {
                            submitButton.setEnabled(true);
                        } else {
                            submitButton.setEnabled(false);
                        }
                    }
                }
            });


            //check if has calendar date
                if (displayDate_Calendar.getText().toString().trim().length() > 0) {

                    useDatePicker.setChecked(true);

                    String[] dateParts = displayDate_Calendar.getText().toString().trim().split("/");

                    if (dateParts.length == 3) {

                        int month = Integer.parseInt(dateParts[0]) - 1;
                        int day = Integer.parseInt(dateParts[1]);
                        int year = Integer.parseInt(dateParts[2]);

                        datePicker.updateDate(year, month, day);
                    }

                }

        }

        //find current value of summary
        String current_answer = "" + textView.getText().toString();

        //find the prompt for this question
        final String prompt = getResources().getStringArray(R.array.storyboard_prompts)[question_id];

        //if it's not default & not empty edit editTextSummary
        if(!current_answer.equals(prompt) && (!current_answer.equals(""))){
            editTextSummary.setText(current_answer);
            submitButton.setEnabled(true);
        }

        editTextSummary.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String new_answer = "" + editTextSummary.getText().toString();
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

        questionDialog.findViewById(R.id.closeDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionDialog.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  new_answer = editTextSummary.getText().toString();

                String string_date="";

                if (new_answer.trim().length() > 0) {

                    if (!new_answer.equals(prompt)) {
                        //save answer;

                        switch(question_id){
                            case 0:
                                mPost.setTitle(new_answer);
                                break;
                            case 1:
                                mPost.setStringLocation(new_answer);
                                break;
                            case 2:
                                mPost.setKeywords(new_answer);
                                break;
                            case 3:
                                textView.setVisibility(View.VISIBLE);
                                mPost.setQwhen(new_answer);
                                break;
                            case 4:
                                mPost.setQwhy(new_answer);
                                break;
                        }
                        textView.setText(new_answer);
                        questionThumb.setColorFilter(getResources().getColor(R.color.color_primary), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }else {

                    textView.setText(prompt);

                    questionThumb.setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                }

                if(question_id == 3) {

                    if(useDatePicker.isChecked()) {
                        //if date calendar is set, we can set textview to blank if empty
                        if(new_answer.trim().length() < 1)
                            textView.setVisibility(View.GONE);

                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth() + 1;
                        int year = datePicker.getYear();

                        string_date = String.format("%02d", month) + "/" + String.format("%02d", day) + "/" + year;
                        mPost.setQwhen_date(string_date);

                        displayDate_Calendar.setText(string_date);
                        displayDate_Calendar.setVisibility(View.VISIBLE);
                        questionThumb.setColorFilter(getResources().getColor(R.color.color_primary), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }else{
                        mPost.setQwhen_date("");
                        displayDate_Calendar.setText("");
                        displayDate_Calendar.setVisibility(View.GONE);
                        if(new_answer.trim().length() < 1)
                            questionThumb.setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }

                }

                WordPress.wpDB.savePost(mPost);

                questionDialog.dismiss();
            }
        });

        questionDialog.show();
    }


    public void attachObjectToPost(String mimeType, File file){
        String attachURL = "";

        //get name
        String filename = file.getName();

        Calendar c = Calendar.getInstance();
        String year = c.get(Calendar.YEAR) + "";
        int month_int = c.get(Calendar.MONTH) + 1;
        String month = String.format("%02d", month_int);

        //compose file url
        String file_url = BuildConfig.DEFAULT_URL + "/wp-content/uploads/" + year + "/" + month + "/wpid-" + filename;

        if(mimeType.startsWith("image")){
            attachURL = "<a href=\""+file_url+"\"><img class=\"alignnone size-medium wp-image-400\" src=\""+file_url+"\" alt=\""+filename+"\" width=\"300\" height=\"225\" /></a>";
            //For thumbnail
            mPost.setRemoteMediaPaths(mPost.getRemoteMediaPaths() + "-:-" + file_url);

        } else if(mimeType.startsWith("video")){
            attachURL = "[video width=\"320\" height=\"240\" mp4=\""+file_url+"\"][/video]";
        }else if(mimeType.startsWith("audio")){
            attachURL = "[audio mp3=\""+file_url+"\"][/audio]";
            //use default audio thumb
            mPost.setRemoteMediaPaths(mPost.getRemoteMediaPaths() + "-:-" + BuildConfig.AUDIO_THUMB);
        }


        String old_description = mPost.getDescription() + "";
        mPost.setDescription(old_description + "\n" + attachURL);
    }


    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {}


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

            return GeocoderUtils.getAddressFromCoords(StoryBoard.this, latitude, longitude);
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

            return GeocoderUtils.getAddressFromLocationName(StoryBoard.this, locationName);
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
            if (StoryBoard.this == null)
                return;
            // note that location will be null when requesting location fails
            StoryBoard.this.runOnUiThread(new Runnable() {
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
    private FButton mButtonSearchLocation;
    private PostLocation mPostLocation;
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
                buttonText = getApplicationContext().getResources().getString(R.string.search_location);
            } else {
                buttonText = getApplicationContext().getResources().getString(R.string.search_current_location);
            }
            mButtonSearchLocation.setText(buttonText);
        }
    };


    /*
     * called when activity is created to initialize the location provider, show views related
     * to location if enabled for this blog, and retrieve the current location if necessary
     */
    private void initLocation() {
        // show the location views if a provider was found and this is a post on a blog that has location enabled
        if (hasLocationProvider() && mPost.supportsLocation()) {
            enableLocation.setVisibility(View.GONE);

            View locationRootView = ((ViewStub) questionDialog.findViewById(R.id.stub_post_location_settings)).inflate();

            mLocationText = (TextView) locationRootView.findViewById(R.id.locationText);
            mLocationText.setOnClickListener(this);

            mLocationAddSection = locationRootView.findViewById(R.id.sectionLocationAdd);
            mLocationSearchSection = locationRootView.findViewById(R.id.sectionLocationSearch);
            mLocationViewSection = locationRootView.findViewById(R.id.sectionLocationView);

            FButton addLocation = (FButton) locationRootView.findViewById(R.id.addLocation);
            addLocation.setOnClickListener(this);

            mButtonSearchLocation = (FButton) locationRootView.findViewById(R.id.searchLocation);
            mButtonSearchLocation.setOnClickListener(this);

            mLocationEditText = (EditText) locationRootView.findViewById(R.id.searchLocationText);
            mLocationEditText.setOnEditorActionListener(this);
            mLocationEditText.addTextChangedListener(mLocationEditTextWatcher);

            Button updateLocation = (FButton) locationRootView.findViewById(R.id.updateLocation);
            Button removeLocation = (FButton) locationRootView.findViewById(R.id.removeLocation);
            updateLocation.setOnClickListener(this);
            removeLocation.setOnClickListener(this);

            // if this post has location attached to it, look up the location address
            if (mPost.hasLocation()) {
                showLocationView();

                PostLocation location = mPost.getLocation();
                setLocation(location.getLatitude(), location.getLongitude());

                submitButton.setEnabled(true);
            } else {
                showLocationAdd();
            }
        }else{
            enableLocation.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasLocationProvider() {
        boolean hasLocationProvider = false;
        LocationManager locationManager = (LocationManager) StoryBoard.this.getSystemService(Activity.LOCATION_SERVICE);
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
        boolean canGetLocation = mLocationHelper.getLocation(StoryBoard.this, locationResult);

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
            updateLocationText(getApplicationContext().getString(R.string.location_not_found));
            setLocationStatus(LocationStatus.NOT_FOUND);
        }
    }

    private void setLocation(double latitude, double longitude) {
        mPostLocation = new PostLocation(latitude, longitude);
        new GetAddressTask().execute(mPostLocation.getLatitude(), mPostLocation.getLongitude());
    }

    private void removeLocation() {
        mPostLocation = null;
        mPost.unsetLocation();

        updateLocationText("");
        setLocationStatus(LocationStatus.NONE);
    }

    private void viewLocation() {
        if (mPostLocation != null && mPostLocation.isValid()) {
            String uri = "geo:" + mPostLocation.getLatitude() + "," + mPostLocation.getLongitude();
            StoryBoard.this.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
        } else {
            showLocationNotAvailableError();
            showLocationAdd();
        }
    }

    private void showLocationNotAvailableError() {
        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getText(R.string.location_not_found), Toast.LENGTH_SHORT).show();
    }

    private void updateLocationText(String locationName) {
        mLocationText.setText(locationName);
        mLocationEditText.setText(locationName);

        if(!locationName.equals(getApplicationContext().getResources().getText(R.string.location_not_found)) && !(locationName.equals(getApplicationContext().getResources().getText(R.string.loading)))) {
            mPost.setStringLocation(locationName);
            editTextSummary.setText(locationName);
        }
    }

    /*
     * changes the left drawable on the location text to match the passed status
     */

    private void setLocationStatus(LocationStatus status) {


        // animate location text when searching
        if (status == LocationStatus.SEARCHING) {
            updateLocationText(getApplicationContext().getString(R.string.loading));

            Animation aniBlink = AnimationUtils.loadAnimation(StoryBoard.this, R.anim.blink);
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
