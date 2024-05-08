/*
 * This software was developed by employees of the National Institute of Standards and Technology (NIST), an agency of the Federal Government
 * and is being made available as a public service. Pursuant to title 17 United States Code Section 105, works of NIST employees are not
 * subject to copyright protection in the United States.  This software may be subject to foreign copyright.  Permission in the United States
 * and in foreign countries, to the extent that NIST may hold copyright, to use, copy, modify, create derivative works, and distribute
 * this software and its documentation without fee is hereby granted on a non-exclusive basis, provided that this notice and disclaimer of
 * warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO,
 * ANY WARRANTY THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND FREEDOM FROM INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL
 * BE ERROR FREE.  IN NO EVENT SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT, INDIRECT, SPECIAL OR CONSEQUENTIAL
 * DAMAGES, ARISING OUT OF, RESULTING FROM, OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY, CONTRACT, TORT, OR OTHERWISE,
 * WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT OF THE RESULTS OF,
 * OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */
package com.kandurr.Late_segnal_detector.Late_signal_finder.Late_segnal_detector_and_Late_signal_finder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.kandurr.Late_segnal_detector.Late_signal_finder.Late_segnal_detector_and_Late_signal_finder.util.SpeedTest_LteLog;

public class LATESignalFinderDetection_ActivityRecordNew extends AppCompatActivity {

    public static final String OFFSET_KEY = "offset_key";
    private static final String TAG = LATESignalFinderDetection_ActivityRecordNew.class.getSimpleName();
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION_START_ACTIVITY = 2;
    private EditText mOffsetUi;
    //private AdView adView,adViewone;
    Button exbutton;

    AdView mAdView;
    InterstitialAd mInterstitialAd;
    int int_exbuttonOne;

    int int_Count = 0;

    //private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedtest_activity_new_recording);
        //AudienceNetworkAds.initialize(this);
        exbutton=findViewById(R.id.button);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                loadAd();
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown


        mOffsetUi = findViewById(R.id.activity_new_recording_offset_ui);
        mOffsetUi.setText(String.format(Locale.getDefault(), "%.1f", 0.0));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);

        }
        exbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int_exbuttonOne = 1;

                int_Count++;

                if (int_Count == 2){

                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(LATESignalFinderDetection_ActivityRecordNew.this);
                    } else {


                        if (ContextCompat.checkSelfPermission(LATESignalFinderDetection_ActivityRecordNew.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(LATESignalFinderDetection_ActivityRecordNew.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION_START_ACTIVITY);
                        }
                        else {
                            startRecordingActivity();
                        }

                    }

                } else {

                    if (ContextCompat.checkSelfPermission(LATESignalFinderDetection_ActivityRecordNew.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LATESignalFinderDetection_ActivityRecordNew.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION_START_ACTIVITY);
                    }
                    else {
                        startRecordingActivity();
                    }

                }



            }
        });
    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        initializeNewAdsRequest();
    }

    private void initializeNewAdsRequest() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.AdMob_InterstitialAd_ID), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                //  Log.i(TAG, "onAdLoaded");

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {

                        //  Log.d("TAG", "The ad was dismissed.");
                        loadAd();

                        if (int_exbuttonOne == 1){

                            if (ContextCompat.checkSelfPermission(LATESignalFinderDetection_ActivityRecordNew.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(LATESignalFinderDetection_ActivityRecordNew.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION_START_ACTIVITY);
                            }
                            else {
                                startRecordingActivity();
                            }

                        } else {
                            Toast.makeText(LATESignalFinderDetection_ActivityRecordNew.this, "Something Issue!!!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }


            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                // Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION_START_ACTIVITY && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecordingActivity();
        }
    }

    private void startRecordingActivity() {
        Intent intent = new Intent(this, LATESegnalFinderSpeedTest_DataRecordActivity.class);
        double offset = 0.0;
        try {
            offset = Double.parseDouble(mOffsetUi.getText().toString().trim());
        }
        catch (Exception caught) {
            SpeedTest_LteLog.e(TAG, caught.getMessage(), caught);
        }
        intent.putExtra(OFFSET_KEY, offset);
        startActivity(intent);
    }
}
