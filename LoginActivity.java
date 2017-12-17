package com.example.dell.smartcitytraveller_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    /*
    Variable Declaration
     */
    private EditText edusername, edpassword;
    private Button btn_Login, btn_Register, btn_ForgotPassword;
    public static final String Key_username="username";
    public static final String Key_id="id";

    String url="http://192.168.1.15:8080/MobileWebService/LoginServlet";
    //String url="http://172.16.237.168:8080/MobileWebService/LoginServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Getting values from edit text and buttons
        edusername = (EditText) findViewById(R.id.username);
        edpassword = (EditText) findViewById(R.id.password);
        btn_Login=(Button) findViewById(R.id.btnLogin);
        btn_ForgotPassword=(Button) findViewById(R.id.btnForgotpassword);
        btn_Register=(Button) findViewById(R.id.btnRegister);
        //End of getting values from edit text and buttons

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i1);
            }
        });
        btn_ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i2);
            }
        });
    }


    /*public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnLogin:
                login();
            case R.id.btnRegister:
                Intent i1 = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i1);
                finish();
                break;
            case R.id.btnForgotpassword:
                Intent i2 = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i2);
                finish();
                break;
            default:
        }

    }*/

    private void login() {
        String username=edusername.getText().toString();
        String password=edpassword.getText().toString();
        if(username.isEmpty()||  password.isEmpty()){
            displayToast("Fields are empty. Please enter your details.");
        }else{
            displayToast("Checking credentials whether user exist or not");
            checkLoginData(username,password);
            //displayToast("Welcome" + username);

        }
    }

    private void checkLoginData(final String username, final String password) {
        /*JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Intent intent = new Intent(LoginActivity.this, SelectTimeActivity.class);
                intent.putExtra("username", username);
                //intent.putExtra(TYPES_OF_RESOURCE_EXTRA, resourceString);
                //intent.putExtra(RESPONSE_EXTRA, response.toString());
                startActivity(intent);
            }*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                displayToast(response);
                if(response.equals("unsuccess")){
                    Toast.makeText(LoginActivity.this,"Credentials are not correct. Please try again.",Toast.LENGTH_LONG).show();

                }else{
                    Intent intent = new Intent(LoginActivity.this,SelectTimeActivity.class);
                    intent.putExtra("id",response);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Error while fetching data")
                        .setMessage("Something wrong happened while trying to get data from the web service.\n" +
                                "See the following error message:" + error.getLocalizedMessage())
                        .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                //Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String > getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<String, String>();
                params.put("username",username);
                params.put("password",password);
                return params;
            }
        };
        //MySingleton.getInstance(LoginActivity.this).addRequestQueue(jsonArrayRequest);
        MySingleton.getInstance(LoginActivity.this).addRequestQueue(stringRequest);
    }

    /*
   Method is used to display the message in proper manner
    */
    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

    }
}
