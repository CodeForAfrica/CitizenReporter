package org.codeforafrica.citizenreporter.eNCA.models;

import android.text.format.DateUtils;

import org.wordpress.android.util.StringUtils;

import java.util.Date;

/**
 * Barebones post/page as listed in AssigmnentsListFragment
 */
public class AssignmentsListPost {
    private int postId;
    private int blogId;
    private String title;
    private String excerpt;
    private String location;

    public String getBounty() {
        return bounty;
    }

    public void setBounty(String bounty) {
        this.bounty = bounty;
    }

    public String getMedia_types() {
        return media_types;
    }

    public void setMedia_types(String media_types) {
        this.media_types = media_types;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    private String bounty;
    private String media_types;
    private String deadline;
    private String coordinates;
    private String postAuthor;
    private String postThumb;
    private String postAvatar;
    private long dateCreatedGmt;
    private String status;
    private boolean isLocalDraft;
    private boolean hasLocalChanges;
    private boolean mIsUploading;
    private String assignmentID;

    public AssignmentsListPost(int postId, int blogId, String assignmentID, String title, String excerpt,
                               String location,
                               String coordinates, String postAuthor, String postThumb, String postAvatar,
                               String deadline,
                               String bounty,
                               String media_types, long dateCreatedGmt, String status, boolean localDraft,
                               boolean localChanges, boolean uploading) {
        setPostId(postId);
        setAssignmentID(assignmentID);
        setBlogId(blogId);
        setTitle(title);
        setExcerpt(excerpt);
        setLocation(location);
        setCoordinates(coordinates);
        setPostAuthor(postAuthor);
        setPostThumb(postThumb);
        setPostAvatar(postAvatar);
        setDeadline(deadline);
        setBounty(bounty);
        setMedia_types(media_types);
        setDateCreatedGmt(dateCreatedGmt);
        setStatus(status);
        setLocalDraft(localDraft);
        setHasLocalChanges(localChanges);
        setIsUploading(uploading);
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return StringUtils.notNullStr(title);
    }
    public String getExcerpt() {
        return StringUtils.notNullStr(excerpt);
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public long getDateCreatedGmt() {
        return dateCreatedGmt;
    }

    public void setDateCreatedGmt(long dateCreatedGmt) {
        this.dateCreatedGmt = dateCreatedGmt;
    }

    public String getOriginalStatus() {
        return StringUtils.notNullStr(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormattedDate() {
        return DateUtils.getRelativeTimeSpanString(getDateCreatedGmt(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS).toString();
    }

    public boolean isLocalDraft() {
        return isLocalDraft;
    }

    public void setLocalDraft(boolean isLocalDraft) {
        this.isLocalDraft = isLocalDraft;
    }

    public boolean hasLocalChanges() {
        return hasLocalChanges;
    }

    public void setHasLocalChanges(boolean localChanges) {
        this.hasLocalChanges = localChanges;
    }

    public boolean isUploading() {
        return mIsUploading;
    }

    public void setIsUploading(boolean uploading) {
        this.mIsUploading = uploading;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getPostAuthor() {
        return postAuthor;
    }

    public void setPostAuthor(String postAuthor) {
        this.postAuthor = postAuthor;
    }

    public String getPostThumb() {
        return postThumb;
    }

    public void setPostThumb(String postThumb) {
        this.postThumb = postThumb;
    }

    public String getPostAvatar() {
        return postAvatar;
    }

    public void setPostAvatar(String postAvatar) {
        this.postAvatar = postAvatar;
    }

    public String getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(String assignmentID) {
        this.assignmentID = assignmentID;
    }
}
