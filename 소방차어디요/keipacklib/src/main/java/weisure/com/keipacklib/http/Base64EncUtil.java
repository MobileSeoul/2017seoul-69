package weisure.com.keipacklib.http;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;

public class Base64EncUtil {
    /**
     * 비트맵 이미지를 Base64로 인코딩된 텍스트로 변환한다.
     * 기본 퀄러티 90
     * @param bmImage 비트맵이미지
     * @return Base64로 인코딩된 텍스트
     */
	public static String BitmapImageToBase64Enc(Bitmap bmImage){
		String base64Enc = "";
		if(bmImage == null) return base64Enc;
		base64Enc = BitmapImageToBase64Enc(bmImage, 90);
		return base64Enc;
	}

    /**
     * 비트맵 이미지를 Base64로 인코딩된 텍스트로 변환한다.
     * @param bmImage 비트맵이미지
     * @param quality 퀄러티 값
     * @return Base64로 인코딩된 텍스트
     */
	public static String BitmapImageToBase64Enc(Bitmap bmImage, int quality){
		String base64Enc = "";
		if(bmImage == null) return base64Enc;
		
		final ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bmImage.compress(Bitmap.CompressFormat.JPEG, quality, bao);
		byte[] ba = bao.toByteArray();
		base64Enc = OrgBase64.encodeBytes(ba);
		
		return base64Enc;
	}
}
