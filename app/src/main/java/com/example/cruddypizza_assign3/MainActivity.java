package com.example.cruddypizza_assign3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import java.io.*;

public class MainActivity extends AppCompatActivity {

    //Controls
    Button btnStart, btnViewOrders, btnTranslate;
    TextView tvWelcome, tvTo, tvSlogan, tvBuiltBy;

    //Shared Pref
    SharedPreferences prefs;
    static final String LANGUAGE_KEY = "language";
    String sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Database
        try{
            String destPath = "/data/data/" + getPackageName() +"/database/OrderDB";
            File f = new File(destPath);
            if(!f.exists()){
                CopyDB(getBaseContext().getAssets().open("orderdb"),
                        new FileOutputStream(destPath));
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        //Hookup controls
        btnStart = findViewById(R.id.btnStart);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnTranslate = findViewById(R.id.btnTranslate);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTo = findViewById(R.id.tvTo);
        tvSlogan = findViewById(R.id.tvSlogan);
        tvBuiltBy = findViewById(R.id.tvBuiltBy);

        //Check lang pref
        checkPref();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send to Order page
                Intent i = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(i);
            }//End onClick
        });//End Start Handler

        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send to order list page
                Intent i = new Intent(MainActivity.this, DisplayOrdersActivity.class);
                startActivity(i);
            }
        });//End View Order Handler

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Translate
                translate();
                //Display lang preference message
                if(!sharedPref.equals("EN")){
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.langPref2),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.langPref),
                            Toast.LENGTH_SHORT).show();
                }
            }//End onClick
        });//End Translate Handler
    }//End onCreate

    public void checkPref() {
        //Set up shared pref and initialize with default
        prefs = getSharedPreferences(LANGUAGE_KEY, MODE_PRIVATE);
        sharedPref = prefs.getString(LANGUAGE_KEY, "EN");
        //Translate if applicable
        if(sharedPref.equals("FR")){
            sharedPref = "EN";
            translate();
        }
    }//End checkPref

    public void translate() {
        //Create resource var
        Resources res = getResources();
        //Shared pref editor
        SharedPreferences.Editor editor = prefs.edit();
        //Check which lang is currently displayed
        if(sharedPref.equals("EN")){
            //Update shared pref
            editor.putString(LANGUAGE_KEY, "FR");
            //Update display to French
            tvWelcome.setText(res.getString(R.string.tvWelcome2));
            tvTo.setText(res.getString(R.string.tvTo2));
            tvSlogan.setText(res.getString(R.string.tvSlogan2));
            btnStart.setText(res.getString(R.string.btnStart2));
            btnViewOrders.setText(res.getString(R.string.btnViewOrders2));
            tvBuiltBy.setText(res.getString(R.string.tvBuiltBy2));
        }
        else {
            //Update shared pref
            editor.putString(LANGUAGE_KEY, "EN");
            //Update display to English
            tvWelcome.setText(res.getString(R.string.tvWelcome));
            tvTo.setText(res.getString(R.string.tvTo));
            tvSlogan.setText(res.getString(R.string.tvSlogan));
            btnStart.setText(res.getString(R.string.btnStart));
            btnViewOrders.setText(res.getString(R.string.btnViewOrders));
            tvBuiltBy.setText(res.getString(R.string.tvBuiltBy));
        }
        //Apply editor changes
        editor.apply();
        //Get current shared pref after update
        sharedPref = prefs.getString(LANGUAGE_KEY, "EN");
    }//End Translate

    public void CopyDB(InputStream inputStream,OutputStream outputStream)
            throws IOException{
        //copy 1k bytes at a time
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) > 0)
        {
            outputStream.write(buffer,0,length);
        }
        inputStream.close();
        outputStream.close();
    }//End CopyDB
}//End Main