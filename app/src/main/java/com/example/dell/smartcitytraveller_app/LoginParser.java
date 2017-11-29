package com.example.dell.smartcitytraveller_app;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Dell on 22-11-2017.
 */

class LoginParser {

    public static void loginParser(List<NameValuePair> list, String url) throws IOException {
            /*StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), response,Toast.LENGTH_LONG).show();

                }
            });*/
       try{ DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(list));
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(ClientProtocolException e){
            e.printStackTrace();
        } catch
         (IOException e) {
            e.printStackTrace();
        }
    }
}
