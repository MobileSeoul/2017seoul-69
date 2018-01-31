package grocket.com.smart119citizen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;
import grocket.com.smart119citizen.global.ApiUrl;
import grocket.com.smart119citizen.global.Preferences;
import grocket.com.smart119citizen.utils.PhoneUtil;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class MainActivity extends CommonCompatActivity implements View.OnClickListener{

    final int REQ_GET_LASTEST_COMMAND = 1000;

    @InjectView(R.id.txtLastedOrderAlarm)
    TextView mTxtLastedOrderAlarm;
    @InjectView(R.id.imgMoreIcon)
    ImageView mImgMoreIcon;
    @InjectView(R.id.btnCall112)
    ImageButton mBtnCall112;
    @InjectView(R.id.btnCall119)
    ImageButton mBtnCall119;
    @InjectView(R.id.btnEmergencyCall)
    ImageButton mBtnEmergencyCall;
    @InjectView(R.id.btnWeatherInfo)
    ImageButton mBtnWeatherInfo;
    @InjectView(R.id.btnViewSmartOrderList)
    ImageButton mBtnViewSmartOrderList;
    @InjectView(R.id.btnLocService)
    ImageButton mBtnLocService;
    @InjectView(R.id.btnSetting)
    ImageButton mBtnSetting;

    private String mLastestCommandType;
    private String mLastestAccidentNo;
    private String mLastestAccidentAddress;
    private String mLastestDetailAddress;
    private String mLastestAccidentContent;
    private String mLastestReporterTelephone;
    private String mLastestRegDate;

    private boolean mIsPressBackKey = false;

    /**
     * Back key 처리 EventHandler
     */
    static class BackkeyProcHandler extends Handler {
        private final WeakReference<MainActivity> mMainFrameActivity;

        public BackkeyProcHandler(MainActivity activity) {
            mMainFrameActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity act = mMainFrameActivity.get();
            if (act == null) return;

            switch (msg.what) {
                case 0:
                    // 2초가 지나면 다시 Falg 를 false로 바꾼다.
                    act.mIsPressBackKey = false;
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        init();
    }

    @Override
    protected void init() {
        super.init();

        mTxtLastedOrderAlarm.setOnClickListener(this);
        mImgMoreIcon.setOnClickListener(this);

        mBtnCall112.setOnClickListener(this);
        mBtnCall119.setOnClickListener(this);
        mBtnEmergencyCall.setOnClickListener(this);
        mBtnWeatherInfo.setOnClickListener(this);
        mBtnViewSmartOrderList.setOnClickListener(this);
        mBtnLocService.setOnClickListener(this);
        mBtnSetting.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 최근 지령 불러오기
        getLastestCommand();
    }

    @Override
    public void onClick(View v) {
        if(v == mBtnViewSmartOrderList) {
            // 스마트 지령
            showActivity(CommandListActivity.class);
        }
        else if (v == mTxtLastedOrderAlarm || v == mImgMoreIcon) {
            // 최근 알람 상태 보여주기
            Intent popupIntent = new Intent(this, ViewCommandPopupActivity.class);
            popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            popupIntent.putExtra("CommandType", mLastestCommandType);
            popupIntent.putExtra("AccidentNo", mLastestAccidentNo);
            popupIntent.putExtra("AccidentAddress", mLastestAccidentAddress);
            popupIntent.putExtra("DetailAddress", mLastestDetailAddress);
            popupIntent.putExtra("AccidentContent", mLastestAccidentContent);
            popupIntent.putExtra("ReporterTelephone", mLastestReporterTelephone);
            popupIntent.putExtra("RegDate", mLastestRegDate);
            startActivity(popupIntent);
        }
        else if (v == mBtnCall112) {
            // 긴급전화 112
            PhoneUtil.showPhoneDial(this, "112");
        }
        else if (v == mBtnCall119) {
            // 긴급전화 119
            PhoneUtil.showPhoneDial(this, "119");
        }
        else if (v == mBtnLocService) {
            // 출동 위치 서비스
            showActivity(LocationListActivity.class);
        }
        else if (v == mBtnEmergencyCall) {
            // 긴급 연락처 목록
            showActivity(ContactListActivity.class);
        }
        else if (v == mBtnSetting) {
            // 환경설정
            showActivity(MySettingActivity.class);
        }
        else if (v == mBtnWeatherInfo) {
            // 날씨정보
            final String url ="http://m.kma.go.kr/m/forecast/forecast_01.jsp";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }

    private void getLastestCommand()
    {
        Preferences pref = Preferences.getInstance(this);
        final String myID = pref.getID();
        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.GET_LASTEST_COMMAND);
        params.setRequestCode(REQ_GET_LASTEST_COMMAND);
        params.addParameter("ID", myID);
        requestHttpConnect(params);
    }

    @Override
    public void onBackPressed() {
        if (!mIsPressBackKey) {
            SimpleToast.show(
                    this,
                    getString(R.string.main_msg_confirm_exit));
            mIsPressBackKey = true;
            new BackkeyProcHandler(this).sendEmptyMessageDelayed(0, 1000 * 2);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);

        if(requestCode == REQ_GET_LASTEST_COMMAND) {
            String command = json_result.getString("AccidentContent");
            command = command.replace("\n", "");

            int max_len = 15;
            if(command.length() > max_len) command = command.substring(0, max_len) + "...";

            mLastestCommandType = json_result.getString("CommandType");
            mLastestAccidentNo = json_result.getString("AccidentNo");
            mLastestAccidentAddress = json_result.getString("AccidentAddress");
            mLastestDetailAddress = json_result.getString("DetailAddress");
            mLastestAccidentContent = json_result.getString("AccidentContent");
            mLastestReporterTelephone = json_result.getString("ReporterTelephone");
            mLastestRegDate = json_result.getString("RegDate");

            mTxtLastedOrderAlarm.setText(command);
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
