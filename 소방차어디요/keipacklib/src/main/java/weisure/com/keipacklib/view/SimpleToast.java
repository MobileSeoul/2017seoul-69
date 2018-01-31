package weisure.com.keipacklib.view;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chokyounglae on 15. 2. 9..
 */
public class SimpleToast {
    public static void show(Context context, String message) {
        try {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void show(Context context, int resId) {
        try {
            String message = context.getString(resId);
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void show(Context context, String message, int duration)
    {
        try {
            Toast toast = Toast.makeText(context, message, duration);
            //toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void show(Context context, String message, int gravity, int duration)
    {
        try {
            Toast toast = Toast.makeText(context, message, duration);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
    }
}
