package me.nillerusr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.valvesoftware.source.R;

/* loaded from: classes.dex */
public class UpdateService extends Service {
    static boolean service_work = false;
    Bundle extras;
    NotificationManager nm;

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.nm = (NotificationManager) getSystemService("notification");
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!service_work) {
            service_work = true;
            try {
                this.extras = intent.getExtras();
                sendNotif();
                return 2;
            } catch (Exception e) {
                return 2;
            }
        }
        return 2;
    }

    private void sendNotif() {
        Notification notif = new Notification(R.drawable.rn_logo, "Update avalible", System.currentTimeMillis());
        notif.contentView = new RemoteViews(getPackageName(), R.layout.update_notify);
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(this.extras.get("update_url").toString()));
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, browserIntent, 0);
        notif.contentIntent = pIntent;
        notif.flags |= 16;
        notif.defaults |= 4;
        notif.defaults |= 2;
        notif.defaults |= 1;
        notif.priority |= 1;
        this.nm.notify(1, notif);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
