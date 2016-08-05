package com.mark.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtils {
	public static String getResultForHttpGet(String url, String content)
			throws ClientProtocolException, IOException {

		String result = null;
		HttpGet httpGet = new HttpGet(url);// 编者按：与HttpPost区别所在，这里是将参数在地址中传递
		HttpResponse response = new DefaultHttpClient().execute(httpGet);
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, HTTP.UTF_8);
		}
		return result;
	}

	public static String getResultForHttpPost(String url, String json)
			throws ParseException, IOException {
		Log.e("zmark_httppost", "Start_HttpPost:" + url + " json:" + json);
		// Url：http://ip:3002/mobile_info,
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		String response = null;
		StringEntity stringEntity = new StringEntity(json);
		stringEntity.setContentType("application/json");
		post.setEntity(stringEntity);
		HttpResponse res = client.execute(post);

		if (res.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = res.getEntity();
			response = EntityUtils.toString(entity, HTTP.UTF_8);
		}
		Log.e("zmark_httppost", "statusCode:"
				+ res.getStatusLine().getStatusCode() + " response:" + response);
		return response;
	}
}
