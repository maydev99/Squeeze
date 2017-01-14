package com.droidloft.squeeze;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String version = "1.0", buildDate = "1-14-2017";
    private EditText sysET, diaET;
    private Button saveB;
    private ListView listView;
    private ArrayList<String> myArrayList;
    private ArrayAdapter<String>myAdapter;
    private String strDate, strDia, strSys;
    private Set<String> set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        idViews();
        loadSettings();

        if(set != null){
            myArrayList = new ArrayList<>(set);
        } else {
            myArrayList = new ArrayList<>();
        }

        displayList();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(MainActivity.this);
                deleteAlert.setTitle("Delete Entry from List");
                deleteAlert.setMessage("Are you sure?");
                deleteAlert.setIcon(R.mipmap.ic_launcher);
                deleteAlert.setCancelable(true);
                deleteAlert.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myArrayList.remove(position);
                        displayList();
                    }
                });
                deleteAlert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Closes Dialog
                    }
                });
                deleteAlert.show();
                return false;
            }
        });



        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate();
                getEnteredValues();
                if(strDia.equals("") || strSys.equals("")){
                    Toast.makeText(MainActivity.this, "Data missing", Toast.LENGTH_SHORT).show();
                } else {
                    getDate();
                    getEnteredValues();
                    String newEntry = strDate + "      " + strSys + "/" + strDia;
                    myArrayList.add(newEntry);
                    saveSettings();
                    displayList();
                    sysET.setText("");
                    sysET.requestFocus();
                    diaET.setText("");
                }


            }
        });



    }

    private void displayList() {
        Collections.sort(myArrayList, Collections.<String>reverseOrder());
        myAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, R.id.list_item_item, myArrayList);
        listView.setAdapter(myAdapter);

    }

    private void loadSettings() {
        SharedPreferences listPrefs = getSharedPreferences("saved_list_key", MODE_PRIVATE);
        set = listPrefs.getStringSet("saved_list_key", null);

    }

    private void saveSettings(){
        set = new HashSet<>();
        set.addAll(myArrayList);

        SharedPreferences listPrefs = getSharedPreferences("saved_list_key", MODE_PRIVATE);
        SharedPreferences.Editor listEditor = listPrefs.edit();
        listEditor.putStringSet("saved_list_key", set);
        listEditor.apply();
    }

    private void getEnteredValues() {
        strDia = diaET.getText().toString();
        strSys = sysET.getText().toString();

    }

    private void getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/DD   hh:mma");
        strDate = sdf.format(cal.getTime());
        //Toast.makeText(MainActivity.this, "" + strDate, Toast.LENGTH_SHORT).show();

    }




    private void idViews() {
        sysET = (EditText)findViewById(R.id.sysEditText);
        diaET = (EditText)findViewById(R.id.diaEditText);
        saveB = (Button)findViewById(R.id.saveButton);
        listView = (ListView)findViewById(R.id.listview);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.about) {
            AlertDialog.Builder aboutAlert = new AlertDialog.Builder(this);
            aboutAlert.setTitle("Squeeze v" + version);
            aboutAlert.setIcon(R.mipmap.ic_launcher);
            aboutAlert.setMessage("Build Date: " + buildDate + "\n" + "by Michael May" + "\n" + "DroidLoft");
            aboutAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
