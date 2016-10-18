package org.codeforafrica.citizenreporter.eNCA.ui.posts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.codeforafrica.citizenreporter.eNCA.Constants;
import org.codeforafrica.citizenreporter.eNCA.R;
import org.codeforafrica.citizenreporter.eNCA.WordPress;
import org.codeforafrica.citizenreporter.eNCA.datasets.SuggestionTable;
import org.codeforafrica.citizenreporter.eNCA.models.Post;
import org.codeforafrica.citizenreporter.eNCA.models.Suggestion;
import org.codeforafrica.citizenreporter.eNCA.ui.WPWebViewActivity;
import org.codeforafrica.citizenreporter.eNCA.ui.main.RipotiMainActivity;
import org.codeforafrica.citizenreporter.eNCA.ui.suggestion.adapters.SuggestionAdapter;
import org.codeforafrica.citizenreporter.eNCA.ui.suggestion.service.SuggestionEvents;
import org.codeforafrica.citizenreporter.eNCA.ui.suggestion.util.SuggestionServiceConnectionManager;
import org.wordpress.android.util.DeviceUtils;
import org.wordpress.android.util.StringUtils;
import org.wordpress.android.util.WPHtml;
import org.wordpress.android.util.WPWebViewClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ViewAssignmentFragment extends Fragment {
    /** Called when the activity is first created. */

    private OnDetailAssignmentActionListener mOnDetailPostActionListener;
    RipotiMainActivity mParentActivity;

    private TextView mTitleTextView, mContentTextView;

    private SuggestionAdapter mSuggestionAdapter;
    private SuggestionServiceConnectionManager mSuggestionServiceConnectionManager;

    private TextView mLocation;
    private TextView mBounty;
    private TextView mDeadline;
    private TextView mAuthor;

    private LinearLayout button_camera;
    private LinearLayout button_video;
    private LinearLayout button_mic;
    private Post assignment;
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Don't load the post until we know the width of mContentTextView
        // GlobalLayoutListener on mContentTextView will load the post once it gets laid out
        if (WordPress.currentPost != null && !getView().isLayoutRequested()) {
            loadPost(WordPress.currentPost);
        }
        mParentActivity = (RipotiMainActivity) getActivity();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(SuggestionEvents.SuggestionNameListUpdated event) {
        int remoteBlogId = WordPress.getCurrentRemoteBlogId();
        // check if the updated suggestions are for the current blog and update the suggestions
        if (event.mRemoteBlogId != 0 && event.mRemoteBlogId == remoteBlogId && mSuggestionAdapter != null) {
            List<Suggestion> suggestions = SuggestionTable.getSuggestionsForSite(event.mRemoteBlogId);
            mSuggestionAdapter.setSuggestionList(suggestions);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mSuggestionServiceConnectionManager != null) {
            mSuggestionServiceConnectionManager.unbindFromService();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.view_assignment_fragment, container, false);
        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                assignment = WordPress.currentPost;
                loadPost(assignment);
                v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        mTitleTextView = (TextView) v.findViewById(R.id.postTitle);
        mContentTextView = (TextView) v.findViewById(R.id.viewPostTextView);
        mAuthor = (TextView) v.findViewById(R.id.assignment_post_author);
        mBounty = (TextView) v.findViewById(R.id.text_bounty);
        mLocation = (TextView) v.findViewById(R.id.text_location);
        mDeadline = (TextView) v.findViewById(R.id.assignment_list_deadline);

        //quick capture icons
        button_camera = (LinearLayout)v.findViewById(R.id.button_camera);
        button_video = (LinearLayout)v.findViewById(R.id.button_video);
        button_mic = (LinearLayout)v.findViewById(R.id.button_mic);


        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean mShouldFinish = false;
                Intent intent = new Intent(mParentActivity, StoryBoard.class);
                intent.putExtra("quick-media", DeviceUtils.getInstance().hasCamera(mParentActivity.getApplicationContext())
                        ? Constants.QUICK_POST_PHOTO_CAMERA
                        : Constants.QUICK_POST_PHOTO_LIBRARY);
                intent.putExtra("isNew", true);
                intent.putExtra("shouldFinish", mShouldFinish);
                intent.putExtra("assignment_id", Integer.parseInt(assignment.getRemotePostId()));
                startActivity(intent);
            }
        });

        button_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean mShouldFinish = false;
                Intent intent = new Intent(mParentActivity, StoryBoard.class);
                intent.putExtra("quick-media", DeviceUtils.getInstance().hasCamera(mParentActivity.getApplicationContext())
                        ? Constants.QUICK_POST_VIDEO_CAMERA
                        : Constants.QUICK_POST_PHOTO_LIBRARY);
                intent.putExtra("isNew", true);
                intent.putExtra("shouldFinish", mShouldFinish);
                intent.putExtra("assignment_id", Integer.parseInt(assignment.getRemotePostId()));
                startActivity(intent);
            }
        });

        button_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean mShouldFinish = false;
                Intent intent = new Intent(mParentActivity, StoryBoard.class);
                intent.putExtra("quick-media", Constants.QUICK_POST_AUDIO_MIC);
                intent.putExtra("isNew", true);
                intent.putExtra("shouldFinish", mShouldFinish);
                intent.putExtra("assignment_id", Integer.parseInt(assignment.getRemotePostId()));
                startActivity(intent);

            }
        });




        return v;
    }

    protected void loadPostMeta(final Post post){
        String locationText = post.getAssignmentLocation();
        mLocation.setText(locationText);

        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post.getCoordinates().equals("")) {

                    String gps = post.getCoordinates().replace("(", "");
                    gps = gps.replace(")", "");

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + gps));
                    i.setClassName("com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity");
                    mParentActivity.startActivity(i);
                }
            }
        });

        String bountyText = post.getBounty();
        if (bountyText == null){
            bountyText = "n/a";
        } else {
            if (bountyText.equals("KSH 0"))
                bountyText = "n/a";
            mBounty.setText(bountyText);
        }

        //set author of assignment
        String postAuthor = post.getAuthorDisplayName();
        mAuthor.setText(postAuthor);

        String deadlineText = post.getDeadline();
        if (deadlineText == null){
            deadlineText = "Open";
        }

        //if deadline passed, tint accordingly
        else if(!deadlineText.equals("")){
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            Date deadlineDate = null;
            try {
                deadlineDate = format.parse(deadlineText);

                Date today = new Date();

                if(today.after(deadlineDate)){
                    mDeadline.setTextColor(mParentActivity.getResources().getColor(R.color.alert_orange));
                }else if(today.before(deadlineDate)) {
                    mDeadline.setTextColor(mParentActivity.getResources().getColor(R.color.alert_green));
                }else{
                    //deadline is today
                    mDeadline.setTextColor(mParentActivity.getResources().getColor(R.color.alert_red));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{

            deadlineText = "Open";
        }
        mDeadline.setText(deadlineText);
    }

    /**
     * Load the post preview as an authenticated URL so stats aren't bumped.
     */
    protected void loadPostPreview() {
        if (WordPress.currentPost != null && !TextUtils.isEmpty(WordPress.currentPost.getPermaLink())) {
            Post post = WordPress.currentPost;
            String url = post.getPermaLink();
            if (-1 == url.indexOf('?')) {
                url = url.concat("?preview=true");
            } else {
                url = url.concat("&preview=true");
            }
            WPWebViewActivity.openUrlByUsingBlogCredentials(getActivity(), WordPress.currentBlog, url);
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // check that the containing activity implements our callback
            mOnDetailPostActionListener = (OnDetailAssignmentActionListener) activity;
        } catch (ClassCastException e) {
            activity.finish();
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }

    public void loadPost(final Post post) {
        // Don't load if the Post object or title are null, see #395
        if (!isAdded() || getView() == null || post == null || post.getTitle() == null) {
            return;
        }

        // create handler on UI thread
        final Handler handler = new Handler();

        // locate views and determine content in the background to avoid ANR - especially
        // important when using WPHtml.fromHtml() for drafts that contain images since
        // thumbnails may take some time to create
        final WebView webView = (WebView) getView().findViewById(R.id.viewPostWebView);
        webView.setWebViewClient(new WPWebViewClient(WordPress.getCurrentBlog()));
        new Thread() {
            @Override
            public void run() {

                final String title = (TextUtils.isEmpty(post.getTitle())
                                        ? "(" + getResources().getText(R.string.untitled) + ")"
                                        : StringUtils.unescapeHTML(post.getTitle()));

                final String postContent = post.getDescription() + "\n\n" + post.getMoreText();

                final Spanned draftContent;
                final String htmlContent;
                if (post.isLocalDraft()) {
                    View view = getView();
                    int maxWidth = Math.min(view.getWidth(), view.getHeight());

                    draftContent = WPHtml.fromHtml(postContent.replaceAll("\uFFFC", ""), getActivity(), post, maxWidth);
                    htmlContent = null;
                } else {
                    draftContent = null;
                    htmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                                + "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"webview.css\" /></head>"
                                + "<body><div id=\"container\">"
                                + StringUtils.addPTags(postContent)
                                + "</div></body></html>";
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // make sure activity is still valid
                        if (!isAdded())
                            return;

                        mTitleTextView.setText(title);
                        loadPostMeta(post);
                        if (post.isLocalDraft()) {
                            mContentTextView.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.GONE);

                            mContentTextView.setText(draftContent);
                        } else {
                            mContentTextView.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                            webView.loadDataWithBaseURL("file:///android_asset/",
                                                        htmlContent,
                                                        "text/html",
                                                        "utf-8",
                                                        null);
                        }
                    }
                });
            }
        }.start();
    }

    public interface OnDetailAssignmentActionListener {
        public void onDetailAssignmentAction(int action, Post post);
    }

    public void clearContent() {
        TextView txtTitle = (TextView) getView().findViewById(R.id.postTitle);
        WebView webView = (WebView) getView().findViewById(R.id.viewPostWebView);
        TextView txtContent = (TextView) getView().findViewById(R.id.viewPostTextView);
        txtTitle.setText("");
        txtContent.setText("");
        String htmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                        + "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"webview.css\" /></head>"
                        + "<body><div id=\"container\"></div></body></html>";
        webView.loadDataWithBaseURL("file:///android_asset/", htmlText,
                "text/html", "utf-8", null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState.isEmpty()) {
            outState.putBoolean("bug_19917_fix", true);
        }
        super.onSaveInstanceState(outState);
    }
}
