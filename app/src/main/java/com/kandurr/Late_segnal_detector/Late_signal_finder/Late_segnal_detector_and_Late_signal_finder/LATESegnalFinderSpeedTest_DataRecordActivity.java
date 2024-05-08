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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

public class LATESegnalFinderSpeedTest_DataRecordActivity extends AppCompatActivity {

    public static final String DATA_READINGS_KEY = "data_readings_key";
    public static final String OFFSET_KEY = "offset_key";

    private static final String TAG = LATESegnalFinderSpeedTest_DataRecordActivity.class.getSimpleName();
    private static final Object MUTEX = new Object();

    private Button mPauseRecordButton,uncertainitybutton,stopbutton;
    private ImageView mRecordingImage;
    private TextView mRecordingImageLabel;
    private AlphaAnimation mRecordingImageAnimation;
    private SignalStrengthListener mSignalStrengthListener;
    private TextView mRsrpText, mRsrqText, mPciText, mDataPointsText, mOffsetText, mSignalStrengthText;
    private LATESegnalFinderSpeedTest_ReadData mCurrentReading;
    private double mOffset;
    private Timer mTimer;
    private List<LATESegnalFinderSpeedTest_ReadData> mSpeedTestReadData;
    private AtomicInteger mTicksSinceLastCellInfoUpdate;
    private AtomicBoolean mCellInfoRefresh;

    AdView mAdView;
    InterstitialAd mInterstitialAd;
    int int_uncertainityBtn, int_clickSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedtest_activity_record);

        uncertainitybutton=findViewById(R.id.button2);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                loadAd();
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mCurrentReading = new LATESegnalFinderSpeedTest_ReadData();
        mSpeedTestReadData = new ArrayList<>();

        mOffset = getIntent().getDoubleExtra(LATESignalFinderDetection_ActivityRecordNew.OFFSET_KEY, 0.0);

        mPauseRecordButton = findViewById(R.id.activity_record_pause_resume_button_ui);
        mRecordingImage = findViewById(R.id.activity_record_record_image_ui);
        mRecordingImageLabel = findViewById(R.id.activity_record_record_image_label_ui);
        mRsrpText = findViewById(R.id.activity_record_lte_rsrp_text_ui);
        mRsrqText = findViewById(R.id.activity_record_lte_rsrq_text_ui);
        mPciText = findViewById(R.id.activity_record_lte_pci_text_ui);
        mDataPointsText = findViewById(R.id.activity_record_data_points_text_ui);
        mOffsetText = findViewById(R.id.activity_record_offset_text_ui);
        mSignalStrengthText = findViewById(R.id.activity_record_signal_strength_text_ui);
        stopbutton=findViewById(R.id.activity_record_stop_button_ui);

        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LATESignalFinderActivityShowResutl.class);
                intent.putExtra(DATA_READINGS_KEY, (ArrayList<LATESegnalFinderSpeedTest_ReadData>) mSpeedTestReadData);
                intent.putExtra(OFFSET_KEY, mOffset);
                startActivity(intent);
                finish();


            }
        });

        uncertainitybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int_uncertainityBtn = 1;

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(LATESegnalFinderSpeedTest_DataRecordActivity.this);
                } else {

                    Intent intent = new Intent(getApplicationContext(), LATESignalFinderActivityNoticeUncertainty.class);
                    startActivity(intent);

                }

            }
        });

        mPauseRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording()) {
                    setPauseRecordingState();

                }
                else {
                    setResumeRecordingState();

                }
            }
        });

        // Make part of text clickable.
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View view) {

                int_clickSpan = 2;


                if (mInterstitialAd != null) {
                    mInterstitialAd.show(LATESegnalFinderSpeedTest_DataRecordActivity.this);
                } else {

                    Intent intent = new Intent(LATESegnalFinderSpeedTest_DataRecordActivity.this, LATESignalFinderActivityNoticeUncertainty.class);
                    startActivity(intent);

                }

            }

            @Override
            public void updateDrawState(@NonNull TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setColor(getResources().getColor(R.color.activity_record_clickable_color));
            }
        };

        TextView rsrpLabel = findViewById(R.id.activity_record_lte_rsrp_label_ui);
        SpannableString rsrpSpan = new SpannableString(getString(R.string.activity_record_lte_rsrp_label_text));
        rsrpSpan.setSpan(clickableSpan, rsrpSpan.length() - 6, rsrpSpan.length(), 0);
        rsrpLabel.setMovementMethod(LinkMovementMethod.getInstance());
        rsrpLabel.setText(rsrpSpan);

        TextView rsrqLabel = findViewById(R.id.activity_record_lte_rsrq_label_ui);
        SpannableString rsrqSpan = new SpannableString(getString(R.string.activity_record_lte_rsrq_label_text));
        rsrqSpan.setSpan(clickableSpan, rsrqLabel.length() - 6, rsrqLabel.length(), 0);
        rsrqLabel.setMovementMethod(LinkMovementMethod.getInstance());
        rsrqLabel.setText(rsrqSpan);

        mRecordingImageAnimation = new AlphaAnimation(1, 0);
        mRecordingImageAnimation.setDuration(750);
        mRecordingImageAnimation.setInterpolator(new LinearInterpolator());
        mRecordingImageAnimation.setRepeatCount(Animation.INFINITE);
        mRecordingImageAnimation.setRepeatMode(Animation.REVERSE);

        mSignalStrengthListener = new SignalStrengthListener();

        // Specifically for AndroidX and higher. Should call a refresh of cell info, otherwise there might be stale data.
        // See getAllCellInfo() docs from TelephonyManager.
        mTicksSinceLastCellInfoUpdate = new AtomicInteger(0);
        mCellInfoRefresh = new AtomicBoolean(true);

        // Now do the recording. We want to keep recording in the background so start and stop
        // in onCreate and onDestroy.
        ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(mSignalStrengthListener, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);
        setResumeRecordingState();

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {

                final LATESegnalFinderSpeedTest_ReadData speedTestReadDataCopy;
                synchronized (MUTEX) {

                    // To be used on the UI thread.
                    speedTestReadDataCopy = new LATESegnalFinderSpeedTest_ReadData(mCurrentReading);
                }

                try {
                    if (ActivityCompat.checkSelfPermission(LATESegnalFinderSpeedTest_DataRecordActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
                        if (allCellInfo != null) {
                            for (CellInfo cellInfo : allCellInfo) {
                                SpeedTest_LteLog.i(TAG, cellInfo.toString());
                                if (cellInfo.isRegistered() && cellInfo instanceof CellInfoLte) {
                                    CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                                    CellSignalStrengthLte signalStrengthLte = cellInfoLte.getCellSignalStrength();

                                    // Overwrite the SignalStrengthListener values if build is greater than 26 and only the rsrp if value less than 26.
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        speedTestReadDataCopy.setRsrp(signalStrengthLte.getRsrp());
                                        speedTestReadDataCopy.setRsrq(signalStrengthLte.getRsrq());
                                        SpeedTest_LteLog.i(TAG, "(VERSION >= 29) rsrp: " + signalStrengthLte.getRsrp() + ", rsrq: " + signalStrengthLte.getRsrq());
                                    }
                                    else {
                                        speedTestReadDataCopy.setRsrp(signalStrengthLte.getDbm()); // dbm = rsrp for values less than build 26.
                                        SpeedTest_LteLog.i(TAG, String.format(Locale.getDefault(), "(VERSION < 29) rsrp: %d", signalStrengthLte.getDbm()));
                                    }

                                    // Now get the pci.
                                    CellIdentityLte identityLte = cellInfoLte.getCellIdentity();
                                    if (identityLte != null) {
                                        int pci = identityLte.getPci();
                                        if (pci == LATESegnalFinderSpeedTest_ReadData.UNAVAILABLE) {
                                            pci = -1;
                                        }
                                        speedTestReadDataCopy.setPci(pci);
                                    }
                                }
                            }
                        }

                        // Anything larger than AndroidX, call for a refresh of the cell info.
                        // See getAllCellInfo() docs from TelephonyManager.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && mTicksSinceLastCellInfoUpdate.getAndIncrement() >= 10 && mCellInfoRefresh.get()) {

                            SpeedTest_LteLog.i(TAG,"Requesting refreshed cell info on AndroidX or higher");
                            mCellInfoRefresh.set(false);
                            telephonyManager.requestCellInfoUpdate(getMainExecutor(), new TelephonyManager.CellInfoCallback() {

                                @Override
                                public void onCellInfo(List<CellInfo> cellInfos) {
                                    SpeedTest_LteLog.i(TAG,"Getting refreshed cell info on AndroidX or higher");
                                    mTicksSinceLastCellInfoUpdate.set(0);
                                    mCellInfoRefresh.set(true);
                                }
                            });
                        }
                    }
                }
                catch (Exception caught) {
                    SpeedTest_LteLog.e(TAG, caught.getMessage(), caught);
                }

                // Adjust the rsrp;
                if (speedTestReadDataCopy.getRsrp() == LATESegnalFinderSpeedTest_ReadData.UNAVAILABLE) {
                    speedTestReadDataCopy.setRsrp(LATESegnalFinderSpeedTest_ReadData.LOW_RSRP);
                }
                else {
                    speedTestReadDataCopy.setRsrp(speedTestReadDataCopy.getRsrp() + (int) mOffset);
                }

                // Adjust the rsrq.
                if (speedTestReadDataCopy.getRsrq() == LATESegnalFinderSpeedTest_ReadData.UNAVAILABLE) {
                    speedTestReadDataCopy.setRsrq(LATESegnalFinderSpeedTest_ReadData.LOW_RSRQ);
                }

                if (isRecording()) {
                    mSpeedTestReadData.add(new LATESegnalFinderSpeedTest_ReadData(speedTestReadDataCopy));
                }

                // To be used on the UI thread.
                final int numDataReadings = mSpeedTestReadData.size() == 0 ? 1 : mSpeedTestReadData.size();

                runOnUiThread(() -> {
                    if (isRecording()) {
                        if (speedTestReadDataCopy.getRsrp() >= LATESegnalFinderSpeedTest_ReadData.EXECELLENT_RSRP_THRESHOLD) {
                            mSignalStrengthText.setText(getResources().getString(R.string.activity_record_signal_strength_excellent));
                        }
                        else if (LATESegnalFinderSpeedTest_ReadData.EXECELLENT_RSRP_THRESHOLD > speedTestReadDataCopy.getRsrp() && speedTestReadDataCopy.getRsrp() >= LATESegnalFinderSpeedTest_ReadData.GOOD_RSRP_THRESHOLD) {
                            mSignalStrengthText.setText(getResources().getString(R.string.activity_record_signal_strength_good));
                        }
                        else if (LATESegnalFinderSpeedTest_ReadData.GOOD_RSRP_THRESHOLD > speedTestReadDataCopy.getRsrp() && speedTestReadDataCopy.getRsrp() >= LATESegnalFinderSpeedTest_ReadData.POOR_RSRP_THRESHOLD) {
                            mSignalStrengthText.setText(getResources().getString(R.string.activity_record_signal_strength_poor));
                        }
                        else {
                            mSignalStrengthText.setText(getResources().getString(R.string.activity_record_signal_strength_no_signal));
                        }
                        mRsrpText.setText(String.format(Locale.getDefault(), "%d", speedTestReadDataCopy.getRsrp()));
                        mRsrqText.setText(String.format(Locale.getDefault(), "%d", speedTestReadDataCopy.getRsrq()));
                        mPciText.setText(speedTestReadDataCopy.getPci() == -1 ? "N/A" : String.format(Locale.getDefault(), "%d", speedTestReadDataCopy.getPci()));
                        mDataPointsText.setText(String.format(Locale.getDefault(), "%d", numDataReadings));
                        mOffsetText.setText(String.format(Locale.getDefault(), "%.1f", mOffset));
                    }
                });
            }
        }, 1000, 1000);
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

                        if (int_uncertainityBtn == 1){

                            Intent intent = new Intent(getApplicationContext(), LATESignalFinderActivityNoticeUncertainty.class);
                            startActivity(intent);

                        } else if (int_clickSpan == 2) {

                            Intent intent = new Intent(LATESegnalFinderSpeedTest_DataRecordActivity.this, LATESignalFinderActivityNoticeUncertainty.class);
                            startActivity(intent);

                        } else {

                            Toast.makeText(LATESegnalFinderSpeedTest_DataRecordActivity.this, "Something Issue!!!", Toast.LENGTH_SHORT).show();

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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mSignalStrengthListener != null) {
                ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(mSignalStrengthListener, SignalStrengthListener.LISTEN_NONE);
            }
        }
        catch (Exception caught) {
            SpeedTest_LteLog.e(TAG, caught.getMessage(), caught);
        }

        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    @Override
    public void onBackPressed() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Do you want to cancel recording? All data will be lost.")
                .setPositiveButton("YES", (dialog, which) -> finish())
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .create();
        alert.show();
    }







    private boolean isRecording() {
        return getString(R.string.activity_record_pause_button_text).equals(mPauseRecordButton.getText().toString());
    }

    private void setPauseRecordingState() {
        mPauseRecordButton.setText(getString(R.string.activity_record_resume_button_text));
        mPauseRecordButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_new_recording), null, null, null);
        mRecordingImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_paused));
        mRecordingImageLabel.setText(R.string.activity_record_recording_paused_image_label_text);
        mRecordingImage.clearAnimation();
    }

    private void setResumeRecordingState() {
        mPauseRecordButton.setText(getString(R.string.activity_record_pause_button_text));
        mPauseRecordButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_pause), null, null, null);
        mRecordingImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording));
        mRecordingImageLabel.setText(R.string.activity_record_record_image_label_text);
        mRecordingImage.startAnimation(mRecordingImageAnimation);
    }

    private class SignalStrengthListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            SpeedTest_LteLog.i(TAG, "onSignalStrengthsChanged: " + signalStrength.toString());

            // AndroidX changed the format of the signal strength.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                String[] values = signalStrength.toString().split(" ");
                if (values != null && values.length > 12) {
                    try {
                        int rsrp = Integer.parseInt(values[9]);
                        int rsrq = Integer.parseInt(values[10]);
                        synchronized (MUTEX) {
                            mCurrentReading.setRsrp(rsrp);
                            mCurrentReading.setRsrq(rsrq);
                            mCurrentReading.setPci(LATESegnalFinderSpeedTest_ReadData.PCI_NA);
                        }
                        SpeedTest_LteLog.i(TAG, String.format(Locale.getDefault(), "rsrp: %d, rsrq: %d", rsrp, rsrq));
                    }
                    catch (Exception caught) {
                        SpeedTest_LteLog.i(TAG, "Caught an error parsing signal strength");
                    }
                }
            }
            else {
                List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
                if (cellSignalStrengths != null) {
                    for (CellSignalStrength cellSignalStrength : cellSignalStrengths) {
                        if (cellSignalStrength instanceof CellSignalStrengthLte) {
                            synchronized (MUTEX) {
                                mCurrentReading.setRsrp(((CellSignalStrengthLte) cellSignalStrength).getRsrp());
                                mCurrentReading.setRsrq(((CellSignalStrengthLte) cellSignalStrength).getRsrq());
                            }
                        }
                    }
                }
            }

            super.onSignalStrengthsChanged(signalStrength);
        }
    }
}
