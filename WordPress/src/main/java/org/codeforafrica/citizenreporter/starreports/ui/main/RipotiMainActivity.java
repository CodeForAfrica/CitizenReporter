package org.codeforafrica.citizenreporter.starreports.ui.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.simperium.client.Bucket;
import com.simperium.client.BucketObjectMissingException;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.List;
import org.codeforafrica.citizenreporter.starreports.BuildConfig;
import org.codeforafrica.citizenreporter.starreports.GCMConfigORG;
import org.codeforafrica.citizenreporter.starreports.R;
import org.codeforafrica.citizenreporter.starreports.RuntimePermissionsActivity;
import org.codeforafrica.citizenreporter.starreports.WordPress;
import org.codeforafrica.citizenreporter.starreports.main.UserMenuActivity;
import org.codeforafrica.citizenreporter.starreports.models.AccountHelper;
import org.codeforafrica.citizenreporter.starreports.models.CommentStatus;
import org.codeforafrica.citizenreporter.starreports.models.Note;
import org.codeforafrica.citizenreporter.starreports.models.Post;
import org.codeforafrica.citizenreporter.starreports.networking.SelfSignedSSLCertsManager;
import org.codeforafrica.citizenreporter.starreports.ui.ActivityLauncher;
import org.codeforafrica.citizenreporter.starreports.ui.RequestCodes;
import org.codeforafrica.citizenreporter.starreports.ui.media.MediaAddFragment;
import org.codeforafrica.citizenreporter.starreports.ui.notifications.NotificationEvents;
import org.codeforafrica.citizenreporter.starreports.ui.notifications.NotificationsListFragment;
import org.codeforafrica.citizenreporter.starreports.ui.notifications.utils.NotificationsUtils;
import org.codeforafrica.citizenreporter.starreports.ui.notifications.utils.SimperiumUtils;
import org.codeforafrica.citizenreporter.starreports.ui.posts.EditPostActivity;
import org.codeforafrica.citizenreporter.starreports.ui.posts.ViewAssignmentFragment;
import org.codeforafrica.citizenreporter.starreports.ui.posts.ViewPostFragmentRipoti;
import org.codeforafrica.citizenreporter.starreports.ui.posts.ViewPostFragmentRipoti.OnDetailPostActionListener;
import org.codeforafrica.citizenreporter.starreports.ui.prefs.AppPrefs;
import org.codeforafrica.citizenreporter.starreports.ui.prefs.BlogPreferencesActivity;
import org.codeforafrica.citizenreporter.starreports.ui.reader.ReaderEvents;
import org.codeforafrica.citizenreporter.starreports.ui.reader.ReaderPostListFragment;
import org.codeforafrica.citizenreporter.starreports.widgets.SlidingTabLayout;
import org.codeforafrica.citizenreporter.starreports.widgets.WPAlertDialogFragment;
import org.codeforafrica.citizenreporter.starreports.widgets.WPMainViewPager;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.AppLog.T;
import org.wordpress.android.util.AuthenticationDialogUtils;
import org.wordpress.android.util.CoreEvents;
import org.wordpress.android.util.CoreEvents.MainViewPagerScrolled;
import org.wordpress.android.util.CoreEvents.UserSignedOutCompletely;
import org.wordpress.android.util.CoreEvents.UserSignedOutWordPressCom;
import org.wordpress.android.util.DisplayUtils;
import org.wordpress.android.util.StringUtils;
import org.wordpress.android.util.ToastUtils;

/**
 * Main activity which hosts sites, reader, me and notifications tabs
 */

public class RipotiMainActivity extends RuntimePermissionsActivity
    implements ViewPager.OnPageChangeListener, SlidingTabLayout.SingleTabClickListener,
    MediaAddFragment.MediaAddFragmentCallback, Bucket.Listener<Note>,
    RipotiPostsListFragment.OnPostSelectedListener,
    RipotiPostsListFragment.OnSinglePostLoadedListener,
    RipotiPostsListFragment.OnPostActionListener,
    AssignmentsListFragment.OnAssignmentSelectedListener,
    AssignmentsListFragment.OnSinglePostLoadedListener,
    AssignmentsListFragment.OnAssignmentActionListener, OnDetailPostActionListener,
    ViewAssignmentFragment.OnDetailAssignmentActionListener,
    WPAlertDialogFragment.OnDialogConfirmListener {
  private WPMainViewPager mViewPager;
  private SlidingTabLayout mTabs;
  private RipotiMainTabAdapter mTabAdapter;

  public static final String ARG_OPENED_FROM_PUSH = "opened_from_push";

    /*
    Assignments list variables
     */

  public static final String EXTRA_VIEW_PAGES = "viewPages";
  public static final String EXTRA_ERROR_MSG = "errorMessage";
  public static final String EXTRA_ERROR_INFO_TITLE = "errorInfoTitle";
  public static final String EXTRA_ERROR_INFO_LINK = "errorInfoLink";

  public static final int POST_DELETE = 0;
  public static final int POST_SHARE = 1;
  public static final int POST_EDIT = 2;
  private static final int POST_CLEAR = 3;
  public static final int POST_VIEW = 5;
  private static final int ID_DIALOG_DELETING = 1, ID_DIALOG_SHARE = 2;
  private ProgressDialog mLoadingDialog;
  private boolean mIsPage = false;
  private String mErrorMsg = "";

  public RipotiPostsListFragment mPostList;

  public AssignmentsListFragment mAssignmentsList;
  private ViewAssignmentFragment viewAssignmentFragment;
  private Toolbar toolbar;
  private ViewPostFragmentRipoti viewPostFragment;

  private LinearLayout button_camera;
  private LinearLayout button_video;
  private LinearLayout button_mic;

  @Override public void onDetailAssignmentAction(int action, Post post) {
    onAssignmentAction(action, post);
  }

  @Override public void onDetailPostAction(int action, Post post) {
    onPostAction(action, post);
  }

  @Override public void onPostAction(int action, final Post post) {

  }

  @Override public void onDialogConfirm() {

  }

  @Override public void onAssignmentAction(int action, final Post post) {

  }

  public void closePost() {
    if (viewAssignmentFragment != null) {
      getFragmentManager().beginTransaction().remove(viewAssignmentFragment).commit();
    }

    toggleToolbar(false);
  }

  public void toggleToolbar(boolean isShowing) {
    if (isShowing) {
      mTabs.setVisibility(View.GONE);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    } else {
      mTabs.setVisibility(View.VISIBLE);
      getSupportActionBar().setDisplayHomeAsUpEnabled(false);
      toolbar.setNavigationIcon(R.drawable.app_icon);
    }
  }

  @Override public void onAssignmentSelected(Post post) {
    if (isFinishing()) {
      return;
    }
    FragmentManager fm = getFragmentManager();
    viewAssignmentFragment = (ViewAssignmentFragment) fm.findFragmentById(R.id.assignmentDetail);

    if (post != null) {
            /*
            TODO: Editable assignments by admin?
            if (post.isUploading()){
                ToastUtils.showToast(this, R.string.toast_err_post_uploading, ToastUtils.Duration.SHORT);
                return;
            }*/
      WordPress.currentPost = post;

      if (viewAssignmentFragment == null || !viewAssignmentFragment.isInLayout()) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(mPostList);
        viewAssignmentFragment = new ViewAssignmentFragment();
        ft.add(R.id.postDetailFragmentContainer, viewAssignmentFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
      } else {
        viewAssignmentFragment.loadPost(post);
      }

      toggleToolbar(true);
    }
  }

  @Override public void onPostSelected(Post post) {

    if (isFinishing()) {
      return;
    }
    FragmentManager fm = getFragmentManager();
    viewPostFragment = (ViewPostFragmentRipoti) fm.findFragmentById(R.id.postDetail);

    if (post != null) {
      if (post.isUploading()) {
        ToastUtils.showToast(this, R.string.toast_err_post_uploading, ToastUtils.Duration.SHORT);
        return;
      }
      WordPress.currentPost = post;
      viewPostFragment = new ViewPostFragmentRipoti();

      if (viewPostFragment == null || !viewPostFragment.isInLayout()) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(mPostList);
        viewPostFragment = new ViewPostFragmentRipoti();
        ft.add(R.id.postDetailFragmentContainer, viewPostFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
      } else {
        viewPostFragment.loadPost(post);
      }

      toggleToolbar(true);
    }
  }

  public void checkIfRegistered() {
    SharedPreferences settings =
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    String rD = settings.getString("rD", "0");

    if (rD.equals("0")) {
      RipotiMainActivity.super.requestAppPermissions(
          new String[] { "com.google.android.c2dm.permission.RECEIVE" },
          R.string.runtime_permissions_txt, RequestCodes.C2D_RECEIVE_PERMISSIONS);

      registerDevice();
    }
  }

  @Override public void onSinglePostLoaded() {

  }

  private void popPostDetail() {

  }

  private void popAssignmentDetail() {
    if (isFinishing()) {
      return;
    }

    FragmentManager fm = getFragmentManager();
    ViewAssignmentFragment f = (ViewAssignmentFragment) fm.findFragmentById(R.id.assignmentDetail);
    if (f == null) {
      try {
        fm.popBackStack();
      } catch (RuntimeException e) {
        AppLog.e(T.POSTS, e);
      }
    }
  }

  public boolean isDualPane() {
    return false;
  }

  public void newPost(int assignment_id) {
    if (WordPress.getCurrentBlog() == null) {
      if (!isFinishing()) Toast.makeText(this, R.string.blog_not_found, Toast.LENGTH_SHORT).show();
      return;
    }
    // Create a new post object
    Post newPost = new Post(WordPress.getCurrentBlog().getLocalTableBlogId(), mIsPage);
    newPost.setAssignment_id(assignment_id);

    WordPress.wpDB.savePost(newPost);
    Intent i = new Intent(this, EditPostActivity.class);
    i.putExtra(EditPostActivity.EXTRA_POSTID, newPost.getLocalTablePostId());
    i.putExtra(EditPostActivity.EXTRA_IS_PAGE, mIsPage);
    i.putExtra(EditPostActivity.EXTRA_IS_NEW_POST, true);
    startActivityForResult(i, RequestCodes.EDIT_POST);
  }

  public void requestPosts() {
    if (WordPress.getCurrentBlog() == null) {
      return;
    }
    // If user has local changes, don't refresh
    if (!WordPress.wpDB.findLocalChanges(WordPress.getCurrentBlog().getLocalTableBlogId(),
        mIsPage)) {
      popPostDetail();
      mPostList.requestPosts(false);
      mPostList.setRefreshing(true);
    }
  }

  public void requestAssignments() {
    if (WordPress.getCurrentBlog() == null) {
      return;
    }
    // If user has local changes, don't refresh
    //if (!WordPress.wpDB.findLocalChanges(WordPress.getCurrentBlog().getLocalTableBlogId(), mIsPage)) {
    // popAssignmentDetail();
    // mAssignmentsList.requestPosts(false);
    // mAssignmentsList.setRefreshing(true);
    //}
  }

  /*
   * tab fragments implement this if their contents can be scrolled, called when user
   * requests to scroll to the top
   */
  public interface OnScrollToTopListener {
    void onScrollToTop();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    setStatusBarColor();

    FirebaseApp.initializeApp(this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.ripoti_main_screen);

    // TODO: Move this to where you establish a user session

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    //toolbar.inflateMenu(R.menu.home_menu);
    toolbar.setNavigationIcon(R.drawable.app_icon);

    getSupportActionBar().setDisplayShowTitleEnabled(true);
    Display display = getWindowManager().getDefaultDisplay();
    if (display.getRotation() == Surface.ROTATION_90) {
      getSupportActionBar().hide();
    } else if (display.getRotation() == Surface.ROTATION_270) {
      getSupportActionBar().hide();
    }
    String token = FirebaseInstanceId.getInstance().getToken();
    Log.d("FIREBASE TOKEN: ", " " + token);

    // testing to check if it can even get permissions

    //        int camera_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    //        Toast.makeText(this, " " + camera_permission, Toast.LENGTH_SHORT).show();
    //        Log.i("Permissions", "camera permission " + camera_permission);

    mViewPager = (WPMainViewPager) findViewById(R.id.viewpager_main);
    mTabAdapter = new RipotiMainTabAdapter(getFragmentManager(), RipotiMainActivity.this);
    mViewPager.setAdapter(mTabAdapter);

    mTabs = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.tab_indicator));

    // tabs are left-aligned rather than evenly distributed in landscape
    mTabs.setDistributeEvenly(!DisplayUtils.isLandscape(this));

    Integer icons[] = { R.drawable.main_tab_notifications, R.drawable.main_tab_reader };
    Integer titles[] = { R.string.assignments, R.string.my_posts };

    // content descriptions
    mTabs.setContentDescription(RipotiMainTabAdapter.TAB_ASSIGNMENTS,
        getString(R.string.assignments));
    mTabs.setContentDescription(RipotiMainTabAdapter.TAB_MYPOSTS, getString(R.string.my_posts));

    mTabs.setCustomTabView(R.layout.tab_text, R.id.text_tab, R.id.icon_tab, titles, icons);

    mTabs.setViewPager(mViewPager);
    mTabs.setOnSingleTabClickListener(this);

    // page change listener must be set on the tab layout rather than the ViewPager
    mTabs.setOnPageChangeListener(this);

    if (savedInstanceState == null) {
      if (AccountHelper.isSignedIn()) {
        // open note detail if activity called from a push, otherwise return to the tab
        // that was showing last time
        boolean openedFromPush =
            (getIntent() != null && getIntent().getBooleanExtra(ARG_OPENED_FROM_PUSH, false));
        if (openedFromPush) {
          getIntent().putExtra(ARG_OPENED_FROM_PUSH, false);
          launchWithNoteId();
        } else {
          int position = AppPrefs.getMainTabIndex();
          if (mTabAdapter.isValidPosition(position) && position != mViewPager.getCurrentItem()) {
            mViewPager.setCurrentItem(position);
          }
        }
      } else {
        ActivityLauncher.showSignInForResult(this);
      }
    }

    //        checkAndRequestAllPermissions();

    //quick capture icons
    button_camera = (LinearLayout) findViewById(R.id.button_camera);
    button_video = (LinearLayout) findViewById(R.id.button_video);
    button_mic = (LinearLayout) findViewById(R.id.button_mic);
    //
    button_camera.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        RipotiMainActivity.super.requestAppPermissions(new String[] {
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, R.string.runtime_permissions_txt, RequestCodes.ALL_PERMISSIONS);
        (new WordPress()).capturePic(RipotiMainActivity.this, getApplicationContext());
      }
    });

    button_video.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        RipotiMainActivity.super.requestAppPermissions(new String[] {
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, R.string.runtime_permissions_txt, RequestCodes.ALL_PERMISSIONS);
        (new WordPress()).captureVid(RipotiMainActivity.this, getApplicationContext());
      }
    });

    button_mic.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        RipotiMainActivity.super.requestAppPermissions(new String[] {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, R.string.runtime_permissions_txt, RequestCodes.ALL_PERMISSIONS);
        (new WordPress()).captureAudio(RipotiMainActivity.this, getApplicationContext());
      }
    });

    FragmentManager fm = getFragmentManager();

    mAssignmentsList = (AssignmentsListFragment) fm.findFragmentById(R.id.assignmentList);

    mPostList = (RipotiPostsListFragment) fm.findFragmentById(R.id.postList);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      mIsPage = extras.getBoolean(EXTRA_VIEW_PAGES);
      //showErrorDialogIfNeeded(extras);
    }

    WordPress.currentPost = null;
    RipotiMainActivity.super.requestAppPermissions(new String[] {
        "com.google.android.c2dm.permission.RECEIVE", Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION
    }, R.string.runtime_permissions_txt, RequestCodes.C2D_RECEIVE_PERMISSIONS);

    checkIfRegistered();
  }

  @Override public void onPermissionsGranted(int requestCode) {

  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.home_menu, menu);
    return true;
  }

  public boolean isRefreshingAssignments() {
    return mAssignmentsList.isRefreshing();
  }

  public boolean isRefreshing() {
    return mPostList.isRefreshing();
  }

  public void attemptToSelectAssignment() {
    FragmentManager fm = getFragmentManager();
    ViewAssignmentFragment f = (ViewAssignmentFragment) fm.findFragmentById(R.id.assignmentDetail);
    if (f != null && f.isInLayout()) {
      mAssignmentsList.setShouldSelectFirstPost(true);
    }
  }

  public void attemptToSelectPost() {
    FragmentManager fm = getFragmentManager();
    ViewPostFragmentRipoti f = (ViewPostFragmentRipoti) fm.findFragmentById(R.id.postDetail);
    if (f != null && f.isInLayout()) {
      mPostList.setShouldSelectFirstPost(true);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {

      case android.R.id.home:
        closePost();
        return true;
      case R.id.user_menu:
        //show user menu
        Intent userMenuIntent = new Intent(RipotiMainActivity.this, UserMenuActivity.class);
        ActivityLauncher.slideInFromRight(RipotiMainActivity.this, userMenuIntent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) private void setStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_tint));
    }
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    //AppLog.i(T.MAIN, "main activity > new intent");
    if (intent.hasExtra(NotificationsListFragment.NOTE_ID_EXTRA)) {
      launchWithNoteId();
    }
  }

  public void refreshAssignmentResponses() {

  }

  public void refreshComments() {

  }

  /*
   * Called when app is launched from a push notification, switches to the notification tab
   * and opens the desired note detail
   */
  private void launchWithNoteId() {
    if (isFinishing() || getIntent() == null) return;

    // Check for push authorization request
    if (getIntent().hasExtra(NotificationsUtils.ARG_PUSH_AUTH_TOKEN)) {
      Bundle extras = getIntent().getExtras();
      String token = extras.getString(NotificationsUtils.ARG_PUSH_AUTH_TOKEN, "");
      String title = extras.getString(NotificationsUtils.ARG_PUSH_AUTH_TITLE, "");
      String message = extras.getString(NotificationsUtils.ARG_PUSH_AUTH_MESSAGE, "");
      long expires = extras.getLong(NotificationsUtils.ARG_PUSH_AUTH_EXPIRES, 0);

      long now = System.currentTimeMillis() / 1000;
      if (expires > 0 && now > expires) {
        // Show a toast if the user took too long to open the notification
        ToastUtils.showToast(this, R.string.push_auth_expired, ToastUtils.Duration.LONG);
        //AnalyticsTracker.track(AnalyticsTracker.Stat.PUSH_AUTHENTICATION_EXPIRED);
      } else {
        NotificationsUtils.showPushAuthAlert(this, token, title, message);
      }
    }

    mViewPager.setCurrentItem(RipotiMainTabAdapter.TAB_ASSIGNMENTS);

    String noteId = getIntent().getStringExtra(NotificationsListFragment.NOTE_ID_EXTRA);
    boolean shouldShowKeyboard =
        getIntent().getBooleanExtra(NotificationsListFragment.NOTE_INSTANT_REPLY_EXTRA, false);

    if (!TextUtils.isEmpty(noteId)) {
      NotificationsListFragment.openNote(this, noteId, shouldShowKeyboard, false);
      //GCMIntentService.clearNotificationsMap();
    }
  }

  @Override public void onPageSelected(int position) {
    // remember the index of this page
    AppPrefs.setMainTabIndex(position);

    switch (position) {/*
            case RipotiMainTabAdapter.TAB_NOTIFS:
                if (getNotificationListFragment() != null) {
                    getNotificationListFragment().updateLastSeenTime();
                    mTabs.setBadge(RipotiMainTabAdapter.TAB_NOTIFS, false);
                }
                break;*/
    }
  }

  @Override public void onPageScrollStateChanged(int state) {
    // noop
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    // fire event if the "My Site" page is being scrolled so the fragment can
    // animate its fab to match
    if (position == RipotiMainTabAdapter.TAB_MYPOSTS) {
      EventBus.getDefault().post(new MainViewPagerScrolled(positionOffset));
    }
  }

  /*
   * user tapped a tab above the viewPager - detect when the active tab is clicked and scroll
   * the fragment to the top if available
   */
  @Override public void onTabClick(View view, int position) {
    if (position == mViewPager.getCurrentItem()) {
      Fragment fragment = mTabAdapter.getFragment(position);
      if (fragment instanceof OnScrollToTopListener) {
        ((OnScrollToTopListener) fragment).onScrollToTop();
      }
    }
  }

  @Override protected void onPause() {
    if (SimperiumUtils.getNotesBucket() != null) {
      SimperiumUtils.getNotesBucket().removeListener(this);
    }

    super.onPause();
  }

  @Override protected void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Override protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override protected void onResume() {
    super.onResume();

    // Start listening to Simperium Note bucket
    if (SimperiumUtils.getNotesBucket() != null) {
      SimperiumUtils.getNotesBucket().addListener(this);
    }
    checkNoteBadge();

    //check if taking picture
    (new WordPress()).checkQuickCapture(this, getApplicationContext());
    checkIfRegistered();
  }

  /*
   * re-create the fragment adapter so all its fragments are also re-created - used when
   * user signs in/out so the fragments reflect the active account
   */
  private void resetFragments() {
    //AppLog.i(T.MAIN, "main activity > reset fragments");

    // remove the event that determines when followed tags/blogs are updated so they're
    // updated when the fragment is recreated (necessary after signin/disconnect)
    EventBus.getDefault().removeStickyEvent(ReaderEvents.UpdatedFollowedTagsAndBlogs.class);

    // remember the current tab position, then recreate the adapter so new fragments are created
    int position = mViewPager.getCurrentItem();
    mTabAdapter = new RipotiMainTabAdapter(getFragmentManager(), RipotiMainActivity.this);
    mViewPager.setAdapter(mTabAdapter);

    // restore previous position
    if (mTabAdapter.isValidPosition(position)) {
      mViewPager.setCurrentItem(position);
    }
  }

  private void moderateCommentOnActivityResult(Intent data) {
    try {
      if (SimperiumUtils.getNotesBucket() != null) {
        Note note = SimperiumUtils.getNotesBucket()
            .get(StringUtils.notNullStr(
                data.getStringExtra(NotificationsListFragment.NOTE_MODERATE_ID_EXTRA)));
        CommentStatus status = CommentStatus.fromString(
            data.getStringExtra(NotificationsListFragment.NOTE_MODERATE_STATUS_EXTRA));
        NotificationsUtils.moderateCommentForNote(note, status, this);
      }
    } catch (BucketObjectMissingException e) {
      AppLog.e(T.NOTIFS, e);
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case RequestCodes.EDIT_POST:
        if (resultCode == RESULT_OK) {
          MySiteFragment mySiteFragment = getMySiteFragment();
          if (mySiteFragment != null) {
            mySiteFragment.onActivityResult(requestCode, resultCode, data);
          }
        }
        break;
      case RequestCodes.READER_SUBS:
      case RequestCodes.READER_REBLOG:
        ReaderPostListFragment readerFragment = getReaderListFragment();
        if (readerFragment != null) {
          readerFragment.onActivityResult(requestCode, resultCode, data);
        }
        break;
      case RequestCodes.ADD_ACCOUNT:
        if (resultCode == RESULT_OK) {
          WordPress.registerForCloudMessaging(this);
          resetFragments();
        } else if (!AccountHelper.isSignedIn()) {
          // can't do anything if user isn't signed in (either to wp.com or self-hosted)
          finish();
        }
        break;
      case RequestCodes.REAUTHENTICATE:
        if (resultCode == RESULT_CANCELED) {
          ActivityLauncher.showSignInForResult(this);
        } else {
          WordPress.registerForCloudMessaging(this);
        }
        break;
      case RequestCodes.NOTE_DETAIL:
        if (resultCode == RESULT_OK && data != null) {
          moderateCommentOnActivityResult(data);
        }
        break;
      case RequestCodes.PICTURE_LIBRARY:
        FragmentManager fm = getFragmentManager();
        Fragment addFragment = fm.findFragmentByTag(MySiteFragment.ADD_MEDIA_FRAGMENT_TAG);
        if (addFragment != null && data != null) {
          ToastUtils.showToast(this, R.string.image_added);
          addFragment.onActivityResult(requestCode, resultCode, data);
        }
        break;
      case RequestCodes.SITE_PICKER:
        if (getMySiteFragment() != null) {
          getMySiteFragment().onActivityResult(requestCode, resultCode, data);
        }
        break;
      case RequestCodes.BLOG_SETTINGS:
        if (resultCode == BlogPreferencesActivity.RESULT_BLOG_REMOVED) {
          // user removed the current (self-hosted) blog from blog settings
          if (!AccountHelper.isSignedIn()) {
            ActivityLauncher.showSignInForResult(this);
          } else {
            MySiteFragment mySiteFragment = getMySiteFragment();
            if (mySiteFragment != null) {
              mySiteFragment.setBlog(WordPress.getCurrentBlog());
            }
          }
        }
        break;
    }
  }

  private void checkAndRequestSpecificPermissions(String[] permissions) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      List<String> listPermissionsNeeded = new ArrayList<>();
      for (String permission : permissions) {
        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED) {
          listPermissionsNeeded.add(permission);
        }
      }
      if (!listPermissionsNeeded.isEmpty()) {
        ActivityCompat.requestPermissions(this,
            listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
            RequestCodes.WRITE_EXTERNAL_STORAGE_PERMISSIONS);
      }
    }
  }

  //    private  void checkAndRequestAllPermissions() {
  //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
  //            String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
  //                    Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.GET_ACCOUNTS,
  //            Manifest.permission.READ_PHONE_STATE};
  //            List<String> listPermissionsNeeded = new ArrayList<>();
  //            for (String permission : permissions) {
  //                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
  //                    listPermissionsNeeded.add(permission);
  //                }
  //            }
  //            if (!listPermissionsNeeded.isEmpty()) {
  //                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
  //                        RequestCodes.CAMERA_PERMISSIONS);
  //            }
  //        }
  //    }

  /*
   * returns the reader list fragment from the reader tab
   */
  private ReaderPostListFragment getReaderListFragment() {
    return getFragmentByPosition(WPMainTabAdapter.TAB_READER, ReaderPostListFragment.class);
  }

  /*
   * returns the notification list fragment from the notification tab
   */
  private NotificationsListFragment getNotificationListFragment() {
    return getFragmentByPosition(WPMainTabAdapter.TAB_NOTIFS, NotificationsListFragment.class);
  }

  /*
   * returns the my site fragment from the sites tab
   */
  public MySiteFragment getMySiteFragment() {
    return getFragmentByPosition(WPMainTabAdapter.TAB_MY_SITE, MySiteFragment.class);
  }

  private <T> T getFragmentByPosition(int position, Class<T> type) {
    Fragment fragment = mTabAdapter != null ? mTabAdapter.getFragment(position) : null;

    if (fragment != null && type.isInstance(fragment)) {
      return type.cast(fragment);
    }

    return null;
  }

  /*
   * badges the notifications tab depending on whether there are unread notes
   */
  private boolean mIsCheckingNoteBadge;

  private void checkNoteBadge() {
    if (mIsCheckingNoteBadge) {
      //AppLog.v(T.MAIN, "main activity > already checking note badge");
      return;
    } /*else if (isViewingNotificationsTab()) {
            // Don't show the badge if the notifications tab is active
            //if (mTabs.isBadged(RipotiMainTabAdapter.TAB_NOTIFS)) {
            //    mTabs.setBadge(RipotiMainTabAdapter.TAB_NOTIFS, false);
            //}

            return;
        }*/

    mIsCheckingNoteBadge = true;
    new Thread() {
      @Override public void run() {/*
                final boolean hasUnreadNotes = SimperiumUtils.hasUnreadNotes();
                boolean isBadged = mTabs.isBadged(RipotiMainTabAdapter.TAB_NOTIFS);
                if (hasUnreadNotes != isBadged) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTabs.setBadge(RipotiMainTabAdapter.TAB_NOTIFS, hasUnreadNotes);
                            mIsCheckingNoteBadge = false;
                        }
                    });
                } else {
                    mIsCheckingNoteBadge = false;
                }*/
      }
    }.start();
  }

    /*
    private boolean isViewingNotificationsTab() {
        return mViewPager.getCurrentItem() == RipotiMainTabAdapter.TAB_NOTIFS;
    }*/

  // Events

  @SuppressWarnings("unused") public void onEventMainThread(UserSignedOutWordPressCom event) {
    resetFragments();
  }

  @SuppressWarnings("unused") public void onEventMainThread(UserSignedOutCompletely event) {
    ActivityLauncher.showSignInForResult(this);
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(CoreEvents.InvalidCredentialsDetected event) {
    AuthenticationDialogUtils.showAuthErrorView(this);
  }

  @SuppressWarnings("unused") public void onEventMainThread(CoreEvents.RestApiUnauthorized event) {
    AuthenticationDialogUtils.showAuthErrorView(this);
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(CoreEvents.TwoFactorAuthenticationDetected event) {
    AuthenticationDialogUtils.showAuthErrorView(this);
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(CoreEvents.InvalidSslCertificateDetected event) {
    SelfSignedSSLCertsManager.askForSslTrust(this, null);
  }

  @SuppressWarnings("unused") public void onEventMainThread(CoreEvents.LoginLimitDetected event) {
    ToastUtils.showToast(this, R.string.limit_reached, ToastUtils.Duration.LONG);
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(NotificationEvents.NotificationsChanged event) {
    checkNoteBadge();
  }

  /*
   * Simperium Note bucket listeners
   */
  @Override public void onNetworkChange(Bucket<Note> noteBucket, Bucket.ChangeType changeType,
      String s) {
    if (changeType == Bucket.ChangeType.INSERT || changeType == Bucket.ChangeType.MODIFY) {
      checkNoteBadge();
    }
  }

  @Override public void onBeforeUpdateObject(Bucket<Note> noteBucket, Note note) {
    // noop
  }

  @Override public void onDeleteObject(Bucket<Note> noteBucket, Note note) {
    // noop
  }

  @Override public void onSaveObject(Bucket<Note> noteBucket, Note note) {
    // noop
  }

  @Override public void onMediaAdded(String mediaId) {
  }

  @Override public void onBackPressed() {

    if (viewAssignmentFragment != null) {
      if (viewAssignmentFragment.isVisible()) {
        closePost();
      } else {
        finish();
      }
    } else {
      finish();
    }
  }

  WordPress aController;
  AsyncTask<Void, Void, Void> mRegisterTask;

  public void registerDevice() {
    //Get Global Controller Class object (see application tag in AndroidManifest.xml)
    aController = (WordPress) getApplicationContext();

    // Check if Internet present
    if (!aController.isConnectingToInternet()) {

      // Internet Connection is not present
      aController.showAlertDialog(this, "Internet Connection Error",
          "Please connect to Internet connection", false);
      // stop executing code by return
      return;
    }

    // Getting name, email from intent
    Intent i = getIntent();
    String name, email;
    name = i.getStringExtra("name");
    email = i.getStringExtra("email");

    // Make sure the device has the proper dependencies.
    GCMRegistrar.checkDevice(getApplicationContext());

    // Make sure the manifest permissions was properly set
    GCMRegistrar.checkManifest(getApplicationContext());

    // Register custom Broadcast receiver to show messages on activity
    registerReceiver(mHandleMessageReceiver, new IntentFilter(GCMConfigORG.DISPLAY_MESSAGE_ACTION));

    // Get GCM registration id
    final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());

    Log.d("GCM", "Registration ID " + regId);

    // Check if regid already presents
    if (regId.equals("")) {
      Log.d("GCM", "registration ID is empty");
      // Register with GCM
      GCMRegistrar.register(getApplicationContext(), BuildConfig.GCM_ID);
    } else {
      Log.d("simple1", "regID is not empty");
      // Device is already registered on GCM Server
      if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {
        Log.d("GCM", "already registered in server");
        // Skips registration.
        //Toast.makeText(getApplicationContext(), "Already registered with GCM Server", Toast.LENGTH_LONG).show();
        Log.d("GCM", "already registered");
      } else {
        Log.d("GCM", "register on the server");
        // Try to register again, but not in the UI thread.
        // It's also necessary to cancel the thread onDestroy(),
        // hence the use of AsyncTask instead of a raw thread.

        final Context context = this;
        mRegisterTask = new AsyncTask<Void, Void, Void>() {

          @Override protected Void doInBackground(Void... params) {

            // Register on our server
            // On server creates a new user
            aController.register(context, regId);
            Log.d("GCM", "registering on server " + regId);

            return null;
          }

          @Override protected void onPostExecute(Void result) {
            mRegisterTask = null;
          }
        };

        // execute AsyncTask
        mRegisterTask.execute(null, null, null);
      }
    }
  }

  //Create a broadcast receiver to get message and show on screen
  private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {

      String newMessage = intent.getExtras().getString(GCMConfigORG.EXTRA_MESSAGE);

      // Waking up mobile if it is sleeping
      aController.acquireWakeLock(getApplicationContext());

      // Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_LONG).show();

      // Releasing wake lock
      aController.releaseWakeLock();
    }
  };

  @Override protected void onDestroy() {
    // Cancel AsyncTask
    if (mRegisterTask != null) {
      mRegisterTask.cancel(true);
    }
    try {
      // Unregister Broadcast Receiver
      unregisterReceiver(mHandleMessageReceiver);

      //Clear internal resources.
      GCMRegistrar.onDestroy(getApplicationContext());
    } catch (Exception e) {
      Log.e("UnRegisterReceiverError", ">" + e.getMessage());
    }
    super.onDestroy();
  }
}
