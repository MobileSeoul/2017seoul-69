package grocket.com.smart119citizen;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import grocket.com.smart119citizen.global.ApiUrl;
import grocket.com.smart119citizen.global.Preferences;
import grocket.com.smart119citizen.utils.MyToolbar;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class ChangePasswordActivity extends CommonCompatActivity implements View.OnClickListener{

    final int REQ_CHANGE_PASSWORD = 1000;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.btnChangePassword)
    ImageButton mBtnChangePassword;
    @InjectView(R.id.edtPassword)
    EditText mEdtPassword;
    @InjectView(R.id.edtPasswordConfirm)
    EditText mEdtPasswordConfirm;

    private MyToolbar mMyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.inject(this);

        init();
    }

    @Override
    protected void init() {
        super.init();

        mMyToolbar = new MyToolbar(this, mToolbar);
        mMyToolbar.setToolbarWithBackKey(getString(R.string.title_change_password));

        mBtnChangePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mBtnChangePassword) {
            changePassword();
        }
    }

    private void changePassword() {
        final String password = mEdtPassword.getText().toString().trim();
        final String passwordConfirm = mEdtPasswordConfirm.getText().toString().trim();

        if(TextUtils.isEmpty(password)) {
            SimpleToast.show(this, "비밀번호를 입력하셔야 합니다.");
            mEdtPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            SimpleToast.show(this, "비밀번호를 6자리 이상 입력하셔야 합니다.");
            mEdtPassword.requestFocus();
            return;
        }

        if(!password.equals(passwordConfirm)) {
            SimpleToast.show(this, "비밀번호가 맞지 않습니다.");
            mEdtPasswordConfirm.requestFocus();
            return;
        }

        Preferences pref = Preferences.getInstance(this);
        final String myID = pref.getID();

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.CHANGE_PASSWORD);
        params.setRequestCode(REQ_CHANGE_PASSWORD);
        params.addParameter("ID", myID);
        params.addParameter("Password", password);
        requestHttpConnect(params);
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);
        if(requestCode == REQ_CHANGE_PASSWORD) {
            SimpleToast.show(this, "비밀번호가 변경되었습니다.");
            this.finish();
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
    }
}
