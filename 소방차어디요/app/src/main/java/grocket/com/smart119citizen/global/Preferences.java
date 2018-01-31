package grocket.com.smart119citizen.global;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String PREF_CONFIG = "APP_PREF";
    private static final String PREF_LOGIN_TYPE = "LOGIN_TYPE";
    private static final String PREF_PUSHKEY = "PUSHKEY";
    private static final String PREF_MB_TELEPHONE = "MEMBER_TELEPHONE";
    private static final String PREF_MB_NAME = "MEMBER_NAME";
    private static final String PREF_MB_ID = "MEMBER_ID";
    private static final String PREF_MB_PASSWORD = "MEMBER_PASSWORD";

    private static final String PREF_SELC_CAT_PK = "SELEC_CAT_PK";
    private static final String PREF_SELC_CAT_NAME = "SELEC_CAT_NAEM";
    private static final String PREF_SELC_LOC_CODE = "SELEC_LOC_CODE";
    private static final String PREF_SELC_LOC_NAME = "SELEC_LOC_NAME";
    private static final String PREF_SELC_DIS_CODE = "SELEC_DIS_CODE";
    private static final String PREF_SELC_DIS_NAME = "SELEC_DIS_NAME";

    private static final String PREF_POPUP_PROPOSEEVENT = "POPUP_PROPOSEEVENT";

    private Context mContext;
    private SharedPreferences pref = null;
    private static Preferences _obj = null;

    private Preferences(Context context) {
        mContext = context;
        pref = context.getSharedPreferences(PREF_CONFIG, 0);
    }

    /**
     * App Preference 객체 반환
     * @param context
     * @return
     */
    public static Preferences getInstance(Context context) {
        if(_obj == null) {
            _obj = new Preferences(context);
        }

        return _obj;
    }

    /**
     * App Preference 메모리 해제
     */
    public static void destory() {
        _obj = null;
        System.gc();
    }

    public String getID() {
        String memberPk = pref.getString(PREF_MB_ID, "");
        return memberPk;
    }

    public void setID(String id) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_MB_ID, id);
        edit.apply();
    }

    public void setPassword(String password) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_MB_PASSWORD, password);
        edit.apply();
    }

    public String getPassword() {
        String password = pref.getString(PREF_MB_PASSWORD, "");
        return password;
    }

    public String getMemberName() {
        String memberName = pref.getString(PREF_MB_NAME, "");
        return memberName;
    }

    public void setMemberName(String memberName) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_MB_NAME, memberName);
        edit.apply();
    }

    public String getMemberTelephone() {
        String memberTelephone = pref.getString(PREF_MB_TELEPHONE, "");
        return memberTelephone;
    }

    public void setMemberTelephone(String memberTelephone) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_MB_TELEPHONE, memberTelephone);
        edit.apply();
    }

    public void setPushKey(String pushKey) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_PUSHKEY, pushKey);
        edit.apply();
    }

    public String getPushKey() {
        String pushkey = pref.getString(PREF_PUSHKEY, "");
        return pushkey;
    }

    ///////////////////////////////////////////

    public void setGoodsCategoryPk(String categoryPk) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_SELC_CAT_PK, categoryPk);
        edit.apply();
    }

    public String getGoodsCategoryPk() {
        return pref.getString(PREF_SELC_CAT_PK, "");
    }

    public void setGoodsCategoryName(String categoryName) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_SELC_CAT_NAME, categoryName);
        edit.apply();
    }

    public String getGoodsCategoryName() {
        return pref.getString(PREF_SELC_CAT_NAME, "");
    }

    public void setGoodsLocationCode(String locationCode) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_SELC_LOC_CODE, locationCode);
        edit.apply();
    }

    public String getGoodsLocationCode() {
        return pref.getString(PREF_SELC_LOC_CODE, "");
    }

    public void setGoodsLocationName(String locationName) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_SELC_LOC_NAME, locationName);
        edit.apply();
    }

    public String getGoodsLocationName() {
        return pref.getString(PREF_SELC_LOC_NAME, "");
    }

    public void setDistrictCode(String districtCode) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_SELC_DIS_CODE, districtCode);
        edit.apply();
    }

    public String getDistrictCode() {
        return pref.getString(PREF_SELC_DIS_CODE, "");
    }

    public void setDistrictName(String districtName) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_SELC_DIS_NAME, districtName);
        edit.apply();
    }

    public String getDistrictName() {
        return pref.getString(PREF_SELC_DIS_NAME, "");
    }


    public void setProposeEventDate(String date) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PREF_POPUP_PROPOSEEVENT, date);
        edit.apply();
    }

    public String getProposeEventDate() {
        return pref.getString(PREF_POPUP_PROPOSEEVENT, "");
    }

}
