package weisure.com.keipacklib.graphic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Log;

/**
 * Created by chokyounglae on 15. 2. 6..
 */
public class GraphicsUtil {
    private static String TAG = "GraphicsUtil";

    public static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static Bitmap getRotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || bitmap == null) return bitmap;

        Matrix m = new Matrix();
        m.setRotate(
                degrees,
                (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);

        try {
            Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), m, true);
            if (bitmap != converted) {
                bitmap.recycle();
                bitmap = converted;
            }
        } catch (OutOfMemoryError ex) {
            // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            ex.printStackTrace();
        }

        return bitmap;
    }

    /*
     * Draw image in circular shape Note: change the pixel size if you want
     * image small or large
     */
    public static Bitmap getCircleBitmap(Bitmap srcBitmap) {
        Bitmap output = null, cropBmp;
        Canvas canvas;
        try {
            if (srcBitmap.getWidth() >= srcBitmap.getHeight()) {
                cropBmp = Bitmap.createBitmap(srcBitmap, srcBitmap.getWidth()
                                / 2 - srcBitmap.getHeight() / 2, 0,
                        srcBitmap.getHeight(), srcBitmap.getHeight());
            } else {
                cropBmp = Bitmap.createBitmap(srcBitmap, 0,
                        srcBitmap.getHeight() / 2 - srcBitmap.getWidth() / 2,
                        srcBitmap.getWidth(), srcBitmap.getWidth());
            }

            output = Bitmap.createBitmap(cropBmp.getWidth(),
                    cropBmp.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(output);

            final int color = 0xffff0000;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, cropBmp.getWidth(),
                    cropBmp.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setFilterBitmap(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) 4);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(cropBmp, rect, rect, paint);
        } catch (OutOfMemoryError e) {
            // TODO: handle exception
            Log.i(TAG, "exception error : " + e.getMessage());
            // System.out.println(e.getMessage());
        }
        return output;
    }
}
