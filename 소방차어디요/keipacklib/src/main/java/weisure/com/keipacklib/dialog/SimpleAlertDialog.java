package weisure.com.keipacklib.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by chokyounglae on 15. 2. 6..
 */
public class SimpleAlertDialog {
    public static void show(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false).create().show();
    }

    public static void show(Context context, String title, String message, DialogInterface.OnClickListener onPositiveButtonClickListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, onPositiveButtonClickListener)
                .setCancelable(false).create().show();
    }
}
