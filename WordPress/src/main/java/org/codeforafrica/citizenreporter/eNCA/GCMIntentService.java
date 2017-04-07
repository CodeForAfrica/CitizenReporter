package org.codeforafrica.citizenreporter.eNCA;

import android.content.Context;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import org.codeforafrica.citizenreporter.eNCA.chat.ChatActivity;
import org.codeforafrica.citizenreporter.eNCA.chat.Message;
import org.codeforafrica.citizenreporter.eNCA.ui.comments.CommentsActivity;
import org.codeforafrica.citizenreporter.eNCA.ui.posts.StoryBoard;
import org.wordpress.android.util.DeviceUtils;
import org.codeforafrica.citizenreporter.eNCA.wallet.Payment;
import org.codeforafrica.citizenreporter.eNCA.wallet.PaymentsListActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    private WordPress aController = null;

    private int messageType;
    private String message;
    private String assignmentID;
    private String assignmentDeadline;
    private String user;
    private String post_id;
    private String receipt;
    private String payment_id;
    private String pay_amount;

    private Bitmap iconFromUrl;
    public GCMIntentService() {
        // Call extended class Constructor GCMBaseIntentService
        super(BuildConfig.GCM_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {

        //Get Global Controller Class object (see application tag in AndroidManifest.xml)
        if(aController == null)
            aController = (WordPress) getApplicationContext();

        Log.i(TAG, "Device registered: regId = " + registrationId);
        aController.displayMessageOnScreen(context, "Your device registred with GCM");
        aController.register(context, registrationId);
    }

    /**
     * Method called on device unregistred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        if(aController == null)
            aController = (WordPress) getApplicationContext();
        Log.i(TAG, "Device unregistered");
        aController.displayMessageOnScreen(context, getString(R.string.gcm_unregistered));
        aController.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message from GCM server
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {

        if(aController == null)
            aController = (WordPress) getApplicationContext();

        Log.i(TAG, "Received message");

        //compose message depending on type
        if(intent.hasExtra("assignment")){
            messageType = 0;
            message = intent.getExtras().getString("assignment");
            //should have assignmentID
            assignmentID = intent.getExtras().getString("assignmentID");
            assignmentDeadline = "" + intent.getExtras().getString("assignmentDeadline");
            if(assignmentDeadline.equals("") || assignmentDeadline.equals("null")){
                assignmentDeadline = "Open ended";
            }else{
                assignmentDeadline = "Due on " + assignmentDeadline;
            }
            generateAdvancedAssignmentNotification();
        }else if(intent.hasExtra("feedback")){
            messageType = 1;
            message = intent.getExtras().getString("feedback");
            user = intent.getExtras().getString("author");

            iconFromUrl = iconFromUrl(intent.getExtras().getString("icon_url"));

            generateFeedbackNotification();
        }else if(intent.hasExtra("chat")){
            messageType = 2;
            message = intent.getExtras().getString("chat");
            user = intent.getExtras().getString("user");

            iconFromUrl = null;//iconFromUrl(intent.getExtras().getString("icon_url"));

            generateChatNotification();
        }else if(intent.hasExtra("payment")){
            messageType = 3;
            message = intent.getExtras().getString("payment");
            receipt = intent.getExtras().getString("receipt");
            post_id = intent.getExtras().getString("post_id");
            payment_id = intent.getExtras().getString("payment_id");
            pay_amount = intent.getExtras().getString("pay_amount");

            iconFromUrl = null;//iconFromUrl(intent.getExtras().getString("icon_url"));

            generatePaymentNotification();
        }

        aController.displayMessageOnScreen(context, message);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {

        if(aController == null)
            aController = (WordPress) getApplicationContext();

        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        aController.displayMessageOnScreen(context, message);
        // notifies user
        //generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {

        if(aController == null)
            aController = (WordPress) getApplicationContext();

        Log.i(TAG, "Received error: " + errorId);
        aController.displayMessageOnScreen(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {

        if(aController == null)
            aController = (WordPress) getApplicationContext();

        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        aController.displayMessageOnScreen(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    public Intent imageIntent(){
        Intent intent = new Intent(this, StoryBoard.class);
        intent.putExtra("quick-media", DeviceUtils.getInstance().hasCamera(getApplicationContext())
                ? Constants.QUICK_POST_PHOTO_CAMERA
                : Constants.QUICK_POST_PHOTO_LIBRARY);
        intent.putExtra("isNew", true);
        intent.putExtra("shouldFinish", false);
        intent.putExtra("assignment_id", Integer.parseInt(assignmentID));

        return intent;
    }
    public Intent videoIntent(){
        Intent intent = new Intent(this, StoryBoard.class);
        intent.putExtra("quick-media", DeviceUtils.getInstance().hasCamera(getApplicationContext())
                ? Constants.QUICK_POST_VIDEO_CAMERA
                : Constants.QUICK_POST_PHOTO_LIBRARY);
        intent.putExtra("isNew", true);
        intent.putExtra("shouldFinish", false);
        intent.putExtra("assignment_id", Integer.parseInt(assignmentID));

        return intent;
    }
    public Intent audioIntent(){
        Intent intent = new Intent(this, StoryBoard.class);
        intent.putExtra("quick-media", Constants.QUICK_POST_AUDIO_MIC);
        intent.putExtra("isNew", true);
        intent.putExtra("shouldFinish", false);
        intent.putExtra("assignment_id", Integer.parseInt(assignmentID));

        return intent;
    }

    private Bitmap iconFromUrl(String strURL){
            try {
                URL url = new URL(strURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


    private void generateAdvancedAssignmentNotification(){
            PendingIntent imageIntent = PendingIntent.getActivity(this, 0, imageIntent(), 0);
            PendingIntent videoIntent = PendingIntent.getActivity(this, 0, videoIntent(), 0);
            PendingIntent audioIntent = PendingIntent.getActivity(this, 0, audioIntent(), 0);

            Notification notif = new Notification.Builder(getApplicationContext())
                    .setContentTitle("New Assignment" )
                    .setContentText(getResources().getString(R.string.expand_to_view))
                    .setSmallIcon(R.drawable.app_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ab_icon_edit))
                    .setStyle(new Notification.InboxStyle()
                            .addLine(message)
                            .setBigContentTitle("New Assignment")
                            .setSummaryText(assignmentDeadline))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .addAction(R.mipmap.ic_camera_white, "", imageIntent)
                    .addAction(R.mipmap.ic_video_white, "", videoIntent)
                    .addAction(R.mipmap.ic_audio_white, "", audioIntent)
                    .build();
            NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(55, notif);
    }
    private void generatePaymentNotification() {
        if(iconFromUrl==null){
            iconFromUrl = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bounty);
        }
        //insert to db
        Payment payment = new Payment();
        payment.setMessage(message.trim());
        payment.setReceipt(receipt);
        payment.setPost(post_id);
        payment.setConfirmed("0");
        payment.setRemoteID(payment_id);
        payment.setAmount(pay_amount);

        WordPress.wpDB.addPayment(payment);

        //create intent
        Intent confirmIntent_ = new Intent(getApplicationContext(), PaymentsListActivity.class);
        confirmIntent_.putExtra("confirm", 1);

        PendingIntent confirmIntent = PendingIntent.getActivity(this, 0, confirmIntent_, 0);

        Intent disputeIntent_ = new Intent(getApplicationContext(), PaymentsListActivity.class);
        disputeIntent_.putExtra("confirm", 0);

        PendingIntent disputeIntent = PendingIntent.getActivity(this, 0, disputeIntent_, 0);

        //build notification
        Notification notif = new Notification.Builder(getApplicationContext())
                .setContentTitle(getApplicationContext().getResources().getString(R.string.payment_notification))
                .setContentText(message)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(iconFromUrl)
                .setStyle(new Notification.InboxStyle()
                        .addLine(message)
                        .setBigContentTitle(getApplicationContext().getResources().getString(R.string.payment_notification))/*
                        .setSummaryText(assignmentDeadline)*/)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(R.drawable.ic_check_white_24dp, "Confirm", confirmIntent)
                .addAction(R.drawable.hs__action_cancel, "Dispute", disputeIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(57, notif);
    }
    private void generateChatNotification() {
        if(iconFromUrl==null){
            iconFromUrl = BitmapFactory.decodeResource(getResources(), R.drawable.me_icon_support);
        }
        //insert to db
        Message chat = new Message();
        chat.setMessage(message.trim());
        chat.setIsMine("2");
        WordPress.wpDB.addMessage(chat);

        //create intent
        Intent chatIntent1 = new Intent(getApplicationContext(), ChatActivity.class);
        PendingIntent chatIntent = PendingIntent.getActivity(this, 0, chatIntent1, 0);

        //build notification
        Notification notif = new Notification.Builder(getApplicationContext())
                .setContentTitle(user)
                .setContentText(message)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(iconFromUrl)
                .setStyle(new Notification.InboxStyle()
                        .addLine(message)
                        .setBigContentTitle(user)/*
                        .setSummaryText(assignmentDeadline)*/)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(R.mipmap.ic_reply, "Reply", chatIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(56, notif);
    }
    private void generateFeedbackNotification() {
        if(iconFromUrl==null){
            iconFromUrl = BitmapFactory.decodeResource(getResources(), R.drawable.my_site_icon_comments);
        }

        //create intent
        Intent feedbackIntent1 = new Intent(getApplicationContext(), CommentsActivity.class);
        PendingIntent feedbackIntent = PendingIntent.getActivity(this, 0, feedbackIntent1, 0);

        //build notification
        Notification notif = new Notification.Builder(getApplicationContext())
                .setContentTitle(user)
                .setContentText(message)
                        .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(iconFromUrl)
                .setStyle(new Notification.InboxStyle()
                        .addLine(message)
                        .setBigContentTitle(user))
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(R.mipmap.ic_reply, "Reply", feedbackIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(57, notif);
    }

    public static void clearNotificationsMap() {
    }

    public static int PUSH_NOTIFICATION_ID=0;

    public boolean shouldCircularizeNoteIcon(String type) {
        return false;
    }
}