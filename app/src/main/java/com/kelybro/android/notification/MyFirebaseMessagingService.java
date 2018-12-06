package com.kelybro.android.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kelybro.android.AppConstants;
import com.kelybro.android.activities.ActivityGPJPFormDownload;
import com.kelybro.android.activities.ChatActivity;
import com.kelybro.android.activities.MainActivity;
import com.kelybro.android.activities.NotificationActivity;
import com.kelybro.android.activities.PostActivity;
import com.kelybro.android.R;
import com.kelybro.android.eventbus.LikeCountMessage;
import com.kelybro.android.eventbus.NotificationMessageCount;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private SharedPreferences mSPF;
    private SharedPreferences.Editor mEDT;
    private SQLiteDatabase mDB;
    String user_profile_pic_thumb,total_likes,total_comments,post_date,user_name,post_id;
    ArrayList<String> myImglists;
    Random random = new Random();
    int m = random.nextInt(9999 - 1000) + 1000;
    int count = 0;
    private NotificationChannel mChannel;
    private NotificationManager notifManager;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mSPF = getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();
        mDB = openOrCreateDatabase("AppDB", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage == null)
            return;

        EventBus.getDefault().post(new NotificationMessageCount(count));

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());

                handleDataMessage(json);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {


        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            String channelId = "Default";
            NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(message)
                    .setContentText(message).setAutoCancel(true).setContentIntent(pendingIntent);;
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            manager.notify(m, builder.build());



        } else {
            // If the app is in background, firebase itself handles the notification
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();


            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            String channelId = "Default";
            NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(message)
                    .setContentText(message).setAutoCancel(true).setContentIntent(pendingIntent);;
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            manager.notify(m, builder.build());


        }
    }



    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            String type = data.getString("type");


            if(title.equals("Post Like")) {
                EventBus.getDefault().post(new LikeCountMessage(count));
                String like = "Post Like";
                Intent intent = new Intent("intentKey");
                intent.putExtra("key", like);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
            else if(title.equals("Comment Notification")) {
                String comment = "Comment Notification";
                Intent intent = new Intent("intentKey");
                intent.putExtra("key", comment);
                intent.putExtra("total_comments", data.getString("total_comments"));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }


            if (data.has("user_name"))
            {
                user_name = data.getString("user_name");
                user_profile_pic_thumb = data.getString("user_profile_pic_thumb");
                total_likes = data.getString("total_likes");
                total_comments = data.getString("total_comments");
                post_date = data.getString("created_date");
                post_id = data.getString("post_id");

                myImglists = new ArrayList<String>();
                if(data.getString("post_image")!="") {
                    JSONObject mpostimage = data.getJSONObject("post_image");
                    for (int i = 0; i<=mpostimage.length();i++) {
                        myImglists.add(AppConstants.imgUrl + mpostimage.getString("main_img_" +0));
                    }
                }

            }

            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            ContentValues mCV = new ContentValues();
            mDB.execSQL(getString(R.string.notification));
            mCV.put("client_code", "All");
            mCV.put("title", title);
            mCV.put("details", message);
            mCV.put("notification_date", timestamp);
            mCV.put("type", type);
            mDB.insert("notification", null, mCV);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) //In front Screen
            {

                // app is in foreground, broadcast the push message

           /*     Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);*/

                Intent intentLike;

                if(user_name!=null){
                    intentLike = new Intent(getApplicationContext(), PostActivity.class);
                    intentLike.putExtra("user_name",user_name);
                    intentLike.putExtra("user_profile_pic_thumb",user_profile_pic_thumb);
                    intentLike.putExtra("total_likes",total_likes);
                    intentLike.putExtra("total_comments",total_comments);
                    intentLike.putExtra("created_date",post_date);
                    intentLike.putExtra("post_image",myImglists.get(1));
                    intentLike.putExtra("post_id",post_id);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor prefsEditor = preferences.edit();
                    prefsEditor.putString("user_name", user_name);
                    prefsEditor.putString("user_profile_pic_thumb", user_profile_pic_thumb);
                    prefsEditor.putString("total_likes", total_likes);
                    prefsEditor.putString("total_comments", total_comments);
                    prefsEditor.putString("created_date", post_date);
                    prefsEditor.putString("post_image", myImglists.get(1));
                    prefsEditor.putString("post_id", post_id);
                    prefsEditor.apply();


                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentLike);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, intentLike);
                    } else {
                        // image is present, show notification with image
                      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentLike);
                        }*/
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, intentLike, imageUrl);
                    }

                }

                else if(title.equals("Upload image")){
                    Intent chatIntent = new Intent(getApplicationContext(), NotificationActivity.class);
                    chatIntent.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,chatIntent);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, chatIntent);

                    } else {
                        // image is present, show notification with image

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,chatIntent);
                        }*/
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, chatIntent, imageUrl);
                    }
                }

                else if(title.equals("Chat NotificationMessageCount")){

                    Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                    chatIntent.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,chatIntent);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, chatIntent);

                    } else {
                        // image is present, show notification with image

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,chatIntent);
                        }*/
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, chatIntent, imageUrl);
                    }
                }
                else if(title.equals("Following Notification"))
                {

                    Intent intentFollow = new Intent(getApplicationContext(), NotificationActivity.class);
                    intentFollow.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, intentFollow);
                    } else {
                        // image is present, show notification with image
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, intentFollow, imageUrl);
                    }

                }
                else if(title.equals("Job Notification"))
                {
                    Intent intentFollow = new Intent(getApplicationContext(), NotificationActivity.class);
                    intentFollow.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, intentFollow);
                    } else {

                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, intentFollow, imageUrl);
                    }
                }

                else if(title.equals("GPJP Notification"))
                {
                    Intent intentFollow = new Intent(getApplicationContext(), ActivityGPJPFormDownload.class);
                    intentFollow.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, intentFollow);
                    } else {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, intentFollow, imageUrl);
                    }
                }

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();

            } else {
                // app is in background, show the notification in notification tray

                Intent intentLike;
                if(user_name!=null){
                    intentLike = new Intent(getApplicationContext(), PostActivity.class);
                    intentLike.putExtra("user_name",user_name);
                    intentLike.putExtra("user_profile_pic_thumb",user_profile_pic_thumb);
                    intentLike.putExtra("total_likes",total_likes);
                    intentLike.putExtra("total_comments",total_comments);
                    intentLike.putExtra("created_date",post_date);
                    intentLike.putExtra("post_image",myImglists.get(1));
                    intentLike.putExtra("post_id",post_id);


                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentLike);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, intentLike);
                    } else {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentLike);
                        }*/
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, intentLike, imageUrl);
                    }

                }

                else if(title.equals("Upload image")){
                    Intent chatIntent = new Intent(getApplicationContext(), NotificationActivity.class);
                    chatIntent.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {


                        showNotificationMessage(getApplicationContext(), title, message, timestamp, chatIntent);

                    } else {
                        // image is present, show notification with image

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,chatIntent);
                        }*/
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, chatIntent, imageUrl);
                    }
                }

                else if(message.equals("Chat NotificationMessageCount")){
                    Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                    resultIntent.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,resultIntent);
                        }*/

                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    } else {
                      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,resultIntent);
                        }*/

                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                }

                else if(title.equals("Following Notification"))
                {
                    Intent intentFollow = new Intent(getApplicationContext(), NotificationActivity.class);
                    intentFollow.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, intentFollow);
                    } else {
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, intentFollow, imageUrl);
                    }
                }
                else if(title.equals("Job Notification"))
                {
                    Intent intentFollow = new Intent(getApplicationContext(), NotificationActivity.class);
                    intentFollow.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, intentFollow);
                    } else {
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, intentFollow, imageUrl);
                    }
                }

                else if(title.equals("GPJP Notification"))
                {
                    Intent intentFollow = new Intent(getApplicationContext(), ActivityGPJPFormDownload.class);
                    intentFollow.putExtra("message", title);

                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, intentFollow);
                    } else {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showOreoNotifications(title, message,intentFollow);
                        }*/
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, intentFollow, imageUrl);
                    }
                }

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();


            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void showOreoNotifications(String title, String message,Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationCompat.Builder builder;
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                String channelId = getString(R.string.default_notification_channel_id);
                mChannel = new NotificationChannel
                        (channelId, title, importance);
                mChannel.setDescription(message);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");

            final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notificationsound");

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, m, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher_round) // required
                    .setContentText(message)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setBadgeIconType(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .setSound(alarmSound);
            Notification notification = builder.build();
            notifManager.notify(m, notification);
        }
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
