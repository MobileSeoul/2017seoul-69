package grocket.com.smart119citizen.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import grocket.com.smart119citizen.MainActivity;
import grocket.com.smart119citizen.R;
import grocket.com.smart119citizen.ViewCommandPopupActivity;


public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     *
     * @param from SenderID 값을 받아온다.
     * @param data Set형태로 GCM으로 받은 데이터 payload이다.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString("TYPE");
        String commandType = data.getString("CommandType");
        String AccidentNo = data.getString("AccidentNo");
        String AccidentAddress = data.getString("AccidentAddress");
        String DetailAddress = data.getString("DetailAddress");
        String AccidentContent = data.getString("AccidentContent");
        String ReporterTelephone = data.getString("ReporterTelephone");
        String RegDate = data.getString("RegDate");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "TYPE : " + type);
        Log.d(TAG, "CommandType : " + commandType);
        Log.d(TAG, "AccidentNo: " + AccidentNo);
        Log.d(TAG, "AccidentAddress: " + AccidentAddress);
        Log.d(TAG, "DetailAddress: " + DetailAddress);
        Log.d(TAG, "AccidentContent: " + AccidentContent);
        Log.d(TAG, "ReporterTelephone: " + ReporterTelephone);
        Log.d(TAG, "RegDate: " + RegDate);

        // 소방차 어디는 푸시 사용안함
//        int CommandType = 0;
//        try { CommandType = Integer.parseInt(commandType); }
//        catch (NumberFormatException ex) {
//            CommandType = 0;
//        }

//        // popup
//        Intent popupIntent = new Intent(getApplicationContext(), ViewCommandPopupActivity.class);
//        popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        popupIntent.putExtra("CommandType", CommandType);
//        popupIntent.putExtra("AccidentNo", AccidentNo);
//        popupIntent.putExtra("AccidentAddress", AccidentAddress);
//        popupIntent.putExtra("DetailAddress", DetailAddress);
//        popupIntent.putExtra("AccidentContent", AccidentContent);
//        popupIntent.putExtra("ReporterTelephone", ReporterTelephone);
//        popupIntent.putExtra("RegDate", RegDate);
//        getApplicationContext().startActivity(popupIntent);

        // GCM으로 받은 메세지를 디바이스에 알려주는 sendNotification()을 호출한다.
//        sendNotification(type, CommandType, AccidentContent);
    }


    /**
     * 실제 디바에스에 GCM으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * @param type
     * @param message
     */
    private void sendNotification(String type, int commandType, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(commandType == 0) {
            // 0 - “화재출동"
            defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fire);
        } else if(commandType == 1) {
            // 1 - “구조출동"
            defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.rescue);
        } else if(commandType == 2) {
            // 2 - “구급출동"
            defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.emergency);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentTitle(message)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
