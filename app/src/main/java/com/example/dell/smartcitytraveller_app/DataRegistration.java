package com.example.dell.smartcitytraveller_app;

import java.io.IOException;
import java.util.ArrayList;
import  java.util.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by Sneh on 22-11-2017.
 */

public class DataRegistration {

    LoginParser loginparser;
    String url="http://localhost:8080/MobileWebService/RegisterationServlet";


    public void getdata(String username,String password,String confirmpassword,String email,String dob,String mobile ){

        List<NameValuePair> list= new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("username",username));
        list.add(new BasicNameValuePair("password",password));
        list.add(new BasicNameValuePair("confirmpassword",confirmpassword));
        list.add(new BasicNameValuePair("email",email));
        list.add(new BasicNameValuePair("dob",dob));
        list.add(new BasicNameValuePair("mobile",mobile));
        try {
            loginparser.loginParser(list,url);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
