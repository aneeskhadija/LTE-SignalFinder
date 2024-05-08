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

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class LATESignalFinderActivityDisclaimer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedtest_activity_disclaimer);
    }

    public void liabilityNoticeActivityButtonClicked(View view) {
        Intent intent = new Intent(this, LATESignalFinderActivityNoticeLiability.class);
        startActivity(intent);
    }

    public void prohibitionNoticeActivityButtonClicked(View view) {
        Intent intent = new Intent(this, LATESignalFinderAcknowledgementNoticeActivity.class);
        startActivity(intent);
    }

    public void acknowledgementNoticeActivityButtonClicked(View view) {
        Intent intent = new Intent(this, LATESegnalFinderActivityNoticeProhibition.class);
        startActivity(intent);
    }

    public void nistSoftwareActivityButtonClicked(View view) {
        Intent intent = new Intent(this, LATESignalFinderActivitySoftwareNist.class);
        startActivity(intent);
    }

    public void uncertaintyNoticeActivityButtonClicked(View view) {
        Intent intent = new Intent(this, LATESignalFinderActivityNoticeUncertainty.class);
        startActivity(intent);
    }

    public void exitButtonClicked(View view) {
        finish();
    }

    public void acceptButtonClicked(View view) {
        Intent intent = new Intent(this, LATESignalFinderDetection_ActivityRecordNew.class);
        startActivity(intent);
        finish();
    }
}
