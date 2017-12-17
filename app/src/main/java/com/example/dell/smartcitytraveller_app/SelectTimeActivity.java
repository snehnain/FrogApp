package com.example.dell.smartcitytraveller_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SelectTimeActivity extends AppCompatActivity {

    /*
    Variable Declaration
     */
    private EditText edfromTime, edtoTime;
    private Button btn_Submit,btn_Logout;
    private TextView edwelcomeusername;
    private int id;
    private String username;
    public static final String Key_idUser="Userid";
    public static final String Key_idTime="Timeid";
    public static final String Key_username="username";

    String url="http://192.168.1.15:8080/MobileWebService/TimeServlet";
    //String url="http://172.16.237.168:8080/MobileWebService/TimeServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        //Getting values from edit text and buttons
        edfromTime = (EditText) findViewById(R.id.fromTime);
        //System.out.println(edfromTime);
        edtoTime = (EditText) findViewById(R.id.toTime);
        btn_Submit = (Button) findViewById(R.id.btnSubmit);
        btn_Logout = (Button) findViewById(R.id.btnLogout);
        edwelcomeusername = (TextView) findViewById(R.id.greetings);
        Intent intent = getIntent();
        edwelcomeusername.setText("Welcome "+ intent.getStringExtra(LoginActivity.Key_username));
        username=intent.getStringExtra(LoginActivity.Key_username);
        id= Integer.parseInt(intent.getStringExtra(LoginActivity.Key_id));

        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserttime();

            }
        });
        btn_Logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectTimeActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void inserttime() {
        String fromTime=edfromTime.getText().toString();
        //displayToast(fromTime);
        String toTime=edtoTime.getText().toString();
        if (fromTime.isEmpty()) {
            displayToast("Please enter time");
            return;
        }
        if( toTime.isEmpty()){
            displayToast("Please enter time");
            return;
        }
            sendData(fromTime,toTime,id);

    }

    private void sendData(final String fromTime, final String toTime, final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                if(response.equals("unsuccess")){
                    displayToast("Something went wrong. Please try again!");
                }else{
                    displayToast("Choose your things you want to do");
                    Intent i = new Intent(SelectTimeActivity.this, SelectInterestActivity.class);
                    i.putExtra("Timeid",response);
                    i.putExtra("Userid",id);
                    //intent.getStringExtra(LoginActivity.Key_username);
                    i.putExtra("username",username);
                    startActivity(i);
                }

            }
        }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
        }){
        @Override
        protected Map<String, String > getParams() throws AuthFailureError {
            Map<String,String> params =new HashMap<String, String>();
            params.put("id", String.valueOf(id));
            params.put("from",fromTime);
            params.put("to",toTime);
            return params;
        }
    };
        MySingleton.getInstance(SelectTimeActivity.this).addRequestQueue(stringRequest);
}

    /*
   Method is used to display the message in proper manner
    */
    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

    }
}
