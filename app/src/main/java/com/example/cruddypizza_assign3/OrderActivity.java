package com.example.cruddypizza_assign3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.database.SQLException;
import android.os.*;
import android.text.*;
import android.widget.*;
import android.view.*;
import android.content.*;

public class OrderActivity extends AppCompatActivity {

    //Controls
    Button btnCancel, btnOrder, btnTranslate2;
    EditText etName;
    CheckBox cbSize1, cbSize2, cbSize3;
    Spinner spTopp1, spTopp2, spTopp3;
    TextView tvYourOrder, tvName, tvNameError, tvSize, tvSizeError, tvTopp1, tvTopp2, tvTopp3;

    //Shared Pref
    SharedPreferences prefs;
    static final String LANGUAGE_KEY = "language";
    String sharedPref;

    //String Arrays/spinner adapters
    //English
    String[] Top;
    String[] Sizes;
    ArrayAdapter<String> TopAdap;
    //French
    String[] Top2;
    String[] Sizes2;
    ArrayAdapter<String> TopAdap2;

    //Vars for current order
    String name;
    int size = -1;
    int topp1 = 0;
    int topp2 = 0;
    int topp3 = 0;

    //DBAdapter
    DBAdapter db =  new DBAdapter(this);

    //Regex
    //Regex source: https://stackoverflow.com/questions/1922097/regular-expression-for-french-characters
    String exp = "^[a-zA-ZÀ-ÿ- ]*$";
    String exp2 = "^\\s*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //Hookup Controls
        btnCancel = findViewById(R.id.btnCancel);
        btnOrder = findViewById(R.id.btnOrder);
        btnTranslate2  = findViewById(R.id.btnTranslate4);
        etName = findViewById(R.id.etName);
        cbSize1 = findViewById(R.id.cbSize1);
        cbSize2 = findViewById(R.id.cbSize2);
        cbSize3 = findViewById(R.id.cbSize3);
        spTopp1 = findViewById(R.id.spTopp1);
        spTopp2 = findViewById(R.id.spTopp2);
        spTopp3 = findViewById(R.id.spTopp3);
        tvYourOrder = findViewById(R.id.tvYourOrder);
        tvName = findViewById(R.id.tvName);
        tvNameError = findViewById(R.id.tvNameError);
        tvSize = findViewById(R.id.tvSize);
        tvSizeError = findViewById(R.id.tvSizeError);
        tvTopp1 = findViewById(R.id.tvTopp1);
        tvTopp2 = findViewById(R.id.tvTopp2);
        tvTopp3 = findViewById(R.id.tvTopp3);

        //Run setup for arrays and adapters
        setUp();
        //Check lang pref
        checkPref();

        btnTranslate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Translate
                translate();
                setFields();
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

        //Name field text listener
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = etName.getText().toString();
                if(!name.matches(exp) || name.contains(" ")){
                    tvNameError.setVisibility(View.VISIBLE);
                }
                else{
                    tvNameError.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = etName.getText().toString();
                if(!name.matches(exp) || name.contains(" ")){
                    tvNameError.setVisibility(View.VISIBLE);
                }
                else{
                    tvNameError.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                name = etName.getText().toString();
                if(!name.matches(exp) || name.contains(" ")){
                    tvNameError.setVisibility(View.VISIBLE);
                }
                else if (name.isEmpty()) {
                    tvNameError.setVisibility(View.INVISIBLE);
                }
            }
        });//End Text Listener

        //Checkbox selection listeners
        cbSize1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    size = 0;
                    cbSize2.setChecked(false);
                    cbSize3.setChecked(false);
                }
                else{
                    cbSize1.setChecked(false);
                }
                tvSizeError.setVisibility(View.INVISIBLE);
            }
        });
        cbSize2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    size = 1;
                    cbSize1.setChecked(false);
                    cbSize3.setChecked(false);
                }
                else{
                    cbSize2.setChecked(false);
                }
                tvSizeError.setVisibility(View.INVISIBLE);
            }
        });
        cbSize3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    size = 2;
                    cbSize1.setChecked(false);
                    cbSize2.setChecked(false);
                }
                else{
                    cbSize3.setChecked(false);
                }
                tvSizeError.setVisibility(View.INVISIBLE);
            }
        });//End Checkbox Listener

        //Spinner selection listeners
        spTopp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Get selected item
                topp1 = spTopp1.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                topp1 = 0;
            }
        });//End Handler
        spTopp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Get selected item
                topp2 = spTopp2.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                topp2 = 0;
            }
        });//End Handler
        spTopp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Get selected item
                topp3 = spTopp3.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                topp3 = 0;
            }
        });//End Handler

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields()){
                    //Insert order
                    insertOrder();
                    //Send to Order List page
                    Intent i = new Intent(OrderActivity.this, DisplayOrdersActivity.class);
                    startActivity(i);
                }
            }//End onClick
        });//End Order Handler

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send to Order List page
                Intent i = new Intent(OrderActivity.this, DisplayOrdersActivity.class);
                startActivity(i);
            }//End onClick
        });//End Cancel Handler
    }//End onCreate

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
        //Adapters for spinners
        //English
        TopAdap = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Top);
        TopAdap.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        //French
        TopAdap2 = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Top2);
        TopAdap2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        //Set adapters
        spTopp1.setAdapter(TopAdap);
        spTopp2.setAdapter(TopAdap);
        spTopp3.setAdapter(TopAdap);
        //Set checkbox labels
        cbSize1.setText(Sizes[0]);
        cbSize2.setText(Sizes[1]);
        cbSize3.setText(Sizes[2]);
    }//End setUp

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

    public void setFields(){
        //If user had fields filled out and translates, re-input fields
        name = etName.getText().toString();

        if(!name.isEmpty()){
            etName.setText(name);
        }
        if(topp1 != 0){
            spTopp1.setSelection(topp1);
        }
        if(topp2 != 0){
            spTopp2.setSelection(topp2);
        }
        if(topp3 != 0){
            spTopp3.setSelection(topp3);
        }
        switch(size){
            case 0:
                cbSize1.setChecked(true);
                break;
            case 1:
                cbSize2.setChecked(true);
                break;
            case 2:
                cbSize3.setChecked(true);
                break;
        }
    }//End setFields

    public boolean checkFields(){
        //Collect order name
        name = etName.getText().toString();
        //Check inputs
        if(name.isEmpty() || size == -1){
            if(sharedPref.equals("EN")){
                Toast.makeText(OrderActivity.this,
                        getResources().getString(R.string.orderError),
                        Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(OrderActivity.this,
                        getResources().getString(R.string.orderError2),
                        Toast.LENGTH_SHORT).show();
            }
        }
        //Check size is selected
        if(size == -1 || !cbSize1.isChecked() && !cbSize2.isChecked() && !cbSize3.isChecked()){
            size = -1;
            tvSizeError.setVisibility(View.VISIBLE);
        }
        //Return T/F
        return name.matches(exp) && !name.contains(" ") && !name.isEmpty() && size != -1;
    }//End checkFields

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
            tvYourOrder.setText(res.getString(R.string.tvYourOrder2));
            tvName.setText(res.getString(R.string.tvName2));
            etName.setHint(res.getString(R.string.etNameHint2));
            tvNameError.setText(res.getString(R.string.tvNameError2));
            tvSize.setText(res.getString(R.string.tvSize2));
            tvSizeError.setText(res.getString(R.string.tvSizeError2));
            tvTopp1.setText(res.getString(R.string.tvTopp1_2));
            tvTopp2.setText(res.getString(R.string.tvTopp2_2));
            tvTopp3.setText(res.getString(R.string.tvTopp3_2));
            btnCancel.setText(res.getString(R.string.btnCancel2));
            btnOrder.setText(res.getString(R.string.btnOrder2));
            cbSize1.setText(Sizes2[0]);
            cbSize2.setText(Sizes2[1]);
            cbSize3.setText(Sizes2[2]);
            spTopp1.setAdapter(TopAdap2);
            spTopp2.setAdapter(TopAdap2);
            spTopp3.setAdapter(TopAdap2);
        }
        else {
            //Update shared pref
            editor.putString(LANGUAGE_KEY, "EN");
            //Update display to English
            tvYourOrder.setText(res.getString(R.string.tvYourOrder));
            tvName.setText(res.getString(R.string.tvName));
            etName.setHint(res.getString(R.string.etNameHint));
            tvNameError.setText(res.getString(R.string.tvNameError));
            tvSize.setText(res.getString(R.string.tvSize));
            tvSizeError.setText(res.getString(R.string.tvSizeError));
            tvTopp1.setText(res.getString(R.string.tvTopp1));
            tvTopp2.setText(res.getString(R.string.tvTopp2));
            tvTopp3.setText(res.getString(R.string.tvTopp3));
            btnCancel.setText(res.getString(R.string.btnCancel));
            btnOrder.setText(res.getString(R.string.btnOrder));
            cbSize1.setText(Sizes[0]);
            cbSize2.setText(Sizes[1]);
            cbSize3.setText(Sizes[2]);
            spTopp1.setAdapter(TopAdap);
            spTopp2.setAdapter(TopAdap);
            spTopp3.setAdapter(TopAdap);
        }
        //Apply editor changes
        editor.apply();
        //Get current shared pref after update
        sharedPref = prefs.getString(LANGUAGE_KEY, "EN");
    }//End Translate

    public void insertOrder() {
        try {
            db.open();
            db.insertOrder(name, String.valueOf(size), String.valueOf(topp1),
                    String.valueOf(topp2), String.valueOf(topp3));
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            db.close();
        }
    }//End insertOrder
}//End Main