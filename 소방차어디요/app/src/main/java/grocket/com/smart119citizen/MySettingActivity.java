package grocket.com.smart119citizen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
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
import grocket.com.smart119citizen.data.TeamData;
import grocket.com.smart119citizen.data.VehicleData;
import grocket.com.smart119citizen.global.ApiUrl;
import grocket.com.smart119citizen.global.Preferences;
import grocket.com.smart119citizen.utils.MyToolbar;
import weisure.com.keipacklib.dialog.SimpleSelectDialog;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class MySettingActivity extends CommonCompatActivity implements View.OnClickListener {

    final int REQ_GET_FIRESTATIONLIST = 1000;
    final int REQ_GET_TEAM_LIST = 1001;
    final int REQ_GET_VEHICLE_LIST = 1002;
    final int REQ_GET_MY_CONFIGURE = 1003;
    final int REQ_SET_MY_CONFIGURE = 1004;
    final int REQ_LOGOUT = 1005;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.btnLogout)
    ImageButton mBtnLogout;
    @InjectView(R.id.edtUserName)
    EditText mEdtUserName;
    @InjectView(R.id.txtSelectedFirestationName)
    TextView mTxtSelectedFirestationName;
    @InjectView(R.id.edtClass)
    EditText mEdtClass;
    @InjectView(R.id.txtWorkTeam)
    TextView mTxtWorkTeam;
    @InjectView(R.id.txtVehicle)
    TextView mTxtVehicle;
    @InjectView(R.id.cbFlexibleWorkplace)
    CheckBox mCbFlexibleWorkplace;
    @InjectView(R.id.cbTotalVehiclePush)
    CheckBox mCbTotalVehiclePush;
    @InjectView(R.id.btnChangePassword)
    ImageButton mBtnChangePassword;

    private MyToolbar mMyToolbar;

    private String mSelectedFirestationPk;
    private String mSelectedTeamPk;
    private String mSelectedVehiclePk;

    private ArrayList<FirestationData> mFirestationDataList;
    private ArrayList<TeamData> mTeamDataList;
    private ArrayList<VehicleData> mVehicleDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        ButterKnife.inject(this);

        init();
    }

    @Override
    protected void init() {
        super.init();

        mSelectedFirestationPk = "";
        mSelectedTeamPk = "";
        mSelectedVehiclePk = "";
        mFirestationDataList = new ArrayList<>();
        mTeamDataList = new ArrayList<>();
        mVehicleDataList = new ArrayList<>();

        mMyToolbar = new MyToolbar(this, mToolbar);
        mMyToolbar.setToolbarWithBackKey(getString(R.string.title_mysetting));

        mBtnLogout.setOnClickListener(this);
        mTxtSelectedFirestationName.setOnClickListener(this);
        mTxtWorkTeam.setOnClickListener(this);
        mTxtVehicle.setOnClickListener(this);
        mBtnChangePassword.setOnClickListener(this);

        getFirestationList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == mTxtSelectedFirestationName) {
            // 근무 소방서 선택
            String[] items = new String[mFirestationDataList.size()];
            for (int i = 0; i < mFirestationDataList.size(); i++) {
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
        else if (v == mTxtWorkTeam) {
            // 근무설정
            String[] items = new String[mTeamDataList.size()];
            for (int i = 0; i < mTeamDataList.size(); i++) {
                TeamData data = mTeamDataList.get(i);
                items[i] = data.getTeamName();
            }

            SimpleSelectDialog.show(
                    this,
                    "근무 소방서 선택",
                    items,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TeamData data = mTeamDataList.get(which);
                            mSelectedTeamPk = data.getTeamPK();
                            mTxtWorkTeam.setText(data.getTeamName());
                        }
                    });
        } else if (v == mTxtVehicle) {
            // 출동 차량
            String[] items = new String[mVehicleDataList.size()];
            for (int i = 0; i < mVehicleDataList.size(); i++) {
                VehicleData data = mVehicleDataList.get(i);
                items[i] = data.getVehicleName();
            }

            SimpleSelectDialog.show(
                    this,
                    "출동차량 선택",
                    items,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VehicleData data = mVehicleDataList.get(which);
                            mSelectedVehiclePk = data.getVehiclePK();
                            mTxtVehicle.setText(data.getVehicleName());
                        }
                    });
        } else if (v == mBtnChangePassword) {
            // 비밀번호 변경
            showActivity(ChangePasswordActivity.class);
        } else if (v == mBtnLogout) {
            // 로그아웃
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
            alert_confirm.setMessage("로그아웃 하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("로그아웃",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 'YES'
                                    logout();
                                }
                            }).setNegativeButton(getString(R.string.dlg_no),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();
        }
    }

    /**
     * 로그아웃
     */
    private void logout() {
        Preferences pref = Preferences.getInstance(this);
        final String myID = pref.getID();

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.LOGOUT);
        params.setRequestCode(REQ_LOGOUT);
        params.addParameter("ID", myID);
        requestHttpConnect(params);
    }

    /**
     * 근무지 소방서 목록 불러오기
     */
    private void getFirestationList() {
        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.GET_FIRESTATION_LIST);
        params.setRequestCode(REQ_GET_FIRESTATIONLIST);
        requestHttpConnect(params);
    }

    /**
     * 팀 목록 불러오기
     */
    private void getTeamList() {
        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.GET_TEAM_LIST);
        params.setRequestCode(REQ_GET_TEAM_LIST);
        requestHttpConnect(params);
    }

    /**
     * 출동 차량 목록 불러오기
     */
    private void getVehicleList() {
        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.GET_VEHICLE_LIST);
        params.setRequestCode(REQ_GET_VEHICLE_LIST);
        requestHttpConnect(params);
    }

    /**
     * 나의 환경설정 불러오기
     */
    private void getMyConfigure() {
        Preferences pref = Preferences.getInstance(this);
        final String myID = pref.getID();

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.GET_MY_CONFIGURE);
        params.setRequestCode(REQ_GET_MY_CONFIGURE);
        params.addParameter("ID", myID);
        requestHttpConnect(params);
    }

    /**
     * 나의 환경설정 저장하기
     */
    private void setMyConfigure() {
        Preferences pref = Preferences.getInstance(this);
        final String myID = pref.getID();

        final String name = mEdtUserName.getText().toString().trim();
        final String position = mEdtClass.getText().toString().trim();

        String isFlexableWorkspace = "0";
        if(mCbFlexibleWorkplace.isChecked()) isFlexableWorkspace = "1";
        String isTotalPush = "0";
        if(mCbTotalVehiclePush.isChecked()) isTotalPush = "1";

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.SET_MY_CONFIGURE);
        params.setRequestCode(REQ_SET_MY_CONFIGURE);
        params.addParameter("ID", myID);
        params.addParameter("UserName", name);
        params.addParameter("Class", position);
        params.addParameter("FirestationPk", mSelectedFirestationPk);
        params.addParameter("TeamPK", mSelectedTeamPk);
        params.addParameter("VehiclePK", mSelectedVehiclePk);
        params.addParameter("IsFlexableWorkspace", isFlexableWorkspace);
        params.addParameter("IsTotalPush", isTotalPush);
        requestHttpConnect(params);
    }

    private String getFirestationName(String pk) {
        if (mFirestationDataList.size() == 0) return "";

        for (int i = 0; i < mFirestationDataList.size(); i++) {
            FirestationData item = mFirestationDataList.get(i);
            if (item.getFireStationPK().equals(pk)) {
                return item.getFireStationName();
            }
        }
        return "";
    }

    private String getTeamName(String pk) {
        if (mTeamDataList.size() == 0) return "";

        for (int i = 0; i < mTeamDataList.size(); i++) {
            TeamData item = mTeamDataList.get(i);
            if (item.getTeamPK().equals(pk)) {
                return item.getTeamName();
            }
        }
        return "";
    }

    private String getVehicleName(String pk) {
        if (mVehicleDataList.size() == 0) return "";

        for (int i = 0; i < mVehicleDataList.size(); i++) {
            VehicleData item = mVehicleDataList.get(i);
            if (item.getVehiclePK().equals(pk)) {
                return item.getVehicleName();
            }
        }
        return "";
    }

    private boolean getBoolType(String data) {
        if (data.equals("0")) return false;
        else return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        setMyConfigure();
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);

        if (requestCode == REQ_GET_FIRESTATIONLIST) {
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

            getTeamList();
        } else if (requestCode == REQ_GET_TEAM_LIST) {
            mTeamDataList.clear();
            String dataResult = json_result.getString("data");
            JSONArray ja = new JSONArray(dataResult);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                TeamData item = new TeamData();
                item.setTeamPK(jo.getString("Primarykey"));
                item.setTeamName(jo.getString("TeamName"));
                mTeamDataList.add(item);
            }

            getVehicleList();
        } else if (requestCode == REQ_GET_VEHICLE_LIST) {
            mVehicleDataList.clear();
            String dataResult = json_result.getString("data");
            JSONArray ja = new JSONArray(dataResult);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                VehicleData item = new VehicleData();
                item.setVehiclePK(jo.getString("Primarykey"));
                item.setVehicleName(jo.getString("VehicleName"));
                mVehicleDataList.add(item);
            }

            getMyConfigure();
        } else if (requestCode == REQ_GET_MY_CONFIGURE) {
            final String userName = json_result.getString("UserName");
            String position = json_result.getString("Class");
            if (position.equals("null")) position = "";

            mEdtUserName.setText(userName);
            mEdtClass.setText(position);

            mSelectedFirestationPk = json_result.getString("FirestationPk");
            mSelectedTeamPk = json_result.getString("TeamPK");
            mSelectedVehiclePk = json_result.getString("VehiclePK");

            String isFlexableWorkspace = json_result.getString("IsFlexableWorkspace");
            String isTotalPush = json_result.getString("IsTotalPush");

            mCbFlexibleWorkplace.setChecked(getBoolType(isFlexableWorkspace));
            mCbTotalVehiclePush.setChecked(getBoolType(isTotalPush));

            String firestaionName = getFirestationName(mSelectedFirestationPk);
            mTxtSelectedFirestationName.setText(firestaionName);
            String teamName = getTeamName(mSelectedTeamPk);
            mTxtWorkTeam.setText(teamName);
            String vehicleName = getVehicleName(mSelectedVehiclePk);
            mTxtVehicle.setText(vehicleName);
        } else if (requestCode == REQ_LOGOUT) {
            Preferences pref = Preferences.getInstance(this);
            pref.setID("");
            pref.setPassword("");
            pref.setMemberName("");
            pref.setMemberTelephone("");

            SimpleToast.show(this, "로그아웃하셨습니다.");
            showActivity(IntroActivity.class, true);
        }
    }

    @Override
    public void onHttpFailure(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpFailure(requestCode, json_result);
    }

    @Override
    public void onHttpError(String error_message) {
        super.onHttpError(error_message);
    }
}
