package weisure.com.keipacklib.http;

import org.json.JSONException;
import org.json.JSONObject;

public interface HttpResponseListener {
    /**
     * HTTP 서버 요청 후 반환받은 결과 문자열을 처리하기 위한 이벤트 핸들러
     * @param http_result HTTP 요청후 반환받은 결과 문자열
     */
    void onHttpRawData(String http_result);

    void onHttpRawData(int requestCode, String http_result);

    /**
     * HTTP 서버 요청 후 반환 받은 결과 문자열을 JSON 파싱 후 정상 처리되었을 때 발생되는 이벤트 핸들러
     * (웨저의 경우, result 데이터 필드가 success일 경우 실행)
     * @param requestCode Request Code 번호
     * @param json_result http 서버 요청 후 반환받은 JSON 객체
     * @throws JSONException
     */
    void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException;

    /**
     * HTTP 서버 요청 후 반환 받은 결과 문자열을 JSON 파싱 후 실패 처리되었을 때 발생되는 이벤트 핸들러
     * (웨저의 경우, result 데이터 필드가 fail일 경우 실행)
     * @param json_result http 서버 요청 후 반환받은 JSON 객체
     * @param requestCode Request Code 번호
     * @throws JSONException
     */
    void onHttpFailure(int requestCode, JSONObject json_result) throws JSONException;

    /**
     * HTTP 서버 요청시 통신 에러 일 경우 실행되는 이벤트 핸들러
     * @param error_message http 통신 에러 메시지
     */
    void onHttpError(String error_message);

    /**
     * HTTP 서버 요청 후 반환 받은 결과 문자열이 JSON 포맷이 아닐 경우 발생되는 이벤트 핸들러
     */
    void onHttpJsonFormatError();
}
