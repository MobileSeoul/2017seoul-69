package grocket.com.smart119citizen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import grocket.com.smart119citizen.utils.PhoneUtil;
import weisure.com.keipacklib.view.SimpleToast;

public class ViewCommandPopupActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.txtMessage)
    TextView mTxtMessage;
    @InjectView(R.id.btnCalling)
    ImageButton mBtnCalling;
    @InjectView(R.id.btnLocInfo)
    ImageButton mBtnLocInfo;
    @InjectView(R.id.imgAlarmType)
    ImageView mImgAlarmType;
    @InjectView(R.id.txtTitle)
    TextView mTxtTitle;
    @InjectView(R.id.txtAccidentNo)
    TextView mTxtAccidentNo;

    private int mCommandType;
    private String mAccidentNo;
    private String mAccidentAddress;
    private String mDetailAddress;
    private String mAccidentContent;
    private String mReporterTelephone;
    private String mRegDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_command_popup);
        ButterKnife.inject(this);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Intent intent = getIntent();
        Bundle argsBundle = intent.getExtras();
        if (argsBundle != null) {
            mCommandType = argsBundle.getInt("CommandType");
            mAccidentNo = argsBundle.getString("AccidentNo");
            mAccidentAddress = argsBundle.getString("AccidentAddress");
            mDetailAddress = argsBundle.getString("DetailAddress");
            mAccidentContent = argsBundle.getString("AccidentContent");
            mReporterTelephone = argsBundle.getString("ReporterTelephone");
            mRegDate = argsBundle.getString("RegDate");

            // 0-화재출동  1-구조출동  2-구급출동
            if (mCommandType == 0) {
                mImgAlarmType.setBackgroundResource(R.drawable.ico_pop_fire);
                mBtnCalling.setBackgroundResource(R.drawable.btn_popup_call_red);
                mTxtTitle.setTextColor(Color.parseColor("#ff6147"));
                mTxtAccidentNo.setTextColor(Color.parseColor("#9e5448"));
                mTxtTitle.setText("화재출동");
            } else if (mCommandType == 1) {
                mImgAlarmType.setBackgroundResource(R.drawable.ico_pop_rescue);
                mBtnCalling.setBackgroundResource(R.drawable.btn_popup_call_yellow);
                mTxtTitle.setTextColor(Color.parseColor("#ffc64d"));
                mTxtAccidentNo.setTextColor(Color.parseColor("#be9a4d"));
                mTxtTitle.setText("구조출동");
            } else if (mCommandType == 2) {
                mImgAlarmType.setBackgroundResource(R.drawable.ico_pop_emergency);
                mBtnCalling.setBackgroundResource(R.drawable.btn_popup_call_green);
                mTxtTitle.setTextColor(Color.parseColor("#5ae49e"));
                mTxtAccidentNo.setTextColor(Color.parseColor("#47a374"));
                mTxtTitle.setText("구급출동");
            }

            mTxtAccidentNo.setText("재해번호 : " + mAccidentNo);
            String message =
                    mAccidentAddress + "\n" +
                    mDetailAddress + "\n" +
                    mAccidentContent + "\n\n" +
                    "신고자 전화번호 : " + mReporterTelephone + "\n" +
                    "신고 일시 : " + mRegDate;

            mTxtMessage.setText(message);
        }

        mBtnCalling.setOnClickListener(this);
        mBtnLocInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnCalling) {
            if (!TextUtils.isEmpty(mReporterTelephone)) {
                PhoneUtil.showPhoneDial(this, mReporterTelephone);
            } else {
                SimpleToast.show(this, "신고자 전화번호가 없습니다.");
            }
        } else if (v == mBtnLocInfo) {
            Intent i = new Intent(this, MobilizeLocActivity.class);
            i.putExtra("CommandType", mCommandType);
            i.putExtra("AccidentNo", mAccidentNo);
            i.putExtra("AccidentAddr", mAccidentAddress);
            i.putExtra("AccidentAddrDetail", mDetailAddress);
            i.putExtra("AccidentContent", mAccidentContent);
            i.putExtra("ReporterTel", mReporterTelephone);
            i.putExtra("RegDate", mRegDate);
            startActivity(i);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
