package jakubw.pracainz.goalsexecutor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String desc = intent.getStringExtra("desc");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifyGoalsExecutor")
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("Daily Tasks! Do it!")
                .setContentText("Masz zrobic zadanko: " + desc)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200,builder.build());
    }
}
