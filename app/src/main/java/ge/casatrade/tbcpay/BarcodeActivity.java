package ge.casatrade.tbcpay;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);


        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setSoundEffectsEnabled(true);
        mScannerView.setAutoFocus(true);

        setContentView(mScannerView);                // Set the scanner view as the content view

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkIfAlreadyhavePermission()) {
                mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                mScannerView.startCamera();          // Start camera on resume
            }
        }else{
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();          // Start camera on resume
        }

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                    mScannerView.startCamera();          // Start camera on resume
                } else {
                    finish();
                }
            }
        }
    }


    @Override
    public void handleResult(Result result) {
        // Do something with the result here
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", result.getText());
        returnIntent.putExtra("format", result.getBarcodeFormat().name());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
