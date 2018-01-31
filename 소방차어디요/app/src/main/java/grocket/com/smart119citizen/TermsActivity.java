package grocket.com.smart119citizen;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import grocket.com.smart119citizen.global.ApiUrl;
import grocket.com.smart119citizen.utils.MyToolbar;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class TermsActivity extends CommonCompatActivity implements View.OnClickListener {

    final int REQ_GET_TERM = 1000;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.btnAllAgree)
    ImageButton mBtnAllAgree;
    @InjectView(R.id.txtTerms01)
    TextView mTxtTerms01;
    @InjectView(R.id.cbTerms01)
    CheckBox mCbTerms01;
    @InjectView(R.id.etTerms01)
    EditText mEtTerms01;
    @InjectView(R.id.txtTerms02)
    TextView mTxtTerms02;
    @InjectView(R.id.cbTerms02)
    CheckBox mCbTerms02;
    @InjectView(R.id.etTerms02)
    EditText mEtTerms02;

    private MyToolbar mMyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        ButterKnife.inject(this);

        init();
    }

    @Override
    protected void init() {
        super.init();

        mMyToolbar = new MyToolbar(this, mToolbar);
        mMyToolbar.setToolbarWithBackKey(getString(R.string.title_terms));
        mBtnAllAgree.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTermInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        popActivityFromBackStack();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnAllAgree) {
            if (mCbTerms01.isChecked() && mCbTerms02.isChecked()) {
                pushThisActivityIntoBackStack();
                showActivity(JoinActivity.class);
            } else {
                SimpleToast.show(this, R.string.terms_msg_youhavetoagree);
            }
        }
    }

    /**
     * 이용약관 불러오기
     */
    private void getTermInfo() {
        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.GET_TERMS);
        params.setRequestCode(REQ_GET_TERM);
        params.addParameter("OS", "A");
        requestHttpConnect(params);
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);

        if (requestCode == REQ_GET_TERM) {
            final String TermsAndConditions = json_result.getString("TermsAndConditions");
            final String PrivacyPolicy = json_result.getString("PrivacyPolicy");

            mEtTerms01.setText(TermsAndConditions);
            mEtTerms02.setText(PrivacyPolicy);
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
