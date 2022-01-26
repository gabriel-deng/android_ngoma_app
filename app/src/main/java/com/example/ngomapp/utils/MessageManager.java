package com.example.ngomapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.ngomapp.DetailsFragment;
import com.example.ngomapp.MainActivity;

/**
 * this class will contain our code to handle notifications
 */
public class MessageManager {
    Context context;
    int music_icon;
    public static final String CHANNEL_ID=" com.example.ngomapp Notifications";
    private CharSequence BigText = "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Interne";

    public MessageManager(Context context, int music_icon) {
        this.context = context;
        this.music_icon = music_icon;
    }

    public NotificationCompat.Builder setupNotification(String title, String text){
        // this method will be used to build a notification with necessary details
        NotificationCompat.Builder builder= new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(music_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(BigText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder;
    }
    public NotificationCompat.Builder setupNotifications(String title, String text, Bitmap image){

        Intent intent= new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent=  PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(music_icon)
                .setContentText(text)
                .setContentTitle(title)
                .setLargeIcon(image)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(music_icon, "Paused", pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon(null));

        return  builder;
    }
    public NotificationCompat.Builder setupNotificationForPlayButton(String title){

       NotificationCompat.Builder builder= new NotificationCompat.Builder(context, CHANNEL_ID)
       .setSmallIcon(music_icon)
    //  .setContentText(text)
       .setContentTitle(title);


       return builder;
    }

    public  void  createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // this will apply to android 8(API 26) and above.

            CharSequence name= "NgomApp Notification";
            String description= "Notifications from NgomApp";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);

            // here we register the channel with the system
            NotificationManager manager= context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }
//
//    public void creatnewNotificationChannel(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            CharSequence name="NgomaApp Notifications";
//            String description="Notifications from NgomaApp";
//            int importance=NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel= new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//
//            NotificationManager manager=context.getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//    }
}
