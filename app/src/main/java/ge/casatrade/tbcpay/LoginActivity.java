package ge.casatrade.tbcpay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ge.casatrade.tbcpay.Requests.RequestBuilder;

public class LoginActivity extends AppCompatActivity {

    private EditText userTxt;
    private EditText passTxt;
    private ProgressDialog progressDialog;
    private static final String API_USER = "installer_api";
    private static final String API_PASS = "XMFe2TetzW2WDhxD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestBuilder.setParams(API_USER, API_PASS);
        RequestBuilder.setUserName(PrefferenceManager.getUserName(this));


        setContentView(R.layout.activity_login);
        userTxt = findViewById(R.id.email_text);
        passTxt = findViewById(R.id.password_text);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("სისტემაში შესვლა...");

        if (PrefferenceManager.getUserName(this) != null) {
            userTxt.setText(PrefferenceManager.getUserName(this));
            if (PrefferenceManager.getPassword(this) != null && PrefferenceManager.getUserName(this).length() > 0) {
                doLogin(PrefferenceManager.getUserName(this), PrefferenceManager.getPassword(this));
                //checkLoginValidity();


                //startActivity(new Intent(this, MainActivity.class));
                //finish();
            }
        }

    }

//    private void checkLoginValidity()
//    {
////        progressDialog.show();
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHECK_TOKEN,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        progressDialog.hide();
//                        // Do something with the response
//                        Log.i("Tag", response);
//                        JSONObject jObj;
//                        try {
//                            jObj = new JSONObject(response);
//                            String status = jObj.getString("status");
//
//                            if(status.equals("OK")){
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                finish();
//                            }else{
//                                Alerter.create(LoginActivity.this).setTitle("გთხოვთ ავტორიზაცია გაიაროთ ხელახლა").setDuration(1000*7).setBackgroundColor(R.color.colorPrimary).show();
//                                PrefferenceManager.clearToken(LoginActivity.this);
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // Handle error
//                        progressDialog.hide();
//                        showScanFailedAlarm();
//                    }
//                }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("username", userTxt.getText().toString());
//                params.put("token", PrefferenceManager.getToken(LoginActivity.this));
//                return params;
//            }
//        };
//
//        WebBridge.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
//    }

    public void startLogin(View v) {
        doLogin(userTxt.getText().toString(), passTxt.getText().toString());

    }

    private void doLogin(final String user, final String passwd) {
        progressDialog.show();

//        RequestBuilder.setUserName(user);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("username", user);
//        params.put("password", passwd);
//        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_LOGIN, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.i("Tag", response);
//                progressDialog.hide();
//                handlerLoginResponse(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("ERROR", "ERROR " + error.getLocalizedMessage());
//                progressDialog.hide();
//                showScanFailedAlarm();
//            }
//        });
//
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        // Do something with the response
                        Log.i("Tag", response);
                        handlerLoginResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.i("ERROR", "ERROR " + error.getLocalizedMessage());
                        progressDialog.hide();
                        showScanFailedAlarm();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", user);
                params.put("password", passwd);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                String credentials =  API_USER + ":" + API_PASS;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", auth);


                return params;
            }
        };

        WebBridge.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onDestroy() {
        progressDialog.dismiss();
        progressDialog = null;
        super.onDestroy();
    }

    private void showScanFailedAlarm() {
        Alerter.create(this).setTitle("მომხმარებლის სახელი ან პაროლი არასწორია").setDuration(1000 * 7).setBackgroundColor(R.color.colorPrimary).show();
    }

    private void handlerLoginResponse(String response) {
        try {
            JSONObject jObj = new JSONObject(response);
            String status = jObj.getString("status");

            if (status.equals("OK")) {
                PrefferenceManager.savePassword(LoginActivity.this, passTxt.getText().toString());
                PrefferenceManager.saveUserName(LoginActivity.this, userTxt.getText().toString());

                RequestBuilder.setUserName(userTxt.getText().toString());
                startActivity(new Intent(this, InstallationActivity.class));
                finish();
            } else {
                showScanFailedAlarm();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showScanFailedAlarm();
        }
    }
}

