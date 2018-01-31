package weisure.com.keipacklib.graphic;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by chokyounglae on 15. 2. 6..
 */
public class BitmapUtil {
    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight) {

        final float densityMultiplier = Resources.getSystem()
                .getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    // 높이 값 기준으로 Bitmap 이미지를 스케일 변환 한다.
    public static Bitmap getScaledBitmap(Bitmap bmSrc,
                                         final int fixed_image_height) {
        if (bmSrc == null)
            return bmSrc;
        Bitmap bmScaledDst = null;
        int height = bmSrc.getHeight();
        int width = bmSrc.getWidth();

        if (height > fixed_image_height) {
            bmScaledDst = Bitmap.createScaledBitmap(bmSrc,
                    (width * fixed_image_height) / height, fixed_image_height,
                    true);
        } else {
            bmScaledDst = bmSrc;
        }

        return bmScaledDst;
    }

    // Uri값을 Bitmap 이미지로 불러온다.
    public static Bitmap getUriImageBitmap(Context context, Uri uri) {
        if (uri == null) return null;

        Bitmap bmImage = null;
        BitmapFactory.Options opt = null;
        AssetFileDescriptor afd;
        try {
            afd = context.getContentResolver()
                    .openAssetFileDescriptor(uri, "r");
            opt = new BitmapFactory.Options();
            opt.inSampleSize = 2;
            bmImage = BitmapFactory.decodeFileDescriptor(
                    afd.getFileDescriptor(), null, opt);

            bmImage = Bitmap.createScaledBitmap(bmImage, bmImage.getWidth(),
                    bmImage.getHeight(), true);

            try {
                // 사진의 메타데이터에서 회전정보를 판단하여 사진회전
                ExifInterface exif = new ExifInterface(BitmapUtil.getImagePath(context, uri));
                int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = GraphicsUtil
                        .exifOrientationToDegrees(exifOrientation);

                Log.d("BitmapUtil", "degree : " + exifDegree);
                bmImage = GraphicsUtil.getRotateBitmap(bmImage, exifDegree);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            bmImage = null;
            e.printStackTrace();
        }

        return bmImage;
    }

    // 이미지 Uri -> 경로 변환
    public static String getImagePath(Context context, Uri uri){
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = context.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
