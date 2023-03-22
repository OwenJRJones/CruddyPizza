package com.example.cruddypizza_assign3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import java.util.*;


public class DisplayOrdersActivity extends AppCompatActivity implements RecyclerViewInterface{

    //Controls
    Button btnTranslate3, btnOrder2;
    TextView tvYourOrders;
    RecyclerView rvOrderList;

    //Shared Pref
    SharedPreferences prefs;
    static final String LANGUAGE_KEY = "language";
    String sharedPref;

    //String Arrays
    //English
    String[] Top;
    String[] Sizes;
    //French
    String[] Top2;
    String[] Sizes2;

    //ArrayList of objects
    private final ArrayList<Order> orderArrayList = new ArrayList<Order>();

    //DBAdapter
    DBAdapter db = new DBAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_orders);

        //Hookup controls
        btnTranslate3 = findViewById(R.id.btnTranslate3);
        btnOrder2 = findViewById(R.id.btnOrder2);
        tvYourOrders = findViewById(R.id.tvYourOrders);
        rvOrderList = findViewById(R.id.rvOrderList);

        //Check lang pref
        checkPref();
        //Set up arrays
        setUp();
        //Get orders
        getOrders();
        //Set adapter
        setAdapter();

        btnOrder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send back to order page
                Intent i = new Intent(DisplayOrdersActivity.this, OrderActivity.class);
                startActivity(i);
            }
        });//End Order Handler

        btnTranslate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Translate
                translate();
                setAdapter();
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

    public void setUp(){
        //Create resource var
        Resources res = getResources();
        //String Arrays
        //English
        Top = res.getStringArray(R.array.top);
        Sizes = res.getStringArray(R.array.sizes);
        //French
        Top2 = res.getStringArray(R.array.top2);
        Sizes2 = res.getStringArray(R.array.sizes2);
    }//End setUp

    public void setAdapter(){
        //Layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvOrderList.setLayoutManager(layoutManager);
        //Item Animator
        rvOrderList.setItemAnimator(new DefaultItemAnimator());
        //Create new adapter depending on lang pref
        OrderAdapter adapter;
        if(sharedPref.equals("EN")){
            adapter = new OrderAdapter(orderArrayList, Top, Sizes, sharedPref, this);
        }
        else {
            adapter = new OrderAdapter(orderArrayList, Top2, Sizes2, sharedPref, this);
        }
        //Set adapter
        rvOrderList.setAdapter(adapter);
    }//End setAdapter

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
            tvYourOrders.setText(res.getString(R.string.tvYourOrders2));
            btnOrder2.setText(res.getString(R.string.btnOrder2));
        }
        else {
            //Update shared pref
            editor.putString(LANGUAGE_KEY, "EN");
            //Update display to English
            tvYourOrders.setText(res.getString(R.string.tvYourOrders));
            btnOrder2.setText(res.getString(R.string.btnOrder));
        }
        //Change RecyclerView adapter
        setAdapter();
        //Apply editor changes
        editor.apply();
        //Get current shared pref after update
        sharedPref = prefs.getString(LANGUAGE_KEY, "EN");
    }//End Translate

    @Override
    public void onItemClick(int position) {
        //Send to details page with appropriate order ID
        Intent intent = new Intent(DisplayOrdersActivity.this, OrderDetailsActivity.class);
        intent.putExtra("ORDER_ID", orderArrayList.get(position).getId());
        startActivity(intent);
    }//End onItemClick

    public void getOrders() {
        try{
            db.open();
            Cursor c = db.getAllOrders();
            if(c.moveToFirst()) {
                do {
                    Order currOrder = new Order(c.getInt(0), c.getString(1), c.getInt(2),
                            c.getInt(3), c.getInt(4), c.getInt(5), c.getString(6));
                    orderArrayList.add(currOrder);
                }while(c.moveToNext());
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }//End getOrders
}//End Main