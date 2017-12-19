package com.example.dell.smartcitytraveller_app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SelectInterestActivity extends AppCompatActivity {

    /*
   Variable Declaration
    */
    private RadioButton edradio_monuments, edradio_events, edradio_resturants;
    //private RadioGroup edradio_types;
    private Button btn_Submit,btn_Logout;
    private TextView edwelcomeusername;
    String selectedInterest;
    private int id;

    String url="http://192.168.1.15:8080/MobileWebService/ActivityServlet";
     //String url="http://172.16.237.168:8080/MobileWebService/ActivityServlet";

    // Variable to use when a reference to the current activity is needed as a parameter in a nested method (in this case "this" does not refer to the current activity)
    Activity thisActivity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_interest);
        //Getting values from edit text and buttons
        //edradio_types = (RadioGroup) findViewById(R.id.radio_types);
        edradio_monuments = (RadioButton) findViewById(R.id.radio_monuments);
        edradio_events = (RadioButton) findViewById(R.id.radio_events);
        edradio_resturants = (RadioButton) findViewById(R.id.radio_resturants);
        edwelcomeusername = (TextView) findViewById(R.id.greetings);
        Intent intent = getIntent();
        edwelcomeusername.setText("Welcome "+ intent.getStringExtra(LoginActivity.Key_username));
        id= Integer.parseInt(intent.getStringExtra(SelectTimeActivity.Key_idTime));
        btn_Submit=(Button) findViewById(R.id.btnSubmit);
        btn_Logout=(Button) findViewById(R.id.btnLogout);
        //End of getting values from edit text and buttons

        //btn_Submit.setOnClickListener(this);
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onRadioButtonClicked(view);
                if (edradio_monuments.isChecked()) {
                    edradio_monuments.setChecked(true);
                    selectedInterest = edradio_monuments.getText().toString();
                } else if (edradio_events.isChecked()) {
                    edradio_events.setChecked(true);
                    selectedInterest = edradio_events.getText().toString();
                } else if (edradio_resturants.isChecked()) {
                    edradio_resturants.setChecked(true);
                    selectedInterest = edradio_resturants.getText().toString();
                }
                displayToast(selectedInterest);
                sendData(selectedInterest,id);
            }
        });
        btn_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectInterestActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }



    /*public void onRadioButtonClicked(View view) {
        //int interest=0 ;
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (edradio_types.getCheckedRadioButtonId()) {
            case R.id.radio_monuments:
                if (checked)
                    edradio_monuments.setChecked(true);

                    displayToast("monuments are checked");
                // Monuments are the best
                    //interest=1;
                    break;
            case R.id.radio_events:
                if (checked)
                    edradio_events.setChecked(true);

                    displayToast("events are checked");
                // Monuments are the best
                //interest=1;
                break;
            case R.id.radio_resturants:
                if (checked)
                    edradio_resturants.setChecked(true);

                    displayToast("restaurants are checked");
                // Monuments are the best
                //interest=1;
                break;
        }
        //sendData(interest);

        /*switch (view.getId()) {
            case R.id.radio_monuments:
                if (checked)
                    interest=edradio_monuments.toString();
                    displayToast("monuments are checked");
                    // Monuments are the best
                    break;
            case R.id.radio_events:
                if (checked)
                    interest=edradio_events.toString();
                    displayToast("events are checked");
                    // Events rule
                    break;
            case R.id.radio_resturants:
                if (checked)
                    interest=edradio_resturants.toString();
                    displayToast("resturants are checked");
                    // Restaurants rule
                    break;
        }
    }*/

    private void sendData(final String selectedInterest, final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    //New user added. Now either start activity to login or your main activity
                    displayToast("Interest Working fine");

                    /** **************************************
                     *  The following lines correspond to the launch of the map class
                     * **************************************
                     */
                    Intent nMap = new Intent(thisActivity, MapActivity.class); // Creating a intent for moving to
                    // We'll suppose that the "kms accessible around the user" equals 2 kms as it finally doesn't limit
                    // the results of a request in the search bar ( the places around the used will only be listed in priority
                    // in the results of a request)
                    double accessibleKmsAroundUser = 2;
                    nMap.putExtra("accessibleKms", accessibleKmsAroundUser);
                    //Also sending informations about the id of the user, and his preferences
                    nMap.putExtra("id", id);
                    nMap.putExtra("monuments", edradio_monuments.isChecked());
                    nMap.putExtra("restaurants", edradio_resturants.isChecked());
                    startActivity(nMap);

                }else {
                    //Wrong credentials according to your server logic. Prompt to re-enter the credentials
                    displayToast("Something went wrong. Please try again!");
                }
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String > getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<String, String>();
                params.put("id", String.valueOf(id));
                params.put("selectedInterest",selectedInterest);
                return params;
            }
        };
        MySingleton.getInstance(SelectInterestActivity.this).addRequestQueue(stringRequest);
    }

    /*
    Method is used to display the message in proper manner
     */
    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

    }
}
