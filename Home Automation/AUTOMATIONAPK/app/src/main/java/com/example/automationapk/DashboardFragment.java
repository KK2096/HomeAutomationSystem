package com.example.automationapk;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Objects;

public class DashboardFragment extends Fragment {
    public View view_dash;
    public EditText editText_appl_name,editText_appl_place;
    public Button btn_appl_add;
    public Spinner spinner_appl;

    //Database obj
    public SQLiteDatabase sdb_dash;
    public Cursor cursor_dash;

    //DB name
    public String db_name = "DBHOMEAUTO";

    //Get data from text
    public String add_appl_name,add_appl_place;

    //Array-Spinner
    ArrayAdapter<String> arrayAdapter_dash;

    //Display database profiles in spinner
    public String[] arrdash;

    //Save status
    public SharedPreferences LastPreferenceDash;
    public SharedPreferences.Editor editor_dash;
    public int lastclick_dash;

    //Fetch appliance
    public String fetch_appl_name,fetch_appl_place;

    //Store selected spinner text
    public String item_appl;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view_dash = inflater.inflate(R.layout.fragment_dashboard, null);

        //Get id
        editText_appl_name = view_dash.findViewById(R.id.edittextapp);
        editText_appl_place = view_dash.findViewById(R.id.edittextplace);
        btn_appl_add = view_dash.findViewById(R.id.add_app);
        spinner_appl = view_dash.findViewById(R.id.spin_app);

        //Create Table
        sdb_dash = Objects.requireNonNull(getActivity()).openOrCreateDatabase(db_name,android.content.Context.MODE_PRIVATE ,null);
        String table_creation = "CREATE TABLE IF NOT EXISTS " + "Appliance_Table" + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,APPLIANCE_NAME TEXT,PLACE TEXT);";
        sdb_dash.execSQL(table_creation);

        //Load spinner
        getDashBoardSpinnerData();


        //Apply onClickAction on add_appliance_button
        btn_appl_add.setOnClickListener(view -> {
            if(TextUtils.isEmpty(editText_appl_name.getText()))
            {
                editText_appl_name.setError("Enter a appliance name");
                editText_appl_name.requestFocus();
                Toast.makeText(getActivity(),"Appliance Name is Empty !! ",Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(editText_appl_place.getText()))
            {
                editText_appl_place.setError("Enter a appliance place");
                editText_appl_place.requestFocus();
                Toast.makeText(getActivity(),"Appliance Place is Empty !! ",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "All are filled", Toast.LENGTH_SHORT).show();
                add_appl_name = editText_appl_name.getText().toString();
                add_appl_place = editText_appl_place.getText().toString();
                Toast.makeText(getActivity(),add_appl_name+"\n"+add_appl_place,Toast.LENGTH_SHORT).show();
                ContentValues contentValues_dash = new ContentValues();
                contentValues_dash.put("APPLIANCE_NAME",add_appl_name);
                contentValues_dash.put("PLACE",add_appl_place);
                sdb_dash.insert("Appliance_Table",null,contentValues_dash);
                Toast.makeText(getActivity(),"Appliance added Successfully :)",Toast.LENGTH_SHORT).show();
            }
            //Load spinner
            getDashBoardSpinnerData();

        });
        cursor_dash.close();
        return view_dash;
    }

    public void getDashBoardSpinnerData()
    {

        //Save current state
        LastPreferenceDash = Objects.requireNonNull(this.getActivity()).getSharedPreferences("LAST STATE DASH", Context.MODE_PRIVATE);
        editor_dash = LastPreferenceDash.edit();
        lastclick_dash = LastPreferenceDash.getInt("Last State Dash",0);


        cursor_dash =sdb_dash.rawQuery("SELECT * FROM Appliance_Table",null);
            arrdash = new String[cursor_dash.getCount()];
            if(cursor_dash.getCount() == 0 )
            {
                Toast.makeText(getActivity(), "No records in Table", Toast.LENGTH_SHORT).show();
            }
            else if(cursor_dash.getCount() > 0)
            {
                cursor_dash.moveToFirst();
                for (int i = 0; i < arrdash.length; i++)
                {
                    arrdash[i] = cursor_dash.getString(1);
                    cursor_dash.moveToNext();
                }
            }
        //Set arrayAdapter
        arrayAdapter_dash = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrdash);
        arrayAdapter_dash.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set data into spinner
        spinner_appl.setAdapter(arrayAdapter_dash);
        spinner_appl.setBackgroundColor(getResources().getColor(R.color.whiteTextColor));

        spinner_appl.setSelection(lastclick_dash);

        spinner_appl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor_dash.putInt("Last State Dash",i).apply();
                item_appl = spinner_appl.getSelectedItem().toString();
                Toast.makeText(getActivity(),"Spinner Click"+item_appl,Toast.LENGTH_SHORT).show();
                cursor_dash = sdb_dash.rawQuery("SELECT * FROM Appliance_Table WHERE APPLIANCE_NAME = '"+item_appl+"'", null);
                if(cursor_dash.getCount() == 0)
                {
                    Toast.makeText(getActivity(), "No records", Toast.LENGTH_SHORT).show();
                }
                else if(cursor_dash.getCount() > 0)
                {
                    if (cursor_dash.moveToFirst())
                    {
                        do {
                            fetch_appl_name = cursor_dash.getString(1);
                            fetch_appl_place = cursor_dash.getString(2);
                            Toast.makeText(getActivity(),"Appliance Name "+fetch_appl_name+ "\nAppliance Place  "+fetch_appl_place,Toast.LENGTH_LONG).show();
                        } while (cursor_dash.moveToNext());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}