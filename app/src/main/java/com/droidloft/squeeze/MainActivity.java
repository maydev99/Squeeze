package com.droidloft.squeeze;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private String version = "0.1", buildDate = "1-13-2017";
    private EditText sysET, diaET;
    private Button saveB;
    private GridView gridView;
    private SQLiteDatabase sqlDB;
    private ArrayList<String> myArrayList;
    private ArrayAdapter<String>myAdapter;
    private Cursor cursor;
    private String strDate, strDia, strSys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        idViews();
        openDB();
        getTheSQLData();


        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate();
                getEnteredValues();
                if(strDia.equals("") || strSys.equals("")){
                    Toast.makeText(MainActivity.this, "Data missing", Toast.LENGTH_SHORT).show();
                } else {
                    putDataIntoDB();
                }


            }
        });



    }

    private void putDataIntoDB() {
        sqlDB.execSQL("INSERT INTO mytable(date, sys, dia) VALUES('" + strDate + "','" + strSys + "','" + strDia + "');");
        Toast.makeText(MainActivity.this, "Blood Pressure Saved", Toast.LENGTH_SHORT).show();
        getTheSQLData();


    }

    private void getEnteredValues() {
        strDia = diaET.getText().toString();
        strSys = sysET.getText().toString();

    }

    private void getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/DD | hh:mma");
        strDate = sdf.format(cal.getTime());
        //Toast.makeText(MainActivity.this, "" + strDate, Toast.LENGTH_SHORT).show();

    }

    private void getTheSQLData() {
        myArrayList = new ArrayList<>();
        cursor = sqlDB.rawQuery("SELECT * FROM mytable ORDER BY date", null);
        int idColumn = cursor.getColumnIndex("id");
        int dateColumn = cursor.getColumnIndex("date");
        int sysColumn = cursor.getColumnIndex("sys");
        int diaColumn = cursor.getColumnIndex("dia");
        cursor.moveToFirst();

        if(cursor != null && cursor.getCount() > 0) {
            do{
                String id = cursor.getString(idColumn);
                String date = cursor.getString(dateColumn);
                String sys = cursor.getString(sysColumn);
                String dia = cursor.getString(diaColumn);

                myArrayList.add(date);
                myArrayList.add(sys);
                myArrayList.add(dia);
            } while (cursor.moveToNext());

            myAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.list_item_item, myArrayList);

            gridView.setAdapter(myAdapter);

        }


    }

    private void openDB() {
        try{
            sqlDB = this.openOrCreateDatabase("SqlDB", MODE_PRIVATE, null);
            sqlDB.execSQL("CREATE TABLE IF NOT EXISTS mytable " + "(id integer primary key, date text, sys text, dia text);");
            File database = getApplicationContext().getDatabasePath("MyDB.db");

        } catch (Exception e){
            Log.e("DATABASE ERROR", "Error Creating Database");
        }

    }

    private void idViews() {
        sysET = (EditText)findViewById(R.id.sysEditText);
        diaET = (EditText)findViewById(R.id.diaEditText);
        saveB = (Button)findViewById(R.id.saveButton);
        gridView = (GridView)findViewById(R.id.gridview);

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
