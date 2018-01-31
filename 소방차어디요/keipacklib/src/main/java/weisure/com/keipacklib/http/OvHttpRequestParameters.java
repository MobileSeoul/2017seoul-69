package weisure.com.keipacklib.http;

import android.util.Log;

import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class OvHttpRequestParameters {
    final String TAG = "OvHttpRequestParameters";

    private int requestCode;
    private String url;
    private ArrayList<NameValuePair> postParameters;
    private boolean isContainBase64EncData;

    // android-async-http-1.4.8 기반
    private boolean isUseAndroidAsyncHttpModule = false;
    private RequestParams requestParams;

    public boolean isUseAndroidAsyncHttpModule() {
        return isUseAndroidAsyncHttpModule;
    }

    public OvHttpRequestParameters() {
        isContainBase64EncData = false;
        postParameters = new ArrayList<NameValuePair>();
        requestParams = null;
        isUseAndroidAsyncHttpModule = false;
    }

    public OvHttpRequestParameters(boolean isUseAndroidAsyncHttpModule) {
        isContainBase64EncData = false;

        this.isUseAndroidAsyncHttpModule = isUseAndroidAsyncHttpModule;
        if(isUseAndroidAsyncHttpModule) {
            postParameters = null;
            requestParams = new RequestParams();
        }else{
            postParameters = new ArrayList<NameValuePair>();
            requestParams = null;
        }
    }

    public void clearParameters() {
        postParameters.clear();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    // 파라미터에 Base64로 인코딩이 된 데이터가 있을 경우 true
    // 기본 값은 false
    public boolean isContainBase64EncData() {
        return isContainBase64EncData;
    }

    public void setContainBase64EncData(boolean isContainBase64EncData) {
        this.isContainBase64EncData = isContainBase64EncData;
    }

    /**
     * 텍스트 파라미터 추가
     * @param parameter
     * @param value
     */
    public void addParameter(String parameter, String value) {
        if(isUseAndroidAsyncHttpModule) {
            requestParams.put(parameter, value);
        }else{
            postParameters.add(new BasicNameValuePair(parameter, value));
        }
    }

    /**
     * 파일전송 파라미터 추가
     * @param parameter 파라미터 명
     * @param file 파일
     */
    public void addParameter(String parameter, File file) {
        if(isUseAndroidAsyncHttpModule) {
            try {
                requestParams.put(parameter, file);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG, "Async 모드가 아닐경우에는 File 첨부 사용 불가..");
        }
    }

    // 파라미터 값 불러내기
    public ArrayList<NameValuePair> getPostParameters() {
        return postParameters;
    }

    public RequestParams getRequestParams() {
        return requestParams;
    }
}
