package weisure.com.keipacklib.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import weisure.com.keipacklib.dialog.SimpleAlertDialog;
import weisure.com.keipacklib.dialog.SimpleProgressDialog;
import weisure.com.keipacklib.http.AsyncHttpRequestTask;
import weisure.com.keipacklib.http.HttpRequestTask;
import weisure.com.keipacklib.http.HttpResponseListener;
import weisure.com.keipacklib.http.OvHttpRequestParameters;

/**
 * Created by chokyounglae on 15. 8. 4..
 */
public class BaseAppCompatActivity extends AppCompatActivity implements HttpResponseListener {

    public final static String DEFAULT_FONT = "NanumBarunGothic.otf";
    private boolean mIsUseCustomFont = false;
    private static Typeface mTypeface;
    private String mFontName = DEFAULT_FONT;

    protected final int REQ_CODE_PICK_GALLERY = 501;
    protected final int REQ_CODE_PICK_CAMERA = 502;
    protected final int REQ_CODE_PICK_CROP = 503;

    /**
     * 초기화 메소드
     */
    protected void init() {
        debugMessage("called init method.");
    }

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
     * Activity 띄위기
     * @param activityClass activity class type
     * @param stringExtraHashMap 문자열 extra hash map
     */
    protected void showActivity(Class<?> activityClass, HashMap<String, String> stringExtraHashMap) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Iterator<String> iter = stringExtraHashMap.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            final String val = stringExtraHashMap.get(key);
            debugMessage("key : " + key + ", value = " + val);
            intent.putExtra(key, val);
        }
        startActivity(intent);
    }

    /**
     * Activity 띄위기
     * @param activityClass activity class type
     * @param stringExtraHashMap 문자열 extra hash map
     * @param isCurrentActivityFinish true일 경우, 현재 Activity는 종료시키기고 Activity 띄운다.
     */
    protected void showActivity(Class<?> activityClass, HashMap<String, String> stringExtraHashMap, boolean isCurrentActivityFinish) {
        showActivity(activityClass, stringExtraHashMap);
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

        AsyncHttpRequestTask asyncHttpRequestTask = new AsyncHttpRequestTask(this);
        asyncHttpRequestTask.execute(params);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 카메라, 갤러리 이미지 선택 처리
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public File getSelectedImageFile() {
        return getTempImageFile();
    }

    private boolean mCropRequested = false;

    /**
     * crop 이 필요한 경우 설정함. 설정하지 않으면 crop 하지 않음.
     *
     * @param width
     *            crop size width.
     * @param height
     *            crop size height.
     */
    private int mCropAspectWidth = 1, mCropAspectHeight = 1;

    public void setCropOption(int aspectX, int aspectY) {
        mCropRequested = true;
        mCropAspectWidth = aspectX;
        mCropAspectHeight = aspectY;
    }

    /**
     * 사용할 이미지의 최대 크기 설정. 가로, 세로 지정한 크기보다 작은 사이즈로 이미지 크기를 조절함. default size :
     * 500
     *
     * @param sizePixel
     *            기본 500
     */
    private int mImageSizeBoundary = 500;

    public void setImageSizeBoundary(int sizePixel) {
        mImageSizeBoundary = sizePixel;
    }

    private boolean checkWriteExternalPermission() {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkSDisAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private File getTempImageFile() {
        File path = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/temp/");
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, "tempimage.png");
        return file;
    }

    /**
     * 카메라 호출
     */
    protected void showCaptureImage() {
        if (!checkWriteExternalPermission()) {
            alert("waring", "we need android.permission.WRITE_EXTERNAL_STORAGE");
            return;
        }
        if (!checkSDisAvailable()) {
            alert("waring", "Check External Storage.");
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempImageFile()));
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQ_CODE_PICK_CAMERA);
    }

    /**
     * 사진 갤러리 호출
     */
    protected void showPickPicture() {
        if (!checkWriteExternalPermission()) {
            alert("waring", "we need android.permission.WRITE_EXTERNAL_STORAGE");
            return;
        }
        if (!checkSDisAvailable()) {
            alert("waring", "Check External Storage.");
            return;
        }

        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        i.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQ_CODE_PICK_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_PICK_GALLERY && resultCode == Activity.RESULT_OK) {
            // 갤러리의 경우 곧바로 data 에 uri가 넘어옴.
            Uri uri = data.getData();
            copyUriToFile(uri, getTempImageFile());
            if (mCropRequested) {
                cropImage();
            } else {
                doFinalProcess();
            }
        } else if (requestCode == REQ_CODE_PICK_CAMERA && resultCode == Activity.RESULT_OK) {
            // 카메라의 경우 file 로 결과물이 돌아옴.
            // 카메라 회전 보정.
            correctCameraOrientation(getTempImageFile());
            if (mCropRequested) {
                cropImage();
            } else {
                doFinalProcess();
            }
        } else if (requestCode == REQ_CODE_PICK_CROP && resultCode == Activity.RESULT_OK) {
            // crop 한 결과는 file로 돌아옴.
            doFinalProcess();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void doFinalProcess() {
        // sample size 를 적용하여 bitmap load.
        Bitmap bitmap = loadImageWithSampleSize(getTempImageFile());

        // image boundary size 에 맞도록 이미지 축소.
        bitmap = resizeImageWithinBoundary(bitmap);

        // 결과 file 을 얻어갈 수 있는 메서드 제공.
        saveBitmapToFile(bitmap);

        // show image on ImageView
        Bitmap bm = BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath());
        onReceiveBitmapFromImageCapture(bm);
    }

    /**
     * 카메라나 겔러리에서 선택한 이미지를 bitmap으로 받아온다.
     * @param bm
     */
    protected void onReceiveBitmapFromImageCapture(Bitmap bm) { }

    private void saveBitmapToFile(Bitmap bitmap) {
        File target = getTempImageFile();
        try {
            FileOutputStream fos = new FileOutputStream(target, false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 이미지 사이즈 수정 후, 카메라 rotation 정보가 있으면 회전 보정함. */
    private void correctCameraOrientation(File imgFile) {
        Bitmap bitmap = loadImageWithSampleSize(imgFile);
        try {
            ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifRotateDegree = exifOrientationToDegrees(exifOrientation);
            bitmap = rotateImage(bitmap, exifRotateDegree);
            saveBitmapToFile(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return bitmap;
    }

    /**
     * EXIF정보를 회전각도로 변환하는 메서드
     *
     * @param exifOrientation
     *            EXIF 회전각
     * @return 실제 각도
     */
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /** 원하는 크기의 이미지로 options 설정. */
    private Bitmap loadImageWithSampleSize(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int width = options.outWidth;
        int height = options.outHeight;
        int longSide = Math.max(width, height);
        int sampleSize = 1;
        if (longSide > mImageSizeBoundary) {
            sampleSize = longSide / mImageSizeBoundary;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inPurgeable = true;
        options.inDither = false;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;
    }

    /**
     * mImageSizeBoundary 크기로 이미지 크기 조정. mImageSizeBoundary 보다 작은 경우 resize하지
     * 않음.
     */
    private Bitmap resizeImageWithinBoundary(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > height) {
            if (width > mImageSizeBoundary) {
                bitmap = resizeBitmapWithWidth(bitmap, mImageSizeBoundary);
            }
        } else {
            if (height > mImageSizeBoundary) {
                bitmap = resizeBitmapWithHeight(bitmap, mImageSizeBoundary);
            }
        }
        return bitmap;
    }

    private Bitmap resizeBitmapWithHeight(Bitmap source, int wantedHeight) {
        if (source == null)
            return null;

        int width = source.getWidth();
        int height = source.getHeight();

        float resizeFactor = wantedHeight * 1f / height;

        int targetWidth, targetHeight;
        targetWidth = (int) (width * resizeFactor);
        targetHeight = (int) (height * resizeFactor);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

        return resizedBitmap;
    }

    private Bitmap resizeBitmapWithWidth(Bitmap source, int wantedWidth) {
        if (source == null)
            return null;

        int width = source.getWidth();
        int height = source.getHeight();

        float resizeFactor = wantedWidth * 1f / width;

        int targetWidth, targetHeight;
        targetWidth = (int) (width * resizeFactor);
        targetHeight = (int) (height * resizeFactor);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

        return resizedBitmap;
    }

    private void copyUriToFile(Uri srcUri, File target) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            // 스트림 생성
            inputStream = (FileInputStream) getContentResolver().openInputStream(srcUri);
            outputStream = new FileOutputStream(target);

            // 채널 생성
            fcin = inputStream.getChannel();
            fcout = outputStream.getChannel();

            // 채널을 통한 스트림 전송
            long size = fcin.size();
            fcin.transferTo(0, size, fcout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fcout.close();
            } catch (IOException ioe) {
            }
            try {
                fcin.close();
            } catch (IOException ioe) {
            }
            try {
                outputStream.close();
            } catch (IOException ioe) {
            }
            try {
                inputStream.close();
            } catch (IOException ioe) {
            }
        }
    }

    private void cropImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> cropToolLists = getPackageManager().queryIntentActivities(intent, 0);
        int size = cropToolLists.size();
        if (size == 0) {
            // crop 을 처리할 앱이 없음. 곧바로 처리.
            doFinalProcess();
        } else {
            intent.setData(Uri.fromFile(getTempImageFile()));
            intent.putExtra("aspectX", mCropAspectWidth);
            intent.putExtra("aspectY", mCropAspectHeight);
            intent.putExtra("output", Uri.fromFile(getTempImageFile()));
            Intent i = new Intent(intent);
            ResolveInfo res = cropToolLists.get(0);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, REQ_CODE_PICK_CROP);
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
