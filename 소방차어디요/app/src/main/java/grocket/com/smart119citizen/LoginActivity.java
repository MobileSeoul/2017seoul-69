package grocket.com.smart119citizen;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import grocket.com.smart119citizen.global.ApiUrl;
import grocket.com.smart119citizen.global.Preferences;
import grocket.com.smart119citizen.utils.CheckInvalidUtil;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class LoginActivity extends CommonCompatActivity implements View.OnClickListener {

    final int REQ_LOGIN = 1000;

    @InjectView(R.id.edtID)
    EditText mEdtID;
    @InjectView(R.id.edtPassword)
    EditText mEdtPassword;
    @InjectView(R.id.btnLogin)
    ImageButton mBtnLogin;
    @InjectView(R.id.imgJoin)
    ImageView mImgJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        init();
    }

    @Override
    protected void init() {
        super.init();

        mImgJoin.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnLogin) {
            attemptLogin();
        } else if (v == mImgJoin) {
            attemptJoin();
        }
    }

    /**
     * 회원가입
     */
    private void attemptJoin() {
        pushThisActivityIntoBackStack();
        showActivity(TermsActivity.class);
    }

    /**
     * 로그인
     */
    private void attemptLogin() {
        // 유효성 검사
        CheckInvalidUtil checkInvalidUtil = new CheckInvalidUtil();
        CheckInvalidUtil.CheckInvalidData result =
                checkInvalidUtil.checkLoginInvalid(
                        this,
                        mEdtID, mEdtPassword);
        if (!result.isInvalidResult()) {
            SimpleToast.show(this, result.getErrorMessage());
            if (result.getFocusView() != null)
                result.getFocusView().requestFocus();
            return;
        }

        Preferences pref = Preferences.getInstance(this);
        final String pushKey = pref.getPushKey();
        final String id = mEdtID.getText().toString().trim();
        final String password = mEdtPassword.getText().toString().trim();

        // 로그인 시도
        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.LOGIN);
        params.setRequestCode(REQ_LOGIN);
        params.addParameter("ID", id);
        params.addParameter("Password", password);
        params.addParameter("PushKey", pushKey);
        params.addParameter("OS", "A");
        requestHttpConnect(params);
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);
        if (requestCode == REQ_LOGIN) {
            final String id = mEdtID.getText().toString().trim();
            final String password = mEdtPassword.getText().toString().trim();

            final String IsApprovalTxt = json_result.getString("IsApproval");
            if (IsApprovalTxt.equals("1")) {
                // 승인된 회원
                Preferences pref = Preferences.getInstance(this);
                pref.setID(id);
                pref.setPassword(password);
                showActivity(MainActivity.class, true);
            } else {
                // 승인이 안된 회원
                SimpleToast.show(this, R.string.login_msg_not_approval);
            }
        }
    }

    @Override
    public void onHttpFailure(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpFailure(requestCode, json_result);
        SimpleToast.show(this, json_result.getString("fail_cause"));
    }

    @Override
    public void onHttpError(String error_message) {
        super.onHttpError(error_message);
        SimpleToast.show(this, error_message);
    }
}
