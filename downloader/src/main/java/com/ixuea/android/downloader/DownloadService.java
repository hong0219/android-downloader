package com.ixuea.android.downloader;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.config.Config;

import java.util.List;

/**
 * Created by ixuea(http://a.ixuea.com/3) on 19/9/2021.
 */

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    public static DownloadManager downloadManager;

    public static DownloadManager getDownloadManager(Context context) {
        return getDownloadManager(context, null);
    }

    public static DownloadManager getDownloadManager(Context context, Config config) {
        if (!isServiceRunning(context)) {
            Intent downloadSvr = new Intent(context, DownloadService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //context.startForegroundService(downloadSvr);
            } else {
                context.startService(downloadSvr);
            }
        }
        if (DownloadService.downloadManager == null) {
            DownloadService.downloadManager = DownloadManagerImpl.getInstance(context, config);
        }
        return downloadManager;
    }

    private static boolean isServiceRunning(Context context) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(
                    DownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//onCreate Error!  An error occurs when the application ends during download ( code -> startForeground(1,notification); )
//Logcat : Unable to create service com.ixuea.android.downloader.DownloadService: android.app.ForegroundServiceStartNotAllowedException: Service.startForeground() not allowed due to mAllowStartForeground false
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String Channel_id = "DOWNLOAD_SERVICE";
//            String Channel_name = "DOWNLOAD_CHANNEL";
//            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    Channel_id,
//                    Channel_name,
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            manager.createNotificationChannel(serviceChannel);
//            Notification notification = new NotificationCompat.Builder(this,Channel_id).setContentTitle("").setContentText("").setPriority(NotificationCompat.PRIORITY_LOW).setAutoCancel(true).setDefaults(0).build();
//            startForeground(1,notification); //An error occurs when the application ends during download
//            stopForeground(true);            
//        }
//    }

    @Override
    public void onDestroy() {
        if (downloadManager != null) {
            downloadManager.destroy();
            downloadManager = null;
        }        
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            stopForeground(true);
//        }
        super.onDestroy();
    }
}
