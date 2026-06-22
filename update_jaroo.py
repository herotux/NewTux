import os

content = """package org.telegram.messenger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import org.telegram.tgnet.TLRPC;
import java.io.File;
import java.util.ArrayList;

public class JarooBarghiService extends Service implements NotificationCenter.NotificationCenterDelegate {

    private static final String CHANNEL_ID = "JarooBarghiChannel";
    private static final int NOTIFICATION_ID = 1001;

    private int currentAccount;
    private long dialogId;
    private String downloadPath;
    private boolean isRunning = false;
    private int totalFiles = 0;
    private int downloadedFiles = 0;
    private int lastMaxId = 0;
    private boolean hasMore = true;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("STOP".equals(action)) {
                stopSelf();
                return START_NOT_STICKY;
            }

            currentAccount = intent.getIntExtra("account", 0);
            dialogId = intent.getLongExtra("dialogId", 0);
            downloadPath = intent.getStringExtra("path");

            if (!isRunning) {
                isRunning = true;
                NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.mediaDidLoad);
                startForeground(NOTIFICATION_ID, buildNotification("Searching for media..."));
                loadNextBatch();
            }
        }
        return START_NOT_STICKY;
    }

    private void loadNextBatch() {
        if (!hasMore || !isRunning) {
            stopSelf();
            return;
        }
        // type 0 = photo/video, 1 = document, 2 = audio, 3 = link, 4 = music, 5 = voice
        // We probably want to iterate multiple types or let user choose. For now, let's do all except links.
        MediaDataController.getInstance(currentAccount).loadMedia(dialogId, 50, lastMaxId, 0, 0, 0, 1, 0, 0, null, null);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.mediaDidLoad) {
            long uid = (Long) args[0];
            if (uid == dialogId) {
                ArrayList<MessageObject> messages = (ArrayList<MessageObject>) args[2];
                boolean fromCache = (Boolean) args[3];
                int type = (Integer) args[5];

                if (messages.isEmpty()) {
                    hasMore = false;
                    updateNotification("Finished. " + downloadedFiles + " files downloaded.");
                    stopSelf();
                    return;
                }

                for (MessageObject msgObj : messages) {
                    lastMaxId = Math.min(lastMaxId == 0 ? msgObj.getId() : lastMaxId, msgObj.getId());
                    processMessage(msgObj);
                }

                if (messages.size() < 50) {
                   hasMore = false;
                   updateNotification("Finished. " + downloadedFiles + " files downloaded.");
                   stopSelf();
                } else {
                   loadNextBatch();
                }
            }
        }
    }

    private void processMessage(MessageObject msgObj) {
        TLRPC.Message message = msgObj.messageOwner;
        if (message.media != null) {
            FileLoader.getInstance(currentAccount).loadFile(msgObj.getDocument(), msgObj.getPhoto(), null, 0, 1);
            downloadedFiles++;
            updateNotification("Downloaded " + downloadedFiles + " files...");
        }
    }

    private void updateNotification(String text) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, buildNotification(text));
    }

    private Notification buildNotification(String text) {
        Intent stopIntent = new Intent(this, JarooBarghiService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TeleTux Jaroo Barghi")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Jaroo Barghi Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
\"\"\"

with open('TMessagesProj/src/main/java/org/telegram/messenger/JarooBarghiService.java', 'w') as f:
    f.write(content)
