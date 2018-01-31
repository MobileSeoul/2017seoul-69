package weisure.com.keipacklib.http;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * android-async-http-1.4.8 기반 HTTP 서버 통신 요청 비동기 Task
 * http://loopj.com/android-async-http/
 * @author chokyounglae
 */
public class AsyncHttpRequestTask  {
    private final String TAG = "AsyncHttpRequestTask";
    private HttpResponseListener mResponseListener;
    private final String JSON_RESULT_TEXT  = "result";
    private final String JSON_SUCCESS_TEXT = "success";

    private AsyncHttpClient client;
    OvHttpRequestResult ovResult;

    public AsyncHttpRequestTask() {
        mResponseListener = null;
        init();
    }

    public AsyncHttpRequestTask(HttpResponseListener responseListener) {
        mResponseListener = responseListener;
        init();
    }

    private void init() {
        client = new AsyncHttpClient();
        ovResult = new OvHttpRequestResult();
    }

    public void setResponseListener(HttpResponseListener responseListener) {
        this.mResponseListener = responseListener;
    }

    public boolean execute(OvHttpRequestParameters params) {
        if(params.isUseAndroidAsyncHttpModule() == false) {
            Log.e(TAG, "OvHttpRequestParameters.isUseAndroidAsyncHttpModule값이 true가 아님.");
            return false;
        }

        RequestParams requestParams = params.getRequestParams();
        ovResult.setReuquestCode(params.getRequestCode());
        client.post(params.getUrl(), requestParams, responseHandler);
        return true;
    }

    private AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String httpResult = new String(bytes);
            ovResult.setResultText(httpResult);
            ovResult.setHttpResultCode(OvHttpRequestResult.HTTP_SUCCESS);

            if(mResponseListener == null) {
                try {
                    throw new Exception("ResponseListner is null.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // HTTP 결과 문자열을 직접 처리하기 위해서 사용
            mResponseListener.onHttpRawData(httpResult);
            mResponseListener.onHttpRawData(ovResult.getReuquestCode(), httpResult);

            // json 프로토콜 체크
            if(ovResult.initJsonObject())  // json 객체 초기화
            {
                JSONObject json = ovResult.getJsonObject();
                try {
                    if(json.getString(JSON_RESULT_TEXT).equals(JSON_SUCCESS_TEXT)) {
                        mResponseListener.onHttpSuccess(ovResult.getReuquestCode(), json);
                    }else{
                        mResponseListener.onHttpFailure(ovResult.getReuquestCode(), json);
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

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            if(mResponseListener == null) {
                try {
                    throw new Exception("ResponseListner is null.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String httpResult = "";
            try { httpResult = new String(bytes); }
            catch (NullPointerException e) {
                e.printStackTrace();
                httpResult = e.getMessage();
            }

            Log.e(TAG, httpResult);
            mResponseListener.onHttpError(httpResult);
        }
    };
}
