package com.kelybro.android.customviews;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by krishna on 16/02/18.
 */

public class Utils {
    public static final String urlUpload = "http://sachinverma.co.in/services/upload_multiple_images/upload_images.php";
    public static final int REQCODE = 100;
    public static final String imageList = "imageList";
    public static final String imageName = "name";






    public static String getDateDifference(Date thenDate){
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        now.setTime(new Date());
        then.setTime(thenDate);


        // Get the represented date in milliseconds
        long nowMs = now.getTimeInMillis();
        long thenMs = then.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = nowMs - thenMs;

        // Calculate difference in seconds
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffMinutes<60){
            if (diffMinutes==1)
                return diffMinutes + " minute ago";
            else
                return diffMinutes + " minutes ago";
        } else if (diffHours<24){
            if (diffHours==1)
                return diffHours + " hour ago";
            else
                return diffHours + " hours ago";
        }else if (diffDays<30){
            if (diffDays==1)
                return diffDays + " day ago";
            else
                return diffDays + " days ago";
        }else {
            return "a long time ago..";
        }
    }
}
