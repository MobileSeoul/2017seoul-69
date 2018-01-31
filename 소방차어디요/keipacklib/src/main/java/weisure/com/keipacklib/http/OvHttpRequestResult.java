package weisure.com.keipacklib.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class OvHttpRequestResult {
    // HTTP 통신 결과
    public final static int HTTP_SUCCESS			= 1000;
    public final static int HTTP_ERROR_NULL			= 1001;
    public final static int HTTP_ERROR_TIMEOUT		= 1002;
    public final static int HTTP_ERROR_FILENOTFOUND	= 1003;
    public final static int HTTP_ERROR_UNKNOWNHOST	= 1004;
    public final static int HTTP_ERROR_ETC			= 1005;

    private String resultText = "";
    private JSONObject json;
    private int httpResultCode = 0;
    private int reuquestCode = 0;

    public OvHttpRequestResult()
    {
    }

    // http 통신으로 받아온 결과 텍스트 반환
    public String getResultText() {
        return resultText;
    }

    // http 통신으로 받아온 결과 저장
    public void setResultText(String result) {
        this.resultText = result;
    }

    // http 통신 결과 코드값 반환
    public int getHttpResultCode() {
        return httpResultCode;
    }

    // http 통신 결과 코드값 저장
    public void setHttpResultCode(int httpResultCode) {
        this.httpResultCode = httpResultCode;
    }

    // Request Code 반환
    public int getReuquestCode() {
        return reuquestCode;
    }

    // Request Code 저장
    public void setReuquestCode(int reuquestCode) {
        this.reuquestCode = reuquestCode;
    }

    // json 오브젝트 초기화
    // 기존에 저장된 resultText를 가지고 Json 오브젝트를 생성
    // 정상적으로 생성된 경우 true, 아닌경우 false
    public boolean initJsonObject()
    {
        return initJsonObject(this.resultText);
    }

    // json 오브젝트 초기화
    // 인자로 입력된 resultText를 가지고 Json 오브젝트를 생성
    // 정상적으로 생성된 경우 true, 아닌경우 false
    public boolean initJsonObject(String result)
    {
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            json = null;
            e.printStackTrace();
            Log.d("OvHttpRequestResult", result);
            return false;
        }

        return true;
    }

    public JSONObject getJsonObject() {
        return json;
    }
}
