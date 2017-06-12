export class StoryDataModel {
    assignmentDeadline: any;
    localTablePostId: number;
    localTableBlogId: number;
    categories: string;
    customFields: any[];
    dateCreated: any;
    dateCreatedGmt: any;
    description: string;
    link: string;
    allowComments: boolean;
    allowPings: boolean;
    excerpt: string;
    keywords: string;
    moreText: string;
    permaLink: string;
    status: string;
    remotePostId: string;
    title: string;
    userId: string;
    authorDisplayName: string;
    authorId: string;
    password: string;
    postFormat: string;
    slug: string;
    location: string;
    bounty: string;
    media_types: string;
    deadline: string;
    localDraft: boolean;
    uploaded: boolean;
    mIsUploading: boolean;
    mChangedFromLocalDraftToPublished: boolean;
    isPage: boolean;
    pageParentId: string;
    pageParentTitle: string;
    isLocalChange: boolean;
    mediaPaths: string;
    quickPostType: string;
    assignment_id: string;
    PostLocation: string;
    mPostLocation: string;
    remoteMediaPaths: string[];

    coordinates: string[];

    qwhy: string;
    qwhen: string;
    qhow: string;

    constructor() {
    }

    public getQwhen_date() {
        return this.qwhen;
    }

        public setQwhen_date(qwhen_date:string) {
        this.qwhen = qwhen_date;
    }


    public getRemoteMediaPaths(){
        return this.remoteMediaPaths;
    }

    public setRemoteMediaPaths(remoteMediaPaths) {
        this.remoteMediaPaths = remoteMediaPaths;
    }



    public getLocalTablePostId() {
        return this.localTablePostId;
    }

    public getDateCreated() {
        return this.dateCreated;
    }

    public setDateCreated(dateCreated) {
        this.dateCreated = dateCreated;
    }

    public getDate_created_gmt() {
        return this.dateCreatedGmt;
    }

    public setDate_created_gmt(dateCreatedGmt) {
        this.dateCreatedGmt = dateCreatedGmt;
    }


    public setCustomFields(customFields) {
        this.customFields = customFields;
    }

    public getLocalTableBlogId() {
        return this.localTableBlogId;
    }

    public setLocalTableBlogId(localTableBlogId) {
        this.localTableBlogId = localTableBlogId;
    }

    public isLocalDraft() {
        return this.localDraft;
    }

    public setLocalDraft(localDraft) {
        this.localDraft = localDraft;
    }


    public getDescription() {
        return this.description;
    }

    public setDescription(description: string) {
        this.description = description;
    }


    public getPostStatus() {
        return this.status;
    }

    public setPostStatus(postStatus) {
        this.status = postStatus;
    }


    public getRemotePostId() {
        return this.remotePostId;
    }

    public setRemotePostId(postId: string) {
        this.remotePostId = postId;
    }

    public getTitle() {
        return this.title;
    }

    public setTitle(title: string) {
        this.title = title;
    }

    public setLocation(location: string) {
        this.location = location;
    }


    public getStringLocation() {
        return location;
    }

    public setStringLocation( _location: string) {
        this.location = _location;
    }

    public getMedia_types() {
        return this.media_types;
    }
    public getDeadline() {
        return this.deadline;
    }

    public getUserId() {
        return this.userId;
    }

    public setUserId(userid: string) {
        this.userId = userid;
    }

    public getAuthorDisplayName() {
        return this.authorDisplayName;
    }

    public setAuthorDisplayName(wpAuthorDisplayName: string) {
        this.authorDisplayName = wpAuthorDisplayName;
    }

    public getAuthorId() {
        return this.authorId;
    }

    public setAuthorId(wpAuthorId: string) {
        this.authorId = wpAuthorId;
    }


    public getMediaPaths() {
        return this.mediaPaths;
    }

    public setMediaPaths(mediaPaths: string) {
        this.mediaPaths = mediaPaths;
    }


    public isUploading() {
        return this.mIsUploading;
    }

    public setUploading(uploading: boolean) {
        this.mIsUploading = uploading;
    }

    public isUploaded() {
        return this.uploaded;
    }

    public setUploaded(uploaded: boolean) {
        this.uploaded = uploaded;
    }


    public setLocalTablePostId(id) {
        this.localTablePostId = id;
    }


    public getAssignment_id() {
        return this.assignment_id;
    }

    public setAssignment_id(assignment_id) {
        this.assignment_id = assignment_id;
    }


    public getAssignmentDeadline() {
        return this.assignmentDeadline;
    }


    public getCoordinates() {
        return this.coordinates;
    }

    public setCoordinates(coordinates) {
        this.coordinates = coordinates;
    }

    public getQwhy() {
        return this.qwhy;
    }

    public setQwhy(qwhy) {
        this.qwhy = qwhy;
    }


    public getQwhen() {
        return this.qwhen;
    }

    public setQwhen(qwhen) {
        this.qwhen = qwhen;
    }

    public getQhow() {
        return this.qhow;
    }

    public setQhow(qhow) {
        this.qhow = qhow;
    }
}