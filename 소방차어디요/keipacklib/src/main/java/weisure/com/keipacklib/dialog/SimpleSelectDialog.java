package weisure.com.keipacklib.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by chokyounglae on 15. 2. 6..
 */
public class SimpleSelectDialog {
    public static void show(
            Context context,
            String title,
            String[] items,
            DialogInterface.OnClickListener onClickListener) {

        final String btn_text_close = "닫기";

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, onClickListener)
                .setNeutralButton(btn_text_close, null)
                .show();
    }
}
