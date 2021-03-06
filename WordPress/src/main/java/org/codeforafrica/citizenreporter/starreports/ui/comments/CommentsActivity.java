package org.codeforafrica.citizenreporter.starreports.ui.comments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cocosw.undobar.UndoBarController;

import org.codeforafrica.citizenreporter.starreports.R;
import org.codeforafrica.citizenreporter.starreports.WordPress;
import org.codeforafrica.citizenreporter.starreports.models.BlogPairId;
import org.codeforafrica.citizenreporter.starreports.models.Comment;
import org.codeforafrica.citizenreporter.starreports.models.CommentStatus;
import org.codeforafrica.citizenreporter.starreports.models.Note;
import org.codeforafrica.citizenreporter.starreports.ui.ActivityLauncher;
import org.codeforafrica.citizenreporter.starreports.ui.comments.CommentsListFragment.OnCommentSelectedListener;
import org.codeforafrica.citizenreporter.starreports.ui.notifications.NotificationFragment;
import org.codeforafrica.citizenreporter.starreports.ui.reader.ReaderPostDetailFragment;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.ToastUtils;

import javax.annotation.Nonnull;

public class CommentsActivity extends ActionBarActivity
        implements OnCommentSelectedListener,
                   NotificationFragment.OnPostClickListener,
                   CommentActions.OnCommentActionListener,
                   CommentActions.OnCommentChangeListener {
    private static final String KEY_SELECTED_COMMENT_ID = "selected_comment_id";
    private static final String KEY_SELECTED_POST_ID = "selected_post_id";
    static final String KEY_AUTO_REFRESHED = "has_auto_refreshed";
    static final String KEY_EMPTY_VIEW_MESSAGE = "empty_view_message";
    private long mSelectedCommentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);

        setContentView(R.layout.comment_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.feedback));

        restoreSavedInstance(savedInstanceState);
    }

    private void restoreSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            getIntent().putExtra(KEY_AUTO_REFRESHED, savedInstanceState.getBoolean(KEY_AUTO_REFRESHED));
            getIntent().putExtra(KEY_EMPTY_VIEW_MESSAGE, savedInstanceState.getString(KEY_EMPTY_VIEW_MESSAGE));

            // restore the selected comment
            long commentId = savedInstanceState.getLong(KEY_SELECTED_COMMENT_ID);
            if (commentId != 0) {
                onCommentSelected(commentId);
            }
            // restore the post detail fragment if one was selected
            BlogPairId selectedPostId = (BlogPairId) savedInstanceState.get(KEY_SELECTED_POST_ID);
            if (selectedPostId != null) {
                showReaderFragment(selectedPostId.getRemoteBlogId(), selectedPostId.getId());
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        ActivityLauncher.slideOutToRight(this);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AppLog.d(AppLog.T.COMMENTS, "comment activity new intent");
    }


    private CommentDetailFragment getDetailFragment() {
        Fragment fragment = getFragmentManager().findFragmentByTag(getString(
                R.string.fragment_tag_comment_detail));
        if (fragment == null) {
            return null;
        }
        return (CommentDetailFragment) fragment;
    }

    private boolean hasDetailFragment() {
        return (getDetailFragment() != null);
    }

    private CommentsListFragment getListFragment() {
        Fragment fragment = getFragmentManager().findFragmentByTag(getString(
                R.string.fragment_tag_comment_list));
        if (fragment == null) {
            return null;
        }
        return (CommentsListFragment) fragment;
    }

    private boolean hasListFragment() {
        return (getListFragment() != null);
    }

    private void showReaderFragment(long remoteBlogId, long postId) {
        FragmentManager fm = getFragmentManager();
        fm.executePendingTransactions();

        Fragment fragment = ReaderPostDetailFragment.newInstance(remoteBlogId, postId);
        FragmentTransaction ft = fm.beginTransaction();
        String tagForFragment = getString(R.string.fragment_tag_reader_post_detail);
        ft.add(R.id.layout_fragment_container, fragment, tagForFragment)
          .addToBackStack(tagForFragment)
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (hasDetailFragment())
            ft.hide(getDetailFragment());
        ft.commit();
    }

    /*
     * called from comment list when user taps a comment
     */
    @Override
    public void onCommentSelected(long commentId) {
        mSelectedCommentId = commentId;
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return;
        }
        fm.executePendingTransactions();
        CommentsListFragment listFragment = getListFragment();

        FragmentTransaction ft = fm.beginTransaction();
        String tagForFragment = getString(R.string.fragment_tag_comment_detail);
        CommentDetailFragment detailFragment = CommentDetailFragment.newInstance(WordPress.getCurrentLocalTableBlogId(),
                commentId);
        ft.add(R.id.layout_fragment_container, detailFragment, tagForFragment).addToBackStack(tagForFragment)
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (listFragment != null) {
            ft.hide(listFragment);
        }
        ft.commitAllowingStateLoss();
    }

    /*
     * called from comment detail when user taps a link to a post - show the post in a
     * reader detail fragment
     */
    @Override
    public void onPostClicked(Note note, int remoteBlogId, int postId) {
        showReaderFragment(remoteBlogId, postId);
    }

    /*
     * reload the comment list from existing data
     */
    private void reloadCommentList() {
        CommentsListFragment listFragment = getListFragment();
        if (listFragment != null)
            listFragment.loadComments();
    }

    /*
     * tell the comment list to get recent comments from server
     */
    void updateCommentList() {
        CommentsListFragment listFragment = getListFragment();
        if (listFragment != null) {
            listFragment.setRefreshing(true);
            listFragment.updateComments(false);
        }
    }

    @Override
    public void onSaveInstanceState(@Nonnull Bundle outState) {
        // https://code.google.com/p/android/issues/detail?id=19917
        if (outState.isEmpty()) {
            outState.putBoolean("bug_19917_fix", true);
        }

        // retain the id of the highlighted and selected comments
        if (mSelectedCommentId != 0) {
            outState.putLong(KEY_SELECTED_COMMENT_ID, mSelectedCommentId);
        }

        if (hasListFragment()) {
            outState.putBoolean(KEY_AUTO_REFRESHED, getListFragment().mHasAutoRefreshedComments);
            outState.putString(KEY_EMPTY_VIEW_MESSAGE, getListFragment().getEmptyViewMessage());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = CommentDialogs.createCommentDialog(this, id);
        if (dialog != null)
            return dialog;
        return super.onCreateDialog(id);
    }

    @Override
    public void onModerateComment(final int accountId, final Comment comment,
                                  final CommentStatus newStatus) {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }

        if (newStatus == CommentStatus.APPROVED || newStatus == CommentStatus.UNAPPROVED) {
            getListFragment().setCommentIsModerating(comment.commentID, true);
            CommentActions.moderateComment(accountId, comment, newStatus,
                    new CommentActions.CommentActionListener() {
                @Override
                public void onActionResult(boolean succeeded) {
                    if (isFinishing() || !hasListFragment()) {
                        return;
                    }

                    getListFragment().setCommentIsModerating(comment.commentID, false);

                    if (succeeded) {
                        getListFragment().updateComments(false);
                    } else {
                        ToastUtils.showToast(CommentsActivity.this,
                                R.string.error_moderate_comment,
                                ToastUtils.Duration.LONG
                        );
                    }
                }
            });
        } else if (newStatus == CommentStatus.SPAM || newStatus == CommentStatus.TRASH) {
            // Remove comment from comments list
            getListFragment().removeComment(comment);
            getListFragment().setCommentIsModerating(comment.commentID, true);

            new UndoBarController.UndoBar(this)
                    .message(newStatus == CommentStatus.TRASH ? R.string.comment_trashed : R.string.comment_spammed)
                    .listener(new UndoBarController.AdvancedUndoListener() {
                        @Override
                        public void onHide(Parcelable parcelable) {
                            CommentActions.moderateComment(accountId, comment, newStatus,
                                    new CommentActions.CommentActionListener() {
                                @Override
                                public void onActionResult(boolean succeeded) {
                                    if (isFinishing() || !hasListFragment()) {
                                        return;
                                    }

                                    getListFragment().setCommentIsModerating(comment.commentID, false);

                                    if (!succeeded) {
                                        // show comment again upon error
                                        getListFragment().loadComments();
                                        ToastUtils.showToast(CommentsActivity.this,
                                                R.string.error_moderate_comment,
                                                ToastUtils.Duration.LONG
                                        );
                                    }
                                }
                            });
                        }

                        @Override
                        public void onClear(Parcelable[] token) {
                            //noop
                        }

                        @Override
                        public void onUndo(Parcelable parcelable) {
                            getListFragment().setCommentIsModerating(comment.commentID, false);
                            // On undo load from the db to show the comment again
                            getListFragment().loadComments();
                        }
                    }).show();
        }
    }

    @Override
    public void onCommentChanged(CommentActions.ChangedFrom changedFrom, CommentActions.ChangeType changeType) {
        if (changedFrom == CommentActions.ChangedFrom.COMMENT_DETAIL
                && changeType == CommentActions.ChangeType.EDITED) {
            reloadCommentList();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
