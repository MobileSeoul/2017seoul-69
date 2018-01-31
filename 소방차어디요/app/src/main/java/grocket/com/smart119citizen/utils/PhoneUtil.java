package grocket.com.smart119citizen.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PhoneUtil {
    /**
     * 다이얼 표시
     */
    public static void showPhoneDial(Context context, String phoneNumber) {
        String telText = "tel:@phone";
        telText = telText.replace("@phone", phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(telText));
        context.startActivity(intent);
    }
}
