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

public class InsertContactActivity extends CommonCompatActivity implements View.OnClickListener {

    final int REQ_INSERT_CONTACT_ITEM = 1000;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.edtName)
    EditText mEdtName;
    @InjectView(R.id.edtClass)
    EditText mEdtClass;
    @InjectView(R.id.edtTelephone)
    EditText mEdtTelephone;
    @InjectView(R.id.btnInsertTelephone)
    ImageButton mBtnInsertTelephone;

    private MyToolbar mMyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_contact);
        ButterKnife.inject(this);

        init();
    }

    @Override
    protected void init() {
        super.init();

        mMyToolbar = new MyToolbar(this, mToolbar);
        mMyToolbar.setToolbarWithBackKey(getString(R.string.title_contact_list));

        mBtnInsertTelephone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mBtnInsertTelephone) {
            final String name = mEdtName.getText().toString().trim();
            final String position = mEdtClass.getText().toString().trim();
            final String telephone = mEdtTelephone.getText().toString().trim();

            if(TextUtils.isEmpty(name)) {
                SimpleToast.show(this, "이름을 입력하셔야 합니다.");
                return;
            }
            if(TextUtils.isEmpty(position)) {
                SimpleToast.show(this, "직책을 입력하셔야 합니다.");
                return;
            }
            if(TextUtils.isEmpty(telephone)) {
                SimpleToast.show(this, "전화번호를 입력하셔야 합니다.");
                return;
            }

            Preferences pref = Preferences.getInstance(this);
            final String myID = pref.getID();

            OvHttpRequestParameters params = new OvHttpRequestParameters();
            params.setUrl(ApiUrl.INSERT_CONTACT_ITEM);
            params.setRequestCode(REQ_INSERT_CONTACT_ITEM);
            params.addParameter("ID", myID);
            params.addParameter("Name", name);
            params.addParameter("Position", position);
            params.addParameter("Telephone", telephone);
            requestHttpConnect(params);
        }
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);

        if(requestCode == REQ_INSERT_CONTACT_ITEM) {
            SimpleToast.show(this, "연락처가 등록되었습니다.");
            setResult(RESULT_OK);
            finish();
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
