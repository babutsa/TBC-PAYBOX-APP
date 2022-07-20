package ge.casatrade.tbcpay;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ge.casatrade.tbcpay.Models.BasicInfo;
import ge.casatrade.tbcpay.Models.TrackerData;
import ge.casatrade.tbcpay.Requests.Parser;
import ge.casatrade.tbcpay.Requests.RequestBuilder;
import ge.casatrade.tbcpay.fragments.BasicInfoFragment;
import ge.casatrade.tbcpay.fragments.EnterPhoneFragment;
import ge.casatrade.tbcpay.fragments.EventsFragment;
import ge.casatrade.tbcpay.fragments.ImeiEnterMethodFragment;
import ge.casatrade.tbcpay.fragments.ManualImeiFragment;
import ge.casatrade.tbcpay.fragments.TerminalListFragment;


public class InstallationActivity extends AppCompatActivity implements TerminalListFragment.TerminalListListener, ImeiEnterMethodFragment.OnShouldScanListener,
        ManualImeiFragment.ManualImeiInteractivityFragment, BasicInfoFragment.OnBasicNextBtnClicked, EventsFragment.EventsFragmentListener, EnterPhoneFragment.OnEnterPhoneFragment {

    private View fragmentHolder;
    private TerminalListFragment terminalListFragment;

    private SimpleDateFormat simpleDate;
    private BasicInfo selectedTerminal;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location lastLocation;
    private String searchMethod = "imei";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation);


        simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        fragmentHolder = findViewById(R.id.fragment_holder);
        if (savedInstanceState == null) {
            startTerminalListFragment();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            lastLocation = location;
                        }
                    });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
        }

        return true;
    }


    private void logout(){
        PrefferenceManager.clear(this);
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    private void startTerminalListFragment() {
        terminalListFragment = TerminalListFragment.newInstance();

        if (android.os.Build.VERSION.SDK_INT > 21) {
            Slide slide = new Slide(Gravity.START);
            slide.setDuration(200);

            Fade fade = new Fade();
            fade.setDuration(200);
            terminalListFragment.setEnterTransition(slide);
            terminalListFragment.setExitTransition(fade);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment_holder, terminalListFragment, "fragment_scan_picker")
                .commit();
    }


    @Override
    public void onTerminalPicked(BasicInfo info) {
        if (info.isTerminalComplete()) {
            displayError(R.string.error, R.string.terminal_already_assigned);
        }

        handleTerminalPicked(info);

    }

    private void handleTerminalPicked(BasicInfo terminal) {
        this.selectedTerminal = terminal;
        if(!terminal.isTerminalComplete()) {
            startImeiEnterMethodFragment();
        }else{
            startEventsFragment();
        }
    }

    private void startPhoneEnterFragment(){
        EnterPhoneFragment manualImeiFragment = EnterPhoneFragment.newInstance();

        if (android.os.Build.VERSION.SDK_INT > 21) {
            Slide slide = new Slide(Gravity.START);
            slide.setDuration(200);

            Fade fade = new Fade();
            fade.setDuration(200);
            manualImeiFragment.setEnterTransition(slide);
            manualImeiFragment.setExitTransition(fade);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_holder, manualImeiFragment, "fragment_basic_info")
                .addToBackStack(null)
                .commit();
    }

    private void startImeiEnterMethodFragment(){
        ImeiEnterMethodFragment manualImeiFragment = ImeiEnterMethodFragment.newInstance();

        if (android.os.Build.VERSION.SDK_INT > 21) {
            Slide slide = new Slide(Gravity.START);
            slide.setDuration(200);

            Fade fade = new Fade();
            fade.setDuration(200);
            manualImeiFragment.setEnterTransition(slide);
            manualImeiFragment.setExitTransition(fade);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_holder, manualImeiFragment, "fragment_basic_info")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onShouldScanClicked(boolean shouldScan, boolean isImeiSelected) {
        this.searchMethod = (isImeiSelected ? "imei" : "serial");

        if (shouldScan) {
            Intent intent = new Intent(this, BarcodeActivity.class);
            startActivityForResult(intent, Constants.REQUEST_START_SCAN);
        } else {
            if (getIntent().getExtras() == null) {
                ManualImeiFragment manualImeiFragment = ManualImeiFragment.newInstance(selectedTerminal, searchMethod);

                if (android.os.Build.VERSION.SDK_INT > 21) {
                    Slide slide = new Slide(Gravity.START);
                    slide.setDuration(200);

                    Fade fade = new Fade();
                    fade.setDuration(200);
                    manualImeiFragment.setEnterTransition(slide);
                    manualImeiFragment.setExitTransition(fade);
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_holder, manualImeiFragment, "fragment_basic_info")
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    @Override
    public void onPhoneEntered(String phone) {
        startImeiEnterMethodFragment();
    }


    @Override
    public void onManuallyFoundImeiClicked(TrackerData data) {
        if (data.isComplete() || selectedTerminal.isTerminalComplete()) {
            displayError(R.string.error, R.string.terminal_already_assigned);
            startEventsFragment();
            return;
        }

        selectedTerminal.setImei(data.getImei());
        selectedTerminal.setSerial(data.getSerial());
        selectedTerminal.setPhone(data.getPhone());

        if (getIntent().getExtras() == null) {
            BasicInfoFragment basicInfoFragment = BasicInfoFragment.newInstance(selectedTerminal);
            if (android.os.Build.VERSION.SDK_INT > 21) {
                Slide slide = new Slide(Gravity.START);
                slide.setDuration(200);

                Fade fade = new Fade();
                fade.setDuration(200);
                basicInfoFragment.setEnterTransition(slide);
                basicInfoFragment.setExitTransition(fade);
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_holder, basicInfoFragment, "fragment_basic_info")
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBasicNextBtnClicked(BasicInfo lastInfo) {
        this.selectedTerminal.setPhone(lastInfo.getPhone());
        assignDevice(lastInfo.getImei(), lastInfo.getId(), lastInfo.getPhone());
    }


    @Override
    public void onFinishClicked() {
        uploadDevice();
    }

    @Override
    public void commandSuccess() {
        showSuccess(R.string.success, R.string.command_send_success);
    }

    @Override
    public void commandFailed() {
        displayError(R.string.error, R.string.command_send_error);
    }


    private void uploadDevice() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.uploading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("terminalid", selectedTerminal.getId());

        Log.i("TAG", "AT " + Constants.URL_UPLOAD);
        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i("Tag", "response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").toLowerCase().equals("ok")) {
                                showSuccess(R.string.success, R.string.uploaded_successfully);
                                startOver();
                            } else {
                                displayError(R.string.error, R.string.could_not_assign);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayError(R.string.server_error, R.string.could_not_assign);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        displayError(R.string.server_error, R.string.could_not_assign);
                    }
                });

        WebBridge.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void startOver() {
        terminalListFragment = TerminalListFragment.newInstance();

        if (android.os.Build.VERSION.SDK_INT > 21) {
            Slide slide = new Slide(Gravity.START);
            slide.setDuration(200);

            Fade fade = new Fade();
            fade.setDuration(200);
            terminalListFragment.setEnterTransition(slide);
            terminalListFragment.setExitTransition(fade);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_holder, terminalListFragment, "fragment_scan_picker")
                .commit();
    }


    private void assignDevice(String imei, String deviceid, String phone) {


        Map<String, String> params = new HashMap<>();
        params.put("terminalid", deviceid);
        params.put("deviceid", imei);
        params.put("phone", phone);

        Log.i("TAG", "AT " + Constants.URL_ASSIGN_DEVICE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.assigning_device));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_ASSIGN_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i("Tag", "response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").toLowerCase().equals("ok")) {
                                //ToDO: Start Device Check View
                                showSuccess(R.string.success, R.string.assigned_successfully);
                                startEventsFragment();
                            } else {
                                if (jsonObject.getString("error").toLowerCase().equals("terminal exists")) {
                                    displayError(R.string.error, R.string.terminal_already_assigned);
                                } else {
                                    displayError(R.string.error, R.string.could_not_assign);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayError(R.string.server_error, R.string.could_not_assign);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        displayError(R.string.server_error, R.string.could_not_assign);
                    }
                });

        WebBridge.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void startEventsFragment() {
        if (getIntent().getExtras() == null) {
            EventsFragment basicInfoFragment = EventsFragment.newInstance(selectedTerminal);
            if (android.os.Build.VERSION.SDK_INT > 21) {
                Slide slide = new Slide(Gravity.START);
                slide.setDuration(200);

                Fade fade = new Fade();
                fade.setDuration(200);
                basicInfoFragment.setEnterTransition(slide);
                basicInfoFragment.setExitTransition(fade);
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_holder, basicInfoFragment, "fragment_basic_info")
                    .addToBackStack(null)
                    .commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                Log.i("TAG", "Scanned " + result);
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                getBasicInfo(result);


            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void getBasicInfo(final String id) {
        Map<String, String> params = new HashMap<>();
        params.put("term", id);
        params.put("method", searchMethod);

        Log.i("TAG", "AT " + Constants.URL_GET_TERMINALS_BY_IMEI);
        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_GET_TERMINALS_BY_IMEI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Tag", "response " + response);
                        handleInfoReceivedResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showScanFailedAlarm();
                    }
                });

        WebBridge.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void handleInfoReceivedResponse(String respone) {
        try {
            ArrayList<TrackerData> infos = Parser.getImeis(respone);
            if (infos.size() > 0) {
                TrackerData data = infos.get(0);
                if (!data.isComplete()) {
                    onManuallyFoundImeiClicked(data);
                } else {
                    displayError(R.string.error, R.string.terminal_already_assigned);
                }
            } else {
                showScanFailedAlarm();
            }
        } catch (JSONException ex) {
            showScanFailedAlarm();
        }
    }

    private void showSuccess(int title, int body) {
        Alerter.create(this).setTitle(title).setText(body).setDuration(1000 * 3).setBackgroundColor(R.color.colorComplete).show();
    }

    private void displayError(int title, int error) {
        Alerter.create(this).setTitle(title).setText(error).setDuration(1000 * 7).setBackgroundColor(R.color.bright_red).show();
    }

    private void displayError(String title, String error) {
        Alerter.create(this).setTitle(title).setText(error).setDuration(1000 * 7).setBackgroundColor(R.color.bright_red).show();
    }

    private void showScanFailedAlarm() {
        Alerter.create(this).setTitle(R.string.could_not_find_car).setText(R.string.could_not_find_car_with_given_imei).setDuration(1000 * 7).setBackgroundColor(R.color.bright_red).show();
    }


}
