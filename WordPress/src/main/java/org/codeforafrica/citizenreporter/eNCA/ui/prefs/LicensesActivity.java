package org.codeforafrica.citizenreporter.eNCA.ui.prefs;

import android.os.Bundle;

import org.codeforafrica.citizenreporter.eNCA.R;
import org.codeforafrica.citizenreporter.eNCA.ui.WebViewActivity;

/**
 * Display open source licenses for the application.
 */
public class LicensesActivity extends WebViewActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getText(R.string.open_source_licenses));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0.0f);
        }

        loadUrl("file:///android_asset/licenses.html");
    }
}
