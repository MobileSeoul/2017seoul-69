package weisure.com.keipacklib.http;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/*
 * arg0 : Object  excute() 실행시 넘겨줄 데이터 타입
 * arg1 : Void	  진행정보 데이터 타입 publishProgress(), onProgressUpdate()인수
 * arg2 : String  doInBackground() 종료시 리턴될 데이터 타입, onPostExcute() 인수
 */

/**
 * HTTP 서버 통신 요청 비동기 Task
 * @author chokyounglae
 */
public class HttpRequestTask extends AsyncTask<Object, Void, Object> {
    private final String TAG = "HttpRequestTask";
    private HttpResponseListener mResponseListener;
    private final String JSON_RESULT_TEXT  = "result";
    private final String JSON_SUCCESS_TEXT = "success";

    public HttpRequestTask() {
        mResponseListener = null;
    }

    public HttpRequestTask(HttpResponseListener responseListener) {
        mResponseListener = responseListener;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object... params) {
        Log.d(TAG, "doInBackground");

        OvHttpRequestResult ovResult = new OvHttpRequestResult();
        OvHttpRequestParameters postParams = (OvHttpRequestParameters)params[0];
        ArrayList<NameValuePair> postParamsArrList = postParams.getPostParameters();
        ovResult.setReuquestCode(postParams.getRequestCode());

        try {
            publishProgress();
            String httpResult = "";

            if(postParams.isContainBase64EncData()) {
                // 데이터안에 base64 엔코딩을 한 데이터가 있을 경우
                // 보통 Bitmap 이미지를 base64로 엔코딩한 후 서버로 전송한다.
                httpResult = CustomHttpClient.executeHttpPostImageNew(
                        postParams.getUrl(), postParamsArrList);
            }else{
                // 일반 데이터일 경우
                httpResult = CustomHttpClient.executeHttpPost(
                        postParams.getUrl(), postParamsArrList);
            }

            ovResult.setResultText(httpResult);
            ovResult.setHttpResultCode(OvHttpRequestResult.HTTP_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            ovResult.setHttpResultCode(getHttpResultErrorCode(e));
        }

        Log.d(TAG, "HTTP 요청 종료");
        return ovResult;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "onProgressUpdate");
    }

    @Override
    protected void onPostExecute(Object ovResult) {
        OvHttpRequestResult ovHttpResult = (OvHttpRequestResult)ovResult;
        if(ovHttpResult == null) {
            try {
                throw new Exception("OvHttpRequestResult is null.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(mResponseListener == null) {
            try {
                throw new Exception("ResponseListner is null.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // HTTP 결과 문자열을 직접 처리하기 위해서 사용
        mResponseListener.onHttpRawData(ovHttpResult.getResultText());
        mResponseListener.onHttpRawData(ovHttpResult.getReuquestCode(),
                ovHttpResult.getResultText());

        // http 통신 오류 체크
        if(ovHttpResult.getHttpResultCode() != OvHttpRequestResult.HTTP_SUCCESS) {
            mResponseListener.onHttpError("HTTP 통신 오류");
            return;
        }

        // json 프로토콜 체크
        if(ovHttpResult.initJsonObject())  // json 객체 초기화
        {
            JSONObject json = ovHttpResult.getJsonObject();
            try {
                if(json.getString(JSON_RESULT_TEXT).equals(JSON_SUCCESS_TEXT)) {
                    mResponseListener.onHttpSuccess(ovHttpResult.getReuquestCode(), json);
                }else{
                    mResponseListener.onHttpFailure(ovHttpResult.getReuquestCode(), json);
                }
            } catch (JSONException e) {
                Log.d(TAG, "JSON 오류");
                mResponseListener.onHttpJsonFormatError();
                e.printStackTrace();
            }
        }else{
            // json 객체 초기화 실패
            // 보통 http result 문자열이 json 문법에 맞지 않을 경우 발생한다.
            mResponseListener.onHttpJsonFormatError();
        }
    }

    private int getHttpResultErrorCode(IOException e) {
        int errorCode = 0;
        String exceptionMsg = e.toString();
        if(exceptionMsg == null) {
            Log.d(TAG, "통신안됨 : Exception message is null.");
            errorCode = OvHttpRequestResult.HTTP_ERROR_NULL;
        }
        else if(exceptionMsg.contains("java.net.SocketTimeoutException")) {
            // 서버통신 Timeout Exception
            Log.d(TAG, "통신안됨 : Socket timeout");
            errorCode = OvHttpRequestResult.HTTP_ERROR_TIMEOUT;
        }
        else if(exceptionMsg.contains("java.io.FileNotFoundException")) {
            // 해당 요청파일 없음
            Log.d(TAG, "통신안됨 : file not found exception");
            errorCode = OvHttpRequestResult.HTTP_ERROR_FILENOTFOUND;
        }
        else if(exceptionMsg.contains("java.net.UnknownHostException")) {
            // 통신안됨
            Log.d(TAG, "통신안됨 : Unknown Host exception");
            errorCode = OvHttpRequestResult.HTTP_ERROR_UNKNOWNHOST;
        }
        else{
            // 기타
            Log.d(TAG, "통신안됨 : " + e.toString());
            errorCode = OvHttpRequestResult.HTTP_ERROR_ETC;
        }

        return errorCode;
    }

    public void setResponseListener(HttpResponseListener responseListener) {
        this.mResponseListener = responseListener;
    }
}
