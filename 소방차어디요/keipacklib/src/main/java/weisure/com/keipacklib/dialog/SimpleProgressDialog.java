package weisure.com.keipacklib.dialog;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by chokyounglae on 15. 2. 6..
 */
public class SimpleProgressDialog {
    private final static String TAG = "CustomProgressDialog";
    private static ProgressDialog progressDlg = null;

    public static void show(Context context) {
        if(progressDlg==null) {
            final String title = "대기";
            final String message = "잠시만 기다려주세요.";

            try {
                progressDlg = ProgressDialog.show(
                        context,
                        title,
                        message,
                        true);
                progressDlg.setCancelable(true);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void dismiss() {
        if(progressDlg!=null) {
            try {
                progressDlg.dismiss();
            }catch(Exception e) {
                e.printStackTrace();
            }finally{
                progressDlg = null;
            }
        }
    }
}
