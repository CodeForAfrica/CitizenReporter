package org.wordpress.passcodelock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.wordpress.passcodelock.R;

public abstract class AbstractPasscodeKeyboardActivity extends Activity {
    protected EditText mPinCodeField;
    protected InputFilter[] filters = null;
    protected TextView topMessage = null;
    private LinearLayout button_camera;
    private LinearLayout button_video;
    private LinearLayout button_mic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getResources().getBoolean(R.bool.allow_rotation)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.app_passcode_keyboard);
        
        topMessage = (TextView) findViewById(R.id.passcodelock_prompt);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String message = extras.getString("message");
            if (message != null) {
                topMessage.setText(message);
            }
        }
        
        filters = new InputFilter[2];
        filters[0]= new InputFilter.LengthFilter(1);
        filters[1] = onlyNumber;
        
        mPinCodeField = (EditText)findViewById(R.id.pin_field);
        
        //setup the keyboard
        findViewById(R.id.button0).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button1).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button2).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button3).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button4).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button5).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button6).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button7).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button8).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button9).setOnClickListener(defaultButtonListener);
        findViewById(R.id.button_erase).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String curText = mPinCodeField.getText().toString();

                        if (curText.length() > 0) {
                            mPinCodeField.setText(curText.substring(0, curText.length() - 1));
                            mPinCodeField.setSelection(mPinCodeField.length());
                        }
                    }
                });

        //quick capture icons
        button_camera = (LinearLayout)findViewById(R.id.button_camera);
        button_video = (LinearLayout)findViewById(R.id.button_video);
        button_mic = (LinearLayout)findViewById(R.id.button_mic);
        //
        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quickCapture("1");
            }
        });

        button_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quickCapture("2");
            }
        });

        button_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quickCapture("3");            }
        });
    }

    public void quickCapture(String type){
        AppLockManager.getInstance().setExtendedTimeout();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("quickCapture", true);
        editor.putString("mediaType", type);
        editor.commit();

        finish();
    }
    
    private OnClickListener defaultButtonListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            int currentValue = -1;
            int id = arg0.getId();
			if (id == R.id.button0) {
				currentValue = 0;
			} else if (id == R.id.button1) {
				currentValue = 1;
			} else if (id == R.id.button2) {
				currentValue = 2;
			} else if (id == R.id.button3) {
				currentValue = 3;
			} else if (id == R.id.button4) {
				currentValue = 4;
			} else if (id == R.id.button5) {
				currentValue = 5;
			} else if (id == R.id.button6) {
				currentValue = 6;
			} else if (id == R.id.button7) {
				currentValue = 7;
			} else if (id == R.id.button8) {
				currentValue = 8;
			} else if (id == R.id.button9) {
				currentValue = 9;
			}

            //set the value and move the focus
            String currentValueString = String.valueOf(currentValue);
            mPinCodeField.setText(mPinCodeField.getText().toString() + currentValueString);
            mPinCodeField.setSelection(mPinCodeField.length());

            if(mPinCodeField.length() >= 4) {
                onPinLockInserted();
            }
        }
    };

    protected void showPasswordError(){
        Toast toast = Toast.makeText(AbstractPasscodeKeyboardActivity.this, getString(R.string.passcode_wrong_passcode), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
        toast.show();
    }
    
    protected abstract void onPinLockInserted();

    private InputFilter onlyNumber = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.length() > 1) {
                return "";
            }

            if (source.length() == 0) {
                return null;
            }

            try {
                int number = Integer.parseInt(source.toString());
                if (number >= 0 && number <= 9) {
                    return String.valueOf(number);
                }

                return "";
            } catch (NumberFormatException e) {
                return "";
            }
        }
    };
}
