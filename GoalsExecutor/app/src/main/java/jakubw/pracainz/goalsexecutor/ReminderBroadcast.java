package jakubw.pracainz.goalsexecutor;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class ReminderBroadcast extends BroadcastReceiver {

    String desc ="null";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.hasExtra("desc"))
            desc = intent.getStringExtra("desc");
        Log.d("BroadcastReceiver", desc);
        //call when i tap on notification
        Intent intent2 = new Intent(context, MainActivity.class);
        int requestcode = new Random().nextInt();
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestcode, intent2, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyGoalsExecutor")
                .setSmallIcon(R.drawable.ic_app_name_transparent)
                .setContentTitle("Daily Tasks! Do it!")
                .setContentText("Masz zrobic zadanko: " + desc)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(requestcode, builder.build());
    }
}
