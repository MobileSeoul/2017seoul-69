package grocket.com.smart119citizen.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import grocket.com.smart119citizen.R;


/**
 * Created by chokyounglae on 15. 8. 7..
 */
public class CheckInvalidUtil {

    /**
     * 로그인 유효성 체크
     * @param edtEmail
     * @param edtPassword
     * @return
     */
    public CheckInvalidData checkLoginInvalid(
            Context context,
            EditText edtEmail, EditText edtPassword
    ) {
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();

        CheckInvalidData result = new CheckInvalidData();

        if (TextUtils.isEmpty(email)) {
            final String msg = context.getResources().getString(R.string.invalid_check_msg_empty_id);
            result.setInvalidResult(false);
            result.setErrorMessage(msg);
            result.setFocusView(edtEmail);
        } else if (TextUtils.isEmpty(password)) {
            final String msg = context.getResources().getString(R.string.invalid_check_msg_empty_password);
            result.setInvalidResult(false);
            result.setErrorMessage(msg);
            result.setFocusView(edtPassword);
        } else if(password.length() < 6) {
            final String msg = context.getResources().getString(R.string.invalid_check_msg_password_length);
            result.setInvalidResult(false);
            result.setErrorMessage(msg);
            result.setFocusView(edtPassword);
        }
        else {
            result.setInvalidResult(true);
            result.setErrorMessage("");
            result.setFocusView(null);
        }

        return result;
    }

    public CheckInvalidData checkJoinInvalid(
            Context context,
            EditText edtEmail,
            EditText edtPassword,
            EditText edtPasswordConfirm,
            EditText edtName,
            String fireStation
    ) {
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String passwordConfirm = edtPasswordConfirm.getText().toString().trim();
        final String name = edtName.getText().toString().trim();

        CheckInvalidData result = new CheckInvalidData();

        if (TextUtils.isEmpty(email)) {
            final String msg = context.getResources().getString(R.string.invalid_check_msg_empty_id);
            result.setInvalidResult(false);
            result.setErrorMessage(msg);
            result.setFocusView(edtEmail);
        } else if (TextUtils.isEmpty(password)) {
            final String msg = context.getResources().getString(R.string.invalid_check_msg_empty_password);
            result.setInvalidResult(false);
            result.setErrorMessage(msg);
            result.setFocusView(edtPassword);
        } else if(password.length() < 6) {
            final String msg = context.getResources().getString(R.string.invalid_check_msg_password_length);
            result.setInvalidResult(false);
            result.setErrorMessage(msg);
            result.setFocusView(edtPassword);
        } else if(!password.equals(passwordConfirm)) {
            final String msg = context.getResources().getString(R.string.invalid_check_msg_not_match_password);
            result.setInvalidResult(false);
            result.setErrorMessage(msg);
            result.setFocusView(edtPassword);
        } else if(TextUtils.isEmpty(name)) {
            final String msg = context.getResources().getString(R.string.invalid_check_msg_empty_name);
            result.setInvalidResult(false);
            result.setErrorMessage(msg);
            result.setFocusView(edtName);
        } else if(TextUtils.isEmpty(fireStation)) {
            final String msg = context.getResources().getString(R.string.invalid_check_msg_empty_firestation);
            result.setInvalidResult(false);
            result.setErrorMessage(msg);
            result.setFocusView(null);
        }
        else {
            result.setInvalidResult(true);
            result.setErrorMessage("");
            result.setFocusView(null);
        }

        return result;
    }

    public CheckInvalidData checkKakaoJoinInvalid(
            EditText edtName,
            String locationCode
    ) {
        final String name = edtName.getText().toString().trim();
        CheckInvalidData result = new CheckInvalidData();

        if(TextUtils.isEmpty(name)) {
            result.setInvalidResult(false);
            result.setErrorMessage("이름을 입력하셔야 합니다.");
            result.setFocusView(edtName);
        } else if(TextUtils.isEmpty(locationCode)) {
            result.setInvalidResult(false);
            result.setErrorMessage("지역을 입력하셔야 합니다.");
            result.setFocusView(null);
        }
        else {
            result.setInvalidResult(true);
            result.setErrorMessage("");
            result.setFocusView(null);
        }

        return result;
    }

    /**
     * 유효성 체크 데이터 객체
     */
    public class CheckInvalidData {
        private boolean invalidResult;
        private String errorMessage;
        private View focusView;

        public CheckInvalidData() {
            invalidResult = false;
            errorMessage = "";
            focusView = null;
        }

        public CheckInvalidData(boolean invalidResult, String errorMessage, View focusView) {
            this.invalidResult = invalidResult;
            this.errorMessage = errorMessage;
            this.focusView = focusView;
        }

        public boolean isInvalidResult() {
            return invalidResult;
        }

        public void setInvalidResult(boolean invalidResult) {
            this.invalidResult = invalidResult;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public View getFocusView() {
            return focusView;
        }

        public void setFocusView(View focusView) {
            this.focusView = focusView;
        }
    }
}
