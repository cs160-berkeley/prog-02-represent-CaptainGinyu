package com.example.cs160_ej.lordofrepresentatives;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener
{
    protected InputMethodManager inputMethodManager;
    protected EditText zipCodeInputField;

    protected Button enterButton;
    protected int enterButtonColor;

    protected Button useGPSButton;
    protected int useGPSButtonColor;

    protected int currTypedZipCode;
    protected TextWatcher zipInputWatcher;

    public static final String DUMMY_GPS_LOCATION = "Berkeley, CA";

    private boolean leavingActivity;
    private boolean showingInvalidZipToast;

    private static final int SHORT_TOAST_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        leavingActivity = false;
        showingInvalidZipToast = false;

        currTypedZipCode = -1;

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        zipCodeInputField = (EditText) findViewById(R.id.zipInput);
        zipCodeInputField.setOnFocusChangeListener(this);
        zipCodeInputField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    zipCodeInputField.clearFocus();
                    return true;
                }
                return false;
            }
        });
        zipInputWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                try
                {
                    currTypedZipCode = Integer.parseInt(s.toString());
                }
                catch (Exception e)
                {
                    currTypedZipCode = -1;
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        };

        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                CharSequence zipFromBefore = extras.getCharSequence("received zip", "");
                zipCodeInputField.setText(zipFromBefore);
                if (zipFromBefore != "")
                {
                    currTypedZipCode = Integer.parseInt(zipFromBefore.toString());
                }
            }
        }

        zipCodeInputField.addTextChangedListener(zipInputWatcher);

        enterButton = (Button) findViewById(R.id.enterButton);
        enterButtonColor = ((ColorDrawable) enterButton.getBackground()).getColor();
        enterButton.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, enterButtonColor);
                if (isValidZipCode(currTypedZipCode))
                {
                    if (!leavingActivity)
                    {
                        leavingActivity = true;
                        Intent goToCongressional = new Intent(MainActivity.this, Congressional.class);
                        goToCongressional.putExtra("zip", currTypedZipCode);
                        goToCongressional.putExtra("to append", "ZIP code");

                        startActivity(goToCongressional);
                        finish();
                    }
                }
                else
                {
                    if (!showingInvalidZipToast)
                    {
                        Toast.makeText(MainActivity.this, "Invalid ZIP code", Toast.LENGTH_SHORT).show();
                        showingInvalidZipToast = true;
                        Thread thread = new Thread()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    Thread.sleep(SHORT_TOAST_DURATION);
                                    showingInvalidZipToast = false;
                                }
                                catch (Exception e)
                                {

                                }
                            }
                        };
                        thread.start();
                    }
                }
                return true;
            }
        });

        useGPSButton = (Button) findViewById(R.id.useGPSButton);
        useGPSButtonColor = ((ColorDrawable) useGPSButton.getBackground()).getColor();
        useGPSButton.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, useGPSButtonColor);
                if (!leavingActivity)
                {
                    leavingActivity = true;
                    Intent goToCongressional = new Intent(MainActivity.this, Congressional.class);
                    goToCongressional.putExtra("to append", DUMMY_GPS_LOCATION);

                    startActivity(goToCongressional);
                    finish();
                }
                return true;
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (!hasFocus)
        {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        else
        {
            int orientation = getResources().getConfiguration().orientation;

            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
            else
            {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        }
    }

    public boolean changeButtonColor(View view, MotionEvent motionEvent, int origColor)
    {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            ((Button) view).setBackgroundColor(Color.GREEN);
        }
        else
        {
            ((Button) view).setBackgroundColor(origColor);
        }

        return true;
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    public boolean isValidZipCode(int testValue)
    {
        return Integer.toString(testValue).length() == 5;
    }
}
