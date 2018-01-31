package weisure.com.keipacklib.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;

import com.loopj.android.http.RequestParams;

public class CustomHttpClient {
	/** The time it takes for our client to timeout */
	public static final int HTTP_TIMEOUT = 10 * 1000; // milliseconds

	/** Single instance of our HttpClient */
	private static HttpClient mHttpClient;

	/**
	 * Get our single instance of our HttpClient object.
	 * 
	 * @return an HttpClient object with connection parameters set
	 */
	private static HttpClient getHttpClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			final HttpParams params = mHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		}
		return mHttpClient;
	}

	/**
	 * Performs an HTTP Post request to the specified url with the specified
	 * parameters.
	 * 
	 * @param url
	 *            The web address to post the request to
	 * @param postParameters
	 *            The parameters to send via the request
	 * @return The result of the request
	 * @throws Exception
	 */

	public static synchronized String executeHttpPost(String $url,
			ArrayList<NameValuePair> postParameters) throws IOException {
		String myResult = "";
		URL url;

		url = new URL($url);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setDefaultUseCaches(false);
		http.setDoInput(true);
		http.setDoOutput(true);
		http.setConnectTimeout(HTTP_TIMEOUT);
		http.setRequestMethod("POST");
		http.setRequestProperty("content-type",
				"application/x-www-form-urlencoded");
		HttpURLConnection.setFollowRedirects(true);

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < postParameters.size(); i++) {
			if (i > 0) {
				buffer.append("&");
			}
			buffer.append(postParameters.get(i).getName()).append("=")
					.append(postParameters.get(i).getValue());
		}

		OutputStreamWriter outStream = new OutputStreamWriter(
				http.getOutputStream(), "UTF-8");
		PrintWriter writer = new PrintWriter(outStream);
		writer.write(buffer.toString());
		writer.flush();

		InputStreamReader tmp = new InputStreamReader(http.getInputStream(),
				"UTF-8");
		BufferedReader reader = new BufferedReader(tmp);
		StringBuilder builder = new StringBuilder();
		String str;

		while ((str = reader.readLine()) != null) {
			builder.append(str + "\n");
		}

		myResult = builder.toString();

		return myResult;
	}

	public static synchronized Bitmap executeHttpGetImage(String $url) {
		Bitmap bitmap = null;
		try {
			URL imageURL = new URL($url);
			HttpURLConnection conn = (HttpURLConnection) imageURL
					.openConnection();
			BufferedInputStream bis = new BufferedInputStream(
					conn.getInputStream(), 1024);
			bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
			bitmap = null;
		}

		return bitmap;
	}

	public static synchronized String executeHttpPostImage(String $url,
			ArrayList<NameValuePair> postParameters) throws IOException {
		String myResult = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost($url);
		UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters, "UTF-8");
		httppost.setEntity(form);
		HttpResponse response = httpclient.execute(httppost);

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		StringBuilder str = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			str.append(line);
		}
		in.close();
		myResult = str.toString();

		return myResult;
	}

	public static synchronized String executeHttpPostImageNew(
			String url,
			ArrayList<NameValuePair> postParameters) throws IOException {


		RequestParams params = new RequestParams();
		for(int i=0;i<postParameters.size();i++) {
			String paramName = postParameters.get(i).getName();
			String paramValue = postParameters.get(i).getValue();
			params.put(paramName, paramValue);
		}


		return "";

//		//MultipartEntityBuilder 생성
//		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//		//문자열 및 데이터 추가
//		for(int i=0;i<postParameters.size();i++) {
//			String paramName = postParameters.get(i).getName();
//			String paramValue = postParameters.get(i).getValue();
//			builder.addTextBody(paramName, paramValue,
//					ContentType.create("Multipart/related", "UTF-8"));
//		}
////		builder.addPart("IMAGE_KEY",
////				                      new FileBody(new File(imagePath)));
//
//		//전송
//		InputStream inputStream = null;
//		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
//		String result = "";
////		HttpClient httpClient = AndroidHttpClient.newInstance("Android");
//		try {
//			HttpPost httpPost = new HttpPost(url);
//			httpPost.setEntity(builder.build());
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			HttpEntity httpEntity = httpResponse.getEntity();
//			inputStream = httpEntity.getContent();
//
//			//응답
//			BufferedReader bufferedReader = new BufferedReader(
//					new InputStreamReader(inputStream, "UTF-8"));
//			StringBuilder stringBuilder = new StringBuilder();
//			String line = null;
//			while ((line = bufferedReader.readLine()) != null) {
//				stringBuilder.append(line + "\n");
//			}
//			inputStream.close();
//
//			result = stringBuilder.toString();
//		} catch (IOException e) {
//
//		} finally {
//			httpClient.close();
//		}
//
//		//응답 결과
//		return result;
	}

	/**
	 * Performs an HTTP GET request to the specified url.
	 * 
	 * @param url
	 *            The web address to post the request to
	 * @return The result of the request
	 * @throws Exception
	 */
	public static String executeHttpGet(String url) throws Exception {

		BufferedReader in = null;
		try {
			HttpClient client = getHttpClient();

			HttpGet request = new HttpGet();

			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			String result = sb.toString();
			return result;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
