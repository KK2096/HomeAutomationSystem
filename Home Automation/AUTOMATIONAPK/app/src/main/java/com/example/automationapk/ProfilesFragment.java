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


public class ProfilesFragment extends Fragment {
    public View view_prof;

    public EditText editText_prof,editText_ser_add,editText_port;
    public Button button_prof_add;
    public Spinner prof_spin;

    //Database obj
    public SQLiteDatabase sdb_prof;
    public Cursor cursor_prof;

    //DB name
    public String db_name = "DBHOMEAUTO";

    //Array-Spinner
    ArrayAdapter<String> arrayAdapter_prof;

    //Display database profiles in spinner
    public String[] arrprof;


    //Get spinner Item
    public String item;

    public SharedPreferences LastPreference;
    public SharedPreferences.Editor editor;
    public int lastclick;

    //Add db temp store var
    public String add_prof,add_ip;
    public Integer add_port;

    //Get records from db
    public static String fetch_prof,fetch_ser_id;
    public static Integer fetch_port;

    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view_prof = inflater.inflate(R.layout.fragment_profiles,null);

        //Get id
        editText_prof = view_prof.findViewById(R.id.editextprof);
        editText_ser_add = view_prof.findViewById(R.id.editextserip);
        editText_port = view_prof.findViewById(R.id.editextport);
        button_prof_add = view_prof.findViewById(R.id.add_prof);
        prof_spin = view_prof.findViewById(R.id.spinProf);

        //       Your fragment
//        LastPreference = Objects.requireNonNull(this.getActivity()).getSharedPreferences("LAST STATE", Context.MODE_PRIVATE);
//        editor = LastPreference.edit();
//        lastclick = LastPreference.getInt("Last State",0);

        //Query for create table

        sdb_prof = Objects.requireNonNull(getActivity()).openOrCreateDatabase(db_name,android.content.Context.MODE_PRIVATE ,null);
        String table_creation = "CREATE TABLE IF NOT EXISTS " + "Profiles_Table" + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,PROFILE_NAME TEXT,SERVER_IP TEXT ,PORT_NO INTEGER);";
        sdb_prof.execSQL(table_creation);

        //Load spinner using array_adapter
        getSpinnerData();

        //Add action of add_prof_button
        button_prof_add.setOnClickListener(view -> {
            if(TextUtils.isEmpty(editText_prof.getText()))
            {
                editText_prof.setError("Enter a profile name");
                editText_prof.requestFocus();
                Toast.makeText(getActivity(), "Profile Name is Empty !! (:", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(editText_ser_add.getText()))
            {
                editText_ser_add.setError("Enter a server address");
                editText_ser_add.requestFocus();
                Toast.makeText(getActivity(), "Server IP is Empty !! (:", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(editText_port.getText()))
            {
                editText_port.setError("Enter a port number");
                editText_port.requestFocus();
                Toast.makeText(getActivity(), "Port Number is Empty !! (:", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(),"All are filled",Toast.LENGTH_SHORT).show();

                //Get data from editText
                add_prof = editText_prof.getText().toString();
                add_ip = editText_ser_add.getText().toString();
                add_port = Integer.parseInt(editText_port.getText().toString());

                Toast.makeText(getActivity(),add_prof+"\n"+add_ip+"\n"+add_port,Toast.LENGTH_SHORT).show();

                //Start add data in db
                ContentValues contentValues_prof = new ContentValues();
                contentValues_prof.put("PROFILE_NAME",add_prof);
                contentValues_prof.put("SERVER_IP",add_ip);
                contentValues_prof.put("PORT_NO",add_port);

                //Insert values Query
                sdb_prof.insert("Profiles_Table", null, contentValues_prof);
                Toast.makeText(getActivity(), "Data Inserted Successfully :) :) " , Toast.LENGTH_SHORT).show();

            }
            getSpinnerData();
        });

        cursor_prof.close();
        return view_prof;
    }

    // Get data from db and load into spinner

        public void getSpinnerData()
        {
            //       Your fragment
            LastPreference = Objects.requireNonNull(this.getActivity()).getSharedPreferences("LAST STATE", Context.MODE_PRIVATE);
            editor = LastPreference.edit();
            lastclick = LastPreference.getInt("Last State",0);

            cursor_prof = sdb_prof.rawQuery("SELECT * FROM Profiles_Table",null);
            arrprof = new String[cursor_prof.getCount()];
            if(cursor_prof.getCount() == 0)
            {
                Toast.makeText(getActivity(), "No records in Table", Toast.LENGTH_SHORT).show();
            }
            else if(cursor_prof.getCount() > 0)
            {
                cursor_prof.moveToFirst();
                for (int i = 0; i < arrprof.length; i++)
                {
                    arrprof[i] = cursor_prof.getString(1);
                    cursor_prof.moveToNext();
                }
            }

            //Load data in db using array_adapter
            arrayAdapter_prof = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrprof);
            arrayAdapter_prof.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Set spinner
            prof_spin.setAdapter(arrayAdapter_prof);
            prof_spin.setBackgroundColor(getResources().getColor(R.color.whiteTextColor));

            prof_spin.setSelection(lastclick);

            //Spinner action
            prof_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    editor.putInt("Last State",i).apply();
                    item = prof_spin.getSelectedItem().toString();
                    Toast.makeText(getActivity(),"Selected: "+item,Toast.LENGTH_SHORT).show();
                    cursor_prof = sdb_prof.rawQuery("SELECT * FROM Profiles_Table WHERE PROFILE_NAME = '"+item+"'", null);
                    if(cursor_prof.getCount() == 0)
                    {
                        Toast.makeText(getActivity(), "No records", Toast.LENGTH_SHORT).show();
                    }
                    //Get spinner selected record from db
                    else if(cursor_prof.getCount() > 0)
                    {
                        if (cursor_prof.moveToFirst()) {
                            do {
                                fetch_prof = cursor_prof.getString(1);
                                fetch_ser_id = cursor_prof.getString(2);
                                fetch_port = Integer.parseInt(cursor_prof.getString(3));
                                Toast.makeText(getActivity(),"Profile "+fetch_prof+ "\nServer IP  "+fetch_ser_id+"\n Port No "+fetch_port,Toast.LENGTH_LONG).show();
                            } while (cursor_prof.moveToNext());
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }

}