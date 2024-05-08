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

import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.kandurr.Late_segnal_detector.Late_signal_finder.Late_segnal_detector_and_Late_signal_finder.util.SpeedTest_LteLog;

public class LATESignalFinderActivityShowResutl extends AppCompatActivity {

    private ArrayList<LATESegnalFinderSpeedTest_ReadData> mSpeedTestReadData;
    private double mOffset;
    private String mCsvFilename;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedtest_activity_display_results);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mSpeedTestReadData = (ArrayList<LATESegnalFinderSpeedTest_ReadData>) getIntent().getSerializableExtra(LATESegnalFinderSpeedTest_DataRecordActivity.DATA_READINGS_KEY);
        mOffset = getIntent().getDoubleExtra(LATESegnalFinderSpeedTest_DataRecordActivity.OFFSET_KEY, 0.0);

        mCsvFilename = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        mCsvFilename = mCsvFilename.replace(' ', '_').replace(",", "");

        ViewPager viewPager = findViewById(R.id.activity_display_results_view_pager_ui);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(LATESegnalFinderSpeedTest_FragmentGrade.newInstance(mSpeedTestReadData, mCsvFilename), getString(R.string.activity_display_results_grade_tab_text));
        adapter.addFragment(LATESegnalFinderSpeedTest_FragmentLineChart.newInstance(mSpeedTestReadData, mCsvFilename), getString(R.string.activity_display_results_line_chart_tab_text));
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.activity_display_results_tab_layout_ui);
        tabLayout.setupWithViewPager(viewPager);
        writeCsv();
    }

    protected void writeCsv() {

        // Write CSV file in a background thread.
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Writer writer = null;
                try {
                    writer = new OutputStreamWriter(new FileOutputStream(new File(getExternalFilesDir(null), mCsvFilename + ".csv")), StandardCharsets.UTF_8);
                    writer.write("\"Time\",\"RSRP\",\"RSRQ\",\"PCI\",\"OFFSET=" + mOffset + "\"\n");
                    for (LATESegnalFinderSpeedTest_ReadData speedTestReadData : mSpeedTestReadData) {
                        String timestamp = DateFormat.getDateTimeInstance().format(speedTestReadData.getTimestamp());
                        writer.write(String.format("\"%s\",\"%d\",\"%d\",\"%s\"%n", timestamp, speedTestReadData.getRsrp(), speedTestReadData.getRsrq(), speedTestReadData.getPci() == -1 ? "N/A" : speedTestReadData.getPci() + ""));
                    }
                    Toast.makeText(LATESignalFinderActivityShowResutl.this, "CSV file written", Toast.LENGTH_SHORT).show();

                }
                catch (IOException caught) {
                    SpeedTest_LteLog.e("CSV Writer", caught.getMessage(), caught);
                    Toast.makeText(LATESignalFinderActivityShowResutl.this, "Error writing CSV file", Toast.LENGTH_SHORT).show();
                }
                finally {
                    if (writer != null) {
                        try {
                            writer.flush();
                            writer.close();
                        }
                        catch (IOException ignore) {}
                    }
                }
            }
        }, 0);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments;
        private final List<String> mFragmentTitles;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            mFragments = new ArrayList<>();
            mFragmentTitles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        private void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }
    }
}
