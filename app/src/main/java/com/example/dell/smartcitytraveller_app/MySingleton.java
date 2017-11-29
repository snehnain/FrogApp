package com.example.dell.smartcitytraveller_app;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Dell on 24-11-2017.
 */

public class MySingleton {
    private static MySingleton mySingleton;
    private RequestQueue requestQueue;
    private static Context  mctx;
    private MySingleton(Context context){
        mctx=context;
        requestQueue=getRequestQueue();

    }

    public RequestQueue getRequestQueue(){
        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }
    public static synchronized MySingleton getInstance(Context context ){
        if(mySingleton==null){
            mySingleton= new MySingleton(context);
        }
        return mySingleton;
    }
    public <T>void addRequestQueue(Request<T> request){
        requestQueue.add(request);
    }

}
