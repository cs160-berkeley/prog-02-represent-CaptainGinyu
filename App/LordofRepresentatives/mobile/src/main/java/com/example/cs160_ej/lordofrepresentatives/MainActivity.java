package com.example.cs160_ej.lordofrepresentatives;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener
{
    protected InputMethodManager inputMethodManager;
    protected EditText zipCodeInputField;
    protected Button enterButton;
    protected int enterButtonColor;
    protected Button useGPSButton;
    protected int useGPSButtonColor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);

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

        enterButton = (Button) findViewById(R.id.enterButton);
        enterButtonColor = ((ColorDrawable) enterButton.getBackground()).getColor();
        enterButton.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, enterButtonColor);
                startActivity(new Intent(MainActivity.this, Congressional.class));
                finish();
                return false;
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
                return false;
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
}
