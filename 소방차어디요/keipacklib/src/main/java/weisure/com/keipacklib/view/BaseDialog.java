package weisure.com.keipacklib.view;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import weisure.com.keipacklib.dialog.SimpleAlertDialog;
import weisure.com.keipacklib.dialog.SimpleProgressDialog;
import weisure.com.keipacklib.http.AsyncHttpRequestTask;
import weisure.com.keipacklib.http.HttpRequestTask;
import weisure.com.keipacklib.http.HttpResponseListener;
import weisure.com.keipacklib.http.OvHttpRequestParameters;

/**
 * Created by chokyounglae on 2016. 3. 22..
 */
public class BaseDialog extends Dialog implements HttpResponseListener {

    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * 기본 Alert Dialog 메소드
     * @param title Alert Dialog 제목
     * @param message Alert Dialog 메시지
     */
    protected void alert(String title, String message)
    {
        SimpleAlertDialog.show(getContext(), title, message);
    }

    /**
     * Logcat 디버그 메시지
     * @param message 디버그 메시지
     */
    protected void debugMessage(String message) {
        Log.d(this.getClass().getName(), message);
    }

    /**
     * HTTP 서버 연결 요청
     * @param params HTTP 통신 파라미터
     * @return true - HTTP 요청 성공, false - HTTP 요청 실패
     */
    protected boolean requestHttpConnect(OvHttpRequestParameters params)
    {
        requestHttpConnect(params, false);
        return true;
    }

    /**
     * HTTP 서버 연결 요청
     * @param params
     * @return true - HTTP 요청 성공, false - HTTP 요청 실패
     */
    protected boolean requestHttpConnect(OvHttpRequestParameters params, boolean isShowProgressDialog)
    {

        ConnectivityManager connMgr = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isConnected()) {
            debugMessage("네트워크 연결 실패");
            return false;
        }

        debugMessage("네트워크 연결");
        if(isShowProgressDialog) {
            SimpleProgressDialog.show(getContext());
        }

        if(params.isUseAndroidAsyncHttpModule()) {
            AsyncHttpRequestTask asyncHttpRequestTask = new AsyncHttpRequestTask(this);
            return asyncHttpRequestTask.execute(params);
        }

        HttpRequestTask httpRequestTask = new HttpRequestTask(this);
        httpRequestTask.execute(params);
        return true;
    }


    /**
     * android-async-http-1.4.8 기반 HTTP Post 전송 요청
     * @param params HTTP 통신 파라미터
     * @return true - HTTP 요청 성공, false - HTTP 요청 실패
     */
    protected boolean requestAsyncHttp(OvHttpRequestParameters params) {
        requestAsyncHttp(params, true);
        return true;
    }

    /**
     * android-async-http-1.4.8 기반 HTTP Post 전송 요청
     * @param params
     * @param isShowProgressDialog
     * @return
     */
    protected boolean requestAsyncHttp(OvHttpRequestParameters params, boolean isShowProgressDialog)
    {
        ConnectivityManager connMgr = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isConnected()) {
            debugMessage("네트워크 연결 실패");
            return false;
        }

        debugMessage("네트워크 연결");
        if(isShowProgressDialog) {
            SimpleProgressDialog.show(getContext());
        }

        AsyncHttpRequestTask asyncHttpRequestTask = new AsyncHttpRequestTask(this);
        asyncHttpRequestTask.execute(params);
        return true;
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
