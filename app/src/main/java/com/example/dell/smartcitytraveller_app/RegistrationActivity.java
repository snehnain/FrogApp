package com.example.dell.smartcitytraveller_app;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    Variable Declaration
     */
    private EditText edname, edemail, edpassword, edconfirmpassword, edmobile, eddob;
    private Button btn_Linktologin, btn_Register;

    //String username,password,confirmpassword,email,dob,mobile;
    //IP found with ipconfig
    String url="http://172.16.235.204:8080/FrogDevService/RegisterationServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Getting values from edit text and buttons
        edname = (EditText) findViewById(R.id.name);
        edpassword = (EditText) findViewById(R.id.password);
        edconfirmpassword = (EditText) findViewById(R.id.confirmpassword);
        edemail = (EditText) findViewById(R.id.email);
        edmobile = (EditText) findViewById(R.id.mobile);
        eddob = (EditText) findViewById(R.id.dob);
        btn_Linktologin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        btn_Register =(Button) findViewById(R.id.btnRegister);
        //End of getting values from edit text and buttons

        btn_Linktologin.setOnClickListener(this);
        btn_Register.setOnClickListener(this);
    }

    /*
     Listen to the button activity and perform the operation accordingly
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnLinkToLoginScreen:
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.btnRegister:
                register();
                break;
            default:
        }

    }

    /*
    Register function to get all the information and check whether the field is empty or not
     */
    private void register() {
        String username=edname.getText().toString();
        String password=edpassword.getText().toString();
        String confirmpassword=edconfirmpassword.getText().toString();
        String email=edemail.getText().toString();
        String dob=eddob.getText().toString();
        String mobileno=edmobile.getText().toString();
        if(username.isEmpty()&&password.isEmpty()&&confirmpassword.isEmpty()&&email.isEmpty()&&dob.isEmpty()&&mobileno.isEmpty()){
            displayToast("Fields are empty. Please enter your details.");
        }else{
            if(!(password.equals(confirmpassword))){
                displayToast("Passwords are different");
            }else{
                displayToast("Credentials are correct");
                sendData(username,password,confirmpassword,email,dob,mobileno);
                displayToast("User Registered");
                finish();

            }
            /*new DataRegistration().getdata(username,password,confirmpassword,email,dob,mobile);
            */
        }

    }

    private void sendData(final String username, final String password, final String confirmpassword, final String email, final String dob, final String mobileno) {
        //RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    //New user added. Now either start activity to login or your main activity
                    Toast.makeText(RegistrationActivity.this, "New User Created.", Toast.LENGTH_SHORT).show();
                    //TODO: start the new activity of your app
                }else{
                    //Wrong credentials according to your server logic. Prompt to re-enter the credentials
                    displayToast("Something went wrong. Please try again!");
                }
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    displayToast(code);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String > getParams() throws AuthFailureError{
                Map<String,String> params =new HashMap<String, String>();
                params.put("username",username);
                params.put("email",email);
                params.put("password",password);
                params.put("confirmpassword",confirmpassword);
                params.put("dob",dob);
                params.put("mobile",mobileno);
                return params;
            }
        };
        MySingleton.getInstance(RegistrationActivity.this).addRequestQueue(stringRequest);
    }

    /*
    Method is used to display the message in proper manner
     */
    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

    }
}
