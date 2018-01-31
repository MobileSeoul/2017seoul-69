package weisure.com.keipacklib.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import weisure.com.keipacklib.dialog.SimpleAlertDialog;
import weisure.com.keipacklib.dialog.SimpleProgressDialog;
import weisure.com.keipacklib.http.HttpRequestTask;
import weisure.com.keipacklib.http.HttpResponseListener;
import weisure.com.keipacklib.http.OvHttpRequestParameters;

/**
 * Created by chokyounglae on 15. 2. 6..
 */
public class BaseFragmentActivity extends FragmentActivity implements HttpResponseListener {
    public final static String DEFAULT_FONT = "NanumBarunGothic.otf";
    private boolean mIsUseCustomFont = false;
    private static Typeface mTypeface;
    private String mFontName = DEFAULT_FONT;

    @Override
    protected void onResume() {
        super.onResume();
        debugMessage("parent : onResume");
        SimpleProgressDialog.dismiss();
    }

    /**
     * 기본 Alert Dialog 메소드
     * @param title Alert Dialog 제목
     * @param message Alert Dialog 메시지
     */
    protected void alert(String title, String message)
    {
        SimpleAlertDialog.show(this, title, message);
    }

    /**
     * Logcat 디버그 메시지
     * @param message 디버그 메시지
     */
    protected void debugMessage(String message) {
        Log.d(this.getClass().getName(), message);
    }

    /**
     * Activity 띄우기
     * @param activityClass activity class type
     */
    protected void showActivity(Class<?> activityClass) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Activity 띄우기
     * @param activityClass activity class type
     * @param isCurrentActivityFinish true일 경우, 현재 Activity는 종료시키기고 Activity 띄운다.
     */
    protected void showActivity(Class<?> activityClass, boolean isCurrentActivityFinish) {
        showActivity(activityClass);
        if(isCurrentActivityFinish) finish();
    }

    /**
     * HTTP 서버 연결 요청
     * @param params HTTP 통신 파라미터
     * @return true - HTTP 요청 성공, false - HTTP 요청 실패
     */
    protected boolean requestHttpConnect(OvHttpRequestParameters params)
    {
        requestHttpConnect(params, true);
        return true;
    }

    /**
     * HTTP 서버 연결 요청
     * @param params
     * @return true - HTTP 요청 성공, false - HTTP 요청 실패
     */
    protected boolean requestHttpConnect(OvHttpRequestParameters params, boolean isShowProgressDialog)
    {
        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isConnected()) {
            debugMessage("네트워크 연결 실패");
            return false;
        }

        debugMessage("네트워크 연결");
        if(isShowProgressDialog) {
            SimpleProgressDialog.show(this);
        }
        HttpRequestTask httpRequestTask = new HttpRequestTask(this);
        httpRequestTask.execute(params);
        return true;
    }

    /**
     * 외부 폰트 사용
     * @param fontName asset 디렉토리에 저장된 폰트 이름
     */
    protected void enableExternalFont(String fontName) {
        mIsUseCustomFont = true;
        mFontName = fontName;
    }

    /**
     * 외부 폰트 사용 금지
     */
    protected void disableExternalFont() {
        mIsUseCustomFont = false;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        if(mIsUseCustomFont) {
            if (mTypeface == null)
            {
                mTypeface = Typeface.createFromAsset(getAssets(), mFontName);
            }

            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            setGlobalFont(root);
        }
    }

    private void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView) child).setTypeface(mTypeface);
            else if (child instanceof Button)
                ((Button) child).setTypeface(mTypeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup) child);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // HTTP 요청 결과 이벤트 핸들러
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onHttpRawData(String http_result) {
        completedHttpRequest();
    }

    @Override
    public void onHttpRawData(int requestCode, String http_result) {
        completedHttpRequest();
    }

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        completedHttpRequest();
    }

    @Override
    public void onHttpFailure(int requestCode, JSONObject json_result) throws JSONException {
        completedHttpRequest();
    }

    @Override
    public void onHttpError(String error_message) {
        completedHttpRequest();
    }

    @Override
    public void onHttpJsonFormatError() {
        completedHttpRequest();
    }

    /**
     * Http 통신 완료 후에 실행되는 공통 메소드
     */
    protected void completedHttpRequest()
    {
        SimpleProgressDialog.dismiss();
    }
}
