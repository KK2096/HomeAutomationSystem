package com.example.automationapk;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class DeviceFragment extends Fragment {

    public View view_device;
    //Switch button object
    Switch[] add_appl_switch = new Switch[30];

    //Database object
    SQLiteDatabase sdb_devi;
    Cursor cursor_devi;

    //DB NAME
    public String db_name = "DBHOMEAUTO";

    //Obj for set appliance number
    public TextView text_app_count;

    //Obj for fetch
    public String fetch_no_dev,fetch_appl_name_db;

    //Store ip_add and port of Profiles
    public String get_ip_add_ser;
    public Integer get_port;

    //Get selected appl_name
    public String getNameSwitch;


    //Save status
    public SharedPreferences LastPreferenceDev;
    public SharedPreferences.Editor editor_devh;
    public int lastclick_dev;

    public boolean switch_status;

    public static String MY_PREF = "switch_pref";
    public static String swch_status = "light_on";

    //Variable for state
    public static Integer state;

    //var for concate status and appl_name
    public String apst;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view_device = inflater.inflate(R.layout.fragment_device, null);

        text_app_count = view_device.findViewById(R.id.no_dev);

        //Open db
        sdb_devi = Objects.requireNonNull(getActivity()).openOrCreateDatabase(db_name, MODE_PRIVATE ,null);
        cursor_devi =sdb_devi.rawQuery("SELECT * FROM Appliance_Table",null);
        if(cursor_devi.getCount() == 0)
        {
            Toast.makeText(getActivity(),"No records in Table",Toast.LENGTH_SHORT).show();
        }
        else {
            //    Get appliance from Appliance table and add dynamic switch buttons
            int i = 0;
            if (cursor_devi.moveToFirst()) {
                do {
                    fetch_no_dev = cursor_devi.getString(0);
                    fetch_appl_name_db = cursor_devi.getString(1);
                    text_app_count.setText(fetch_no_dev);
                    addRadBtn(fetch_appl_name_db, i);
                    i++;
                }
                while (cursor_devi.moveToNext());
            }
        }
        return view_device;
    }

    //Add dynamic switch
    public void addRadBtn(String setSwitchText, int i)
    {

        LinearLayout linearLayout = (LinearLayout) view_device.findViewById(R.id.switch_lay);
        add_appl_switch[i] = new Switch(getActivity());
        add_appl_switch[i].setTextSize(20);
        add_appl_switch[i].setTextColor(getResources().getColor(R.color.whiteTextColor));
        //add_appl_switch[i].setPaddingRelative(40,80,480,40);
        add_appl_switch[i].setText(setSwitchText);
        /*
        LastPreferenceDev = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREF,MODE_PRIVATE);
        editor_devh = getActivity().getSharedPreferences(MY_PREF,MODE_PRIVATE).edit();

        switch_status = LastPreferenceDev.getBoolean(swch_status,false);//False is default value

        add_appl_switch[i].setChecked(switch_status);

        add_appl_switch[i].setOnCheckedChangeListener((compoundButton, b) -> {
            if(add_appl_switch[i].isChecked() == true)
            {
                editor_devh.putBoolean(swch_status,true);
                editor_devh.apply();
                add_appl_switch[i].setChecked(true);
                //Toast.makeText(getActivity(),get_ip_add_ser+"\n"+get_port,Toast.LENGTH_SHORT).show();
            }
            else{
                editor_devh.putBoolean(swch_status,false);
                editor_devh.apply();
                add_appl_switch[i].setChecked(false);
                //Toast.makeText(getActivity(),"Nahi ho gaya",Toast.LENGTH_SHORT).show();

            }
        });*/

        add_appl_switch[i].setOnClickListener(view -> {
            if(add_appl_switch[i].isChecked())
            {
                state = 1;
                get_ip_add_ser = ProfilesFragment.fetch_ser_id;
                get_port = ProfilesFragment.fetch_port;
                getNameSwitch = add_appl_switch[i].getText().toString();
                apst = getNameSwitch + "," + state;
                Toast.makeText(getActivity(),getNameSwitch+"\n"+apst+"\n"+get_ip_add_ser+"\n"+get_port,Toast.LENGTH_SHORT).show();
                ServerConnector serverConnector;
                if(get_ip_add_ser == null && get_port == null)
                {
                    serverConnector = new ServerConnector("192.168.1.104", 5000);
                }
                else {
                    serverConnector = new ServerConnector(get_ip_add_ser, get_port);
                }
                serverConnector.execute(apst);

            }

            else if(!add_appl_switch[i].isChecked()){
                state = 0;
                get_ip_add_ser = ProfilesFragment.fetch_ser_id;
                get_port = ProfilesFragment.fetch_port;
                getNameSwitch = add_appl_switch[i].getText().toString();
                apst = getNameSwitch + "," + state;
                Toast.makeText(getActivity(),getNameSwitch+"\n"+apst+"\n"+get_ip_add_ser+"\n"+get_port,Toast.LENGTH_SHORT).show();
                ServerConnector serverConnector;
                if(get_ip_add_ser == null && get_port == null)
                {
                    serverConnector = new ServerConnector("192.168.1.104", 5000);
                }
                else {
                    serverConnector = new ServerConnector(get_ip_add_ser, get_port);
                }
                serverConnector.execute(apst);
                Toast.makeText(getActivity(), apst, Toast.LENGTH_LONG).show();

            }

        });
        linearLayout.addView(add_appl_switch[i]);
    }
}