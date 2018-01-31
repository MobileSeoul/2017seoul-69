package grocket.com.smart119citizen;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import grocket.com.smart119citizen.gcm.RegistrationIntentService;
import grocket.com.smart119citizen.global.ApiUrl;
import grocket.com.smart119citizen.global.Preferences;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class IntroActivity extends CommonCompatActivity {
    static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    final int DELAY_TIME = 3000;
    final int REQ_GET_VERSION = 1000;
    final int REQ_AUTO_LOGIN = 1001;

    int mCurrentVersionCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        init();
    }

    @Override
    protected void init() {
        super.init();

        // 현재 버전 가져오기
        PackageManager pm = this.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            String version = packageInfo.versionName;        // 버전 네임
            mCurrentVersionCode = packageInfo.versionCode;    // 버전 코드

            debugMessage("현재 버전  : " + version);
            debugMessage("현재 버전 코드 : " + mCurrentVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // GCM Instance key 불러오기(device key)
        getInstanceIdToken();

        // 버전 체크
        checkVersion();
    }

    private Runnable r = new Runnable() {
        public void run() {

            // 로그인 정보 불러오기
            Preferences pref = Preferences.getInstance(IntroActivity.this);
            final String id = pref.getID();
            final String password = pref.getPassword();
            final String pushKey = pref.getPushKey();

            if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
                // 기본 로그인 정보 삭제
                pref.setID("");
                pref.setPassword("");
                showActivity(LoginActivity.class, true);
            } else {
                // 자동 로그인
                OvHttpRequestParameters params = new OvHttpRequestParameters();
                params.setUrl(ApiUrl.LOGIN);
                params.setRequestCode(REQ_AUTO_LOGIN);
                params.addParameter("ID", id);
                params.addParameter("Password", password);
                params.addParameter("PushKey", pushKey);
                params.addParameter("OS", "A");
                requestHttpConnect(params);
            }

        }
    };

    private void checkVersion() {
        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.VERSION);
        params.setRequestCode(REQ_GET_VERSION);
        params.addParameter("OS", "A");
        if (!requestHttpConnect(params)) {
            final String title = getResources().getString(R.string.alert_warning);
            final String msg = getResources().getString(R.string.msg_network_not_good);
            alert(title, msg);
            this.finish();
        }
    }

    private void checkLastestVersion(int recvVersionCode) {
        // 최신버전 검사
        if (mCurrentVersionCode >= recvVersionCode) {
            new Handler().postDelayed(r, DELAY_TIME);
        } else {
            // 최신 버전 업데이트
            final String title = getResources().getString(R.string.intro_alert_checkversion_title);
            final String message = getResources().getString(R.string.intro_alert_checkversion_message);
            final String dlg_yes = getResources().getString(R.string.dlg_yes);

            new AlertDialog.Builder(IntroActivity.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(dlg_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            } finally {
                                IntroActivity.this.finish();
                            }
                        }
                    }).show();
        }
    }

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }else{
            debugMessage("checkPlayServices failed.");
        }
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);

        if (requestCode == REQ_GET_VERSION) {
            // 최신버전 검사
            debugMessage("서버등록 버전 코드 : " + json_result.getString("VersionCode"));
            String versionCodeTxt = json_result.getString("VersionCode");
            int versionCode = 0;
            try {
                versionCode = Integer.parseInt(versionCodeTxt);
            } catch (NumberFormatException e) {
            }

            checkLastestVersion(versionCode);
        } else if (requestCode == REQ_AUTO_LOGIN) {
            final String IsApprovalTxt = json_result.getString("IsApproval");
            if(IsApprovalTxt.equals("1")) {
                // 승인된 회원일 경우 자동로그인 완료
                showActivity(MainActivity.class, true);
            }else{
                // 아직 승인이 안된 회원
                SimpleToast.show(this, R.string.login_msg_not_approval);
            }
        }
    }

    @Override
    public void onHttpFailure(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpFailure(requestCode, json_result);

        SimpleToast.show(this, json_result.getString("fail_cause"));

        if(requestCode == REQ_AUTO_LOGIN) {
            Preferences pref = Preferences.getInstance(this);
            pref.setID("");
            pref.setPassword("");
            pref.setMemberName("");
            pref.setMemberTelephone("");
            showActivity(LoginActivity.class, true);
        }
    }

    @Override
    public void onHttpError(String error_message) {
        super.onHttpError(error_message);

        debugMessage(error_message);

        SimpleToast.show(this, error_message);
        finish();
    }
}
