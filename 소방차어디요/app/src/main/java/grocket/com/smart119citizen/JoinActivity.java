package grocket.com.smart119citizen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import grocket.com.smart119citizen.data.FirestationData;
import grocket.com.smart119citizen.global.ApiUrl;
import grocket.com.smart119citizen.global.Preferences;
import grocket.com.smart119citizen.utils.CheckInvalidUtil;
import grocket.com.smart119citizen.utils.MyToolbar;
import weisure.com.keipacklib.dialog.SimpleSelectDialog;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class JoinActivity extends CommonCompatActivity implements View.OnClickListener{

    final int REQ_JOIN = 1000;
    final int REQ_AUTO_LOGIN = 1001;
    final int REQ_GET_FIRESTATIONLIST = 1002;

    private MyToolbar mMyToolbar;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.btnJoin)
    ImageButton mBtnJoin;
    @InjectView(R.id.edtID)
    EditText mEdtID;
    @InjectView(R.id.edtPassword)
    EditText mEdtPassword;
    @InjectView(R.id.edtPasswordConfirm)
    EditText mEdtPasswordConfirm;
    @InjectView(R.id.edtUserName)
    EditText mEdtUserName;
    @InjectView(R.id.txtSelectedFirestationName)
    TextView mTxtSelectedFirestationName;

    private String mSelectedFirestationPk;
    private ArrayList<FirestationData> mFirestationDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        ButterKnife.inject(this);

        init();
    }

    @Override
    protected void init() {
        super.init();

        mSelectedFirestationPk = "";
        mFirestationDataList = new ArrayList<>();

        mMyToolbar = new MyToolbar(this, mToolbar);
        mMyToolbar.setToolbarWithBackKey(getString(R.string.title_join));
        mBtnJoin.setOnClickListener(this);
        mTxtSelectedFirestationName.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFirestationList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        popActivityFromBackStack();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnJoin) {
            attemptJoin();
        }
        else if(v == mTxtSelectedFirestationName) {
            String[] items = new String[mFirestationDataList.size()];
            for(int i=0;i<mFirestationDataList.size();i++) {
                FirestationData data = mFirestationDataList.get(i);
                items[i] = data.getFireStationName();
            }

            SimpleSelectDialog.show(
                    this,
                    "근무 소방서 선택",
                    items,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirestationData data = mFirestationDataList.get(which);
                            mSelectedFirestationPk = data.getFireStationPK();
                            mTxtSelectedFirestationName.setText(data.getFireStationName());
                        }
                    });
        }
    }

    /**
     * 근무지 소방서 목록 불러오기
     */
    private  void getFirestationList() {
        Preferences pref = Preferences.getInstance(this);
        final String myID = pref.getID();

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.GET_FIRESTATION_LIST);
        params.setRequestCode(REQ_GET_FIRESTATIONLIST);
        params.addParameter("ID", myID);
        requestHttpConnect(params);
    }

    /**
     * 회원가입 요청
     */
    private void attemptJoin() {
        // 회원가입 정보 유효성 체크
        CheckInvalidUtil checkInvalidUtil = new CheckInvalidUtil();
        CheckInvalidUtil.CheckInvalidData result =
                checkInvalidUtil.checkJoinInvalid(
                        this,
                        mEdtID, mEdtPassword, mEdtPasswordConfirm, mEdtUserName, mSelectedFirestationPk);
        if (!result.isInvalidResult()) {
            SimpleToast.show(this, result.getErrorMessage());
            if (result.getFocusView() != null)
                result.getFocusView().requestFocus();
            return;
        }

        // 회원가입 처리 요청
        final String id = mEdtID.getText().toString().trim();
        final String password = mEdtPassword.getText().toString().trim();
        final String name = mEdtUserName.getText().toString().trim();

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.JOIN);
        params.setRequestCode(REQ_JOIN);
        params.addParameter("ID", id);
        params.addParameter("Password", password);
        params.addParameter("UserName", name);
        params.addParameter("FirestationPk", mSelectedFirestationPk);
        requestHttpConnect(params);
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);

        if(requestCode == REQ_GET_FIRESTATIONLIST) {
            mFirestationDataList.clear();
            String dataResult = json_result.getString("data");
            JSONArray ja = new JSONArray(dataResult);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                FirestationData item = new FirestationData();
                item.setFireStationPK(jo.getString("Primarykey"));
                item.setFireStationName(jo.getString("FirestationName"));
                mFirestationDataList.add(item);
            }
        }
        else if (requestCode == REQ_JOIN) {
            SimpleToast.show(this, R.string.join_msg_joinokay);

            // 자동 로그인 요청
            debugMessage("자동 로그인 시도");
            final String id = mEdtID.getText().toString().trim();
            final String password = mEdtPassword.getText().toString().trim();

            Preferences pref = Preferences.getInstance(this);
            final String pushKey = pref.getPushKey();
            debugMessage("push key : " + pushKey);

            OvHttpRequestParameters params = new OvHttpRequestParameters();
            params.setUrl(ApiUrl.LOGIN);
            params.setRequestCode(REQ_AUTO_LOGIN);
            params.addParameter("ID", id);
            params.addParameter("Password", password);
            params.addParameter("PushKey", pushKey);
            params.addParameter("OS", "A");
            requestHttpConnect(params);
        } else if (requestCode == REQ_AUTO_LOGIN) {
            final String id = mEdtID.getText().toString().trim();
            final String password = mEdtPassword.getText().toString().trim();

            Preferences pref = Preferences.getInstance(this);
            pref.setID(id);
            pref.setPassword(password);

            // 뒤에 쌓여있던 activity 모두 닫기
            finishAllActivityInBackStack();
            finish();

            // 자동 로그인후 메인화면으로 이동해야됨
            showActivity(MainActivity.class);
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
