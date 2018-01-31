package grocket.com.smart119citizen;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weisure.com.keipacklib.data.TimeValue;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class MobilizeLocActivity extends CommonCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener, LocationListener, View.OnClickListener {

    private final int REQ_GET_WEATHER_INFO = 100;
    static final LatLng DEF_LOC_SEOUL = new LatLng(37.56, 126.97);

    @InjectView(R.id.txtDirectionOfWind)
    TextView mTxtDirectionOfWind;
    @InjectView(R.id.txtTemp)
    TextView mTxtTemp;
    @InjectView(R.id.txtHum)
    TextView mTxtHum;
    @InjectView(R.id.txtAccidentContent)
    TextView mTxtAccidentContent;
    @InjectView(R.id.txtTimer)
    TextView mTxtTimer;
    @InjectView(R.id.btnLive)
    ImageButton mBtnLive;
    @InjectView(R.id.btnMic)
    ImageButton mBtnMic;
    @InjectView(R.id.btnPic)
    ImageButton mBtnPic;
    @InjectView(R.id.txtNotServiceHopistal1)
    TextView mTxtNotServiceHopistal1;
    @InjectView(R.id.txtNotServiceHopistal2)
    TextView mTxtNotServiceHopistal2;

    private GoogleMap mGoogleMap;

    private int mCommandType;
    private String mAccidentNo;
    private String mAccidentAddr;
    private String mAccidentAddrDetail;
    private String mAccidentContent;
    private String mReporterTel;
    private String mRegDate;

    private double mAccidentGeoPtLat = 0; // 사고지점 위도
    private double mAccidentGeoPtLng = 0; // 사고지점 경도
    private TimeValue mGoldenTimerValue;
    private Timer mGoldenTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilize_loc);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        Bundle argsBundle = intent.getExtras();
        if (argsBundle != null) {
            mCommandType = argsBundle.getInt("CommandType");
            mAccidentNo = argsBundle.getString("AccidentNo");
            mAccidentAddr = argsBundle.getString("AccidentAddr");
            mAccidentAddrDetail = argsBundle.getString("AccidentAddrDetail");
            mAccidentContent = argsBundle.getString("AccidentContent");
            mReporterTel = argsBundle.getString("ReporterTel");
            mRegDate = argsBundle.getString("RegDate");

            debugMessage("재해번호 : " + mAccidentNo);
            debugMessage("주소 : " + mAccidentAddr);
            debugMessage("상세주소 : " + mAccidentAddrDetail);
            debugMessage("사고내용 : " + mAccidentContent);
            debugMessage("신고자 전화번호 : " + mReporterTel);

            String content = mAccidentAddr + "\n\n" +
                    mAccidentContent + "\n\n" +
                    "신고자 전화번호 : " + mReporterTel;
            mTxtAccidentContent.setText(content);
        }

        init();
    }

    @Override
    protected void init() {
        super.init();

        mGoldenTimerValue = new TimeValue();
        // 사고 접수 날짜 시간을 기준으로 골든타임 5분으로 설정
        String[] tmp1 = mRegDate.split(" ");
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdfNow.format(new Date(System.currentTimeMillis()));
        String[] tmp2 = time.split(" ");

        debugMessage("사고 접수 날짜 : " + mRegDate);
        debugMessage("현재 시간 : " + time);

        mTxtTimer.setText("05:00:00");
        if (tmp1[0].equals(tmp2[0])) {
            // 같은 날짜일 경우에는 골든 타임 진행

            String[] accidentTime = tmp1[1].split(":");
            Calendar accidentCal = Calendar.getInstance();
            accidentCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(accidentTime[0]));
            accidentCal.set(Calendar.MINUTE, Integer.parseInt(accidentTime[1]));
            accidentCal.set(Calendar.SECOND, Integer.parseInt(accidentTime[2]));

            long accidentMillis = accidentCal.getTimeInMillis();

            Calendar nowCal = Calendar.getInstance();
            long nowMillis = nowCal.getTimeInMillis();

            debugMessage("accident mil : " + accidentMillis + ",  now mil : " + nowMillis);
            long diff = nowMillis - accidentMillis;
            long diff_sec = TimeUnit.MILLISECONDS.toSeconds(diff);
            if (diff_sec <= (long) (60 * 5)) {
                // 골든 타이머 동작
                mGoldenTimerValue.set_second((int) diff_sec);
                mGoldenTimer = new Timer();
                mGoldenTimer.schedule(mGoldenTimerTask, 0, 1000);
            }
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mBtnLive.setOnClickListener(this);
        mBtnMic.setOnClickListener(this);
        mBtnPic.setOnClickListener(this);

        mTxtNotServiceHopistal1.setOnClickListener(this);
        mTxtNotServiceHopistal2.setOnClickListener(this);
    }

    TimerTask mGoldenTimerTask = new TimerTask() {
        @Override
        public void run() {
            mGoldenTimerValue.add_second(1);
            if (mGoldenTimerValue.get_TotalMinute() > 5.0f) {
                mGoldenTimer.cancel();
                mGoldenTimer = null;
                return;
            }


            runOnUiThread(new TimerTask() {
                @Override
                public void run() {
                    final String time = String.format("%02d:%02d:%02d",
                            mGoldenTimerValue.get_hour(),
                            mGoldenTimerValue.get_minute(),
                            mGoldenTimerValue.get_second());
                    mTxtTimer.setText(time);
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoldenTimer != null) {
            mGoldenTimer.cancel();
            mGoldenTimer = null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setTrafficEnabled(true);
        mGoogleMap.setIndoorEnabled(true);
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEF_LOC_SEOUL, 15));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

        // 사고 주소를 위도, 경도로 변환 요청
        getGeoPoint(mAccidentAddr);
    }

    /**
     * 날씨정보 불러오기
     */
    private void getWeatherInfo(double lat, double lon) {
        String url = String.format(
                "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&APPID=3ab2b43f62314b2ec3e2f41b684fdc18",
                lat, lon);
        debugMessage(url);

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(url);
        params.setRequestCode(REQ_GET_WEATHER_INFO);
        requestHttpConnect(params);
    }

    /**
     * 주소 -> 위도 경도 변환
     *
     * @param address
     */
    private void getGeoPoint(String address) {

        debugMessage("주소 : " + address);

        Geocoder mGeoCoder =
                new Geocoder(getApplicationContext(), Locale.KOREA);
        try {
            List<Address> addrs =
                    mGeoCoder.getFromLocationName(address, 1);

            mAccidentGeoPtLat = addrs.get(0).getLatitude();
            mAccidentGeoPtLng = addrs.get(0).getLongitude();

            debugMessage("사고 주소 : " + mAccidentAddr);
            debugMessage("위도 : " + mAccidentGeoPtLat);
            debugMessage("경도 : " + mAccidentGeoPtLng);

            // 사고 지점 날씨 정보 불러오기
            getWeatherInfo(mAccidentGeoPtLat, mAccidentGeoPtLng);

            // 사고 지점으로 이동
            LatLng startingPoint = new LatLng(mAccidentGeoPtLat, mAccidentGeoPtLng);
            mGoogleMap.clear();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 14));

            // 사고 위치  마커
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon));
            markerOptions.title("사고 위치");
            markerOptions.position(startingPoint);
            mGoogleMap.addMarker(markerOptions);

            startingPoint = new LatLng(mAccidentGeoPtLat, mAccidentGeoPtLng + 0.00450);
            markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_symbol));
            markerOptions.title("소방차 위치");
            markerOptions.position(startingPoint);
            mGoogleMap.addMarker(markerOptions);

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHttpRawData(int requestCode, String http_result) {
        super.onHttpRawData(requestCode, http_result);
        if (requestCode == REQ_GET_WEATHER_INFO) {
            try {
                JSONObject json = new JSONObject(http_result);
                String main = json.getString("main");
                String wind = json.getString("wind");

                JSONObject main_json = new JSONObject(main);
                JSONObject wind_json = new JSONObject(wind);

                final String temp = main_json.getString("temp") + "(도)";
                final String hum = main_json.getString("humidity") + "(%)";

                int degree = wind_json.getInt("deg");
                String dir_wind = "풍향";
                if (degree == 0) dir_wind = "동품";
                else if (degree > 0 && degree < 90) dir_wind = "북동풍\n";
                else if (degree == 90) dir_wind = "북풍";
                else if (degree > 90 && degree < 180) dir_wind = "북서풍\n";
                else if (degree == 180) dir_wind = "서풍";
                else if (degree > 180 && degree < 270) dir_wind = "남서풍\n";
                else if (degree == 270) dir_wind = "남풍";
                else if (degree > 270 && degree < 360) dir_wind = "남동풍\n";
                else if (degree >= 360) dir_wind = "동풍";

                final String speed = dir_wind + "(" + wind_json.getString("speed") + "m/s)";

                mTxtTemp.setText(temp);
                mTxtHum.setText(hum);
                mTxtDirectionOfWind.setText(speed);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);
    }

    @Override
    public void onHttpFailure(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpFailure(requestCode, json_result);
        if (requestCode != REQ_GET_WEATHER_INFO) {
            debugMessage(json_result.getString("fail_cause"));
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onClick(View v) {
        if (v == mBtnLive) {
            debugMessage("live");
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivity(takePictureIntent);

            Intent intent = new Intent(this, SampleVedioViewActivity.class);
            intent.putExtra("CommandType", mCommandType);
            intent.putExtra("AccidentNo", mAccidentNo);
            intent.putExtra("AccidentAddr", mAccidentAddr);
            intent.putExtra("AccidentAddrDetail", mAccidentAddrDetail);
            intent.putExtra("AccidentContent", mAccidentContent);
            intent.putExtra("ReporterTel", mReporterTel);
            intent.putExtra("RegDate", mRegDate);
            startActivity(intent);
        } else if (v == mBtnMic) {
            debugMessage("record");
            try {
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                SimpleToast.show(this, "마이크앱이 존재하지 않습니다.");
            }
        } else if (v == mBtnPic) {
            debugMessage("picture");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(takePictureIntent);
        } else if (v == mTxtNotServiceHopistal1) {
            SimpleToast.show(this, "Full Bed", Gravity.CENTER, 1500);
        } else if (v == mTxtNotServiceHopistal2) {
            SimpleToast.show(this, "MRI 고장", Gravity.CENTER, 1500);
        }

    }
}
