package com.example.cruddypizza_assign3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.os.*;
import android.text.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.util.*;

public class OrderDetailsActivity extends AppCompatActivity {

    //Controls
    Button btnEdit, btnDelete, btnTranslate4;
    EditText etName;
    CheckBox cbSize1, cbSize2, cbSize3;
    Spinner spTopp1, spTopp2, spTopp3;
    TextView tvOrderDetails, tvName, tvNameError, tvSize, tvSizeError, tvTopp1, tvTopp2, tvTopp3;

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

    //Vras for current order initial vals
    String initName;
    int initSize;
    int initTopp1;
    int initTopp2;
    int initTopp3;

    //DBAdapter
    DBAdapter db = new DBAdapter(this);

    //Regex
    //Regex source: https://stackoverflow.com/questions/1922097/regular-expression-for-french-characters
    String exp = "^[a-zA-ZÀ-ÿ- ]*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        //Get order ID from bundle
        final int ORDER_ID = getIntent().getIntExtra("ORDER_ID", 0);

        //Hookup Controls
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnTranslate4  = findViewById(R.id.btnTranslate4);
        etName = findViewById(R.id.etName);
        cbSize1 = findViewById(R.id.cbSize1);
        cbSize2 = findViewById(R.id.cbSize2);
        cbSize3 = findViewById(R.id.cbSize3);
        spTopp1 = findViewById(R.id.spTopp1);
        spTopp2 = findViewById(R.id.spTopp2);
        spTopp3 = findViewById(R.id.spTopp3);
        tvOrderDetails = findViewById(R.id.tvOrderDetails);
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
        //Get selected order
        getOrder(ORDER_ID);
        //Disable editable fields
        fieldsEnabled(false);

        //Name field text listener
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = etName.getText().toString();
                if(!name.matches(exp)){
                    tvNameError.setVisibility(View.VISIBLE);
                }
                else{
                    tvNameError.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = etName.getText().toString();
                if(!name.matches(exp)){
                    tvNameError.setVisibility(View.VISIBLE);
                }
                else{
                    tvNameError.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                name = etName.getText().toString();
                if(!name.matches(exp)){
                    tvNameError.setVisibility(View.VISIBLE);
                }
                else if (name.isEmpty()){
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
        });//End Checkbox Listener
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
        });//End Checkbox Listener
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

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkButton(btnEdit, "Edit") || checkButton(btnEdit, "Éditer")){
                    //Execute edit
                    edit();
                }
                else if(checkButton(btnEdit, "Cancel") || checkButton(btnEdit, "Annuler")){
                    //Execute cancel
                    cancel();
                }
            }
        });//End Edit Handler

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkButton(btnDelete, "Delete") || checkButton(btnDelete, "Effacer")){
                    //Delete order
                    deleteOrder(ORDER_ID);
                }
                else if(checkButton(btnDelete, "Save") || checkButton(btnDelete, "Confirmer")){
                    //Update order
                    if(!valuesUnchanged()){
                        updateOrder(ORDER_ID);
                    }
                }
                //Send back to orders list page
                Intent i = new Intent(OrderDetailsActivity.this, DisplayOrdersActivity.class);
                startActivity(i);
            }
        });//End Delete Handler

        btnTranslate4.setOnClickListener(new View.OnClickListener() {
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
    }//End onClick

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
            tvOrderDetails.setText(res.getString(R.string.tvOrderDetails2));
            tvName.setText(res.getString(R.string.tvName2));
            etName.setHint(res.getString(R.string.etNameHint2));
            tvNameError.setText(res.getString(R.string.tvNameError2));
            tvSize.setText(res.getString(R.string.tvSize2));
            tvSizeError.setText(res.getString(R.string.tvSizeError2));
            tvTopp1.setText(res.getString(R.string.tvTopp1_2));
            tvTopp2.setText(res.getString(R.string.tvTopp2_2));
            tvTopp3.setText(res.getString(R.string.tvTopp3_2));
            btnEdit.setText(res.getString(R.string.btnEdit2));
            btnDelete.setText(res.getString(R.string.btnDelete2));
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
            tvOrderDetails.setText(res.getString(R.string.tvOrderDetails));
            tvName.setText(res.getString(R.string.tvName));
            etName.setHint(res.getString(R.string.etNameHint));
            tvNameError.setText(res.getString(R.string.tvNameError));
            tvSize.setText(res.getString(R.string.tvSize));
            tvSizeError.setText(res.getString(R.string.tvSizeError));
            tvTopp1.setText(res.getString(R.string.tvTopp1));
            tvTopp2.setText(res.getString(R.string.tvTopp2));
            tvTopp3.setText(res.getString(R.string.tvTopp3));
            btnEdit.setText(res.getString(R.string.btnEdit));
            btnDelete.setText(res.getString(R.string.btnDelete));
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

    //Set fields
    public void setFields() {
        //Set fields accordingly
        etName.setText(name);

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

    public void setInitialVals() {
        name = initName;
        size = initSize;
        topp1 = initTopp1;
        topp2 = initTopp2;
        topp3 = initTopp3;
        //Set fields back to initial values
        etName.setText(name);
        spTopp1.setSelection(topp1);
        spTopp2.setSelection(topp2);
        spTopp3.setSelection(topp3);
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
    }//End setInitialVals

    public void fieldsEnabled(Boolean bool) {
        etName.setEnabled(bool);
        cbSize1.setEnabled(bool);
        cbSize2.setEnabled(bool);
        cbSize3.setEnabled(bool);
        spTopp1.setEnabled(bool);
        spTopp2.setEnabled(bool);
        spTopp3.setEnabled(bool);
    }//End fieldsEnabled

    public Boolean checkButton(Button btn, String str) {
        return btn.getText().equals(str);
    }//End checkButton

    public Boolean valuesUnchanged() {
        return name.equals(initName) && size == initSize && topp1 == initTopp1 &&
                topp2 == initTopp2 && topp3 == initTopp3;
    }//End valuesUnchanged

    public void edit() {
        //Allow fields to be edited
        fieldsEnabled(true);
        //Change button labels
        if(sharedPref.equals("EN")){
            btnEdit.setText(getResources().getString(R.string.btnCancel));
            btnDelete.setText(getResources().getString(R.string.btnSave));
        }
        else{
            btnEdit.setText(getResources().getString(R.string.btnCancel2));
            btnDelete.setText(getResources().getString(R.string.btnSave2));
        }
    }//End edit

    public void cancel() {
        //Disable editable fields
        fieldsEnabled(false);
        //Return fields to initial values
        setInitialVals();
        //Change button labels
        if(sharedPref.equals("EN")){
            btnEdit.setText(getResources().getString(R.string.btnEdit));
            btnDelete.setText(getResources().getString(R.string.btnDelete));
        }
        else{
            btnEdit.setText(getResources().getString(R.string.btnEdit2));
            btnDelete.setText(getResources().getString(R.string.btnDelete2));
        }
    }//End cancel

    public void getOrder(int orderId) {
        try {
            db.open();
            Cursor c = db.getOrder(orderId);
            if(c.moveToFirst()) {
                name = c.getString(1);
                size = c.getInt(2);
                topp1 = c.getInt(3);
                topp2 = c.getInt(4);
                topp3 = c.getInt(5);
                setFields();
                //Store initial values for reference
                initName = name;
                initSize = size;
                initTopp1 = topp1;
                initTopp2 = topp2;
                initTopp3 = topp3;
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }//End getOrder

    public void updateOrder(int orderId) {
       try{
           db.open();
           if(db.updateOrder(orderId, name, String.valueOf(size), String.valueOf(topp1),
                   String.valueOf(topp2), String.valueOf(topp3))) {
               if(sharedPref.equals("EN")){
                   Toast.makeText(getBaseContext(), getResources().getString(R.string.OrderUpdateSuccess),
                           Toast.LENGTH_SHORT).show();
               }
               else {
                   Toast.makeText(getBaseContext(), getResources().getString(R.string.OrderUpdateSuccess2),
                           Toast.LENGTH_SHORT).show();
               }
           }
           else {
               if(sharedPref.equals("EN")){
                   Toast.makeText(getBaseContext(), getResources().getString(R.string.OrderUpdateFail),
                           Toast.LENGTH_SHORT).show();
               }
               else {
                   Toast.makeText(getBaseContext(), getResources().getString(R.string.OrderUpdateFail2),
                           Toast.LENGTH_SHORT).show();
               }
           }
       }
       catch(SQLException e) {
          e.printStackTrace();
       }
       catch(Exception e) {
           e.printStackTrace();
       } finally {
           db.close();
       }
    }//End updateOrder

    public void deleteOrder(int orderId) {
        try{
            db.open();
            if(db.deleteOrder(orderId)) {
                if(sharedPref.equals("EN")){
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.OrderDeleteSuccess),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.OrderDeleteSuccess2),
                            Toast.LENGTH_SHORT).show();
                }
            }
            else {
                if(sharedPref.equals("EN")){
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.OrderDeleteSuccess2),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.OrderDeleteFail2),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }//End deleteOrder
}//End Main