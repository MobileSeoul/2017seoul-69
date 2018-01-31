package weisure.com.keipacklib.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * Created by chokyounglae on 15. 2. 6..
 */
public class CheckPhoneNumber {
    public static boolean isValidCellPhoneNumber(String cellphoneNumber) {
        boolean returnValue = false;
        Log.i("cell", cellphoneNumber);
        String regex = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(cellphoneNumber);
        if (m.matches()) {
            returnValue = true;
        }
        return returnValue;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        boolean returnValue = false;
        String regex = "^(02|031|032|033|041|042|043|051|052|053|054|055|061|062|063|064|070)-(?:\\d{3}|\\d{4})-\\d{4}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phoneNumber);
        if (m.matches()) {
            returnValue = true;
        }
        return returnValue;
    }
}
