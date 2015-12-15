package com.example.feihe.healthcare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String ItemName;
    public static final String NAME = "itemName";
    private MyDB myDB;

    public static final String BMI = "BMI";
    public static final String BMIKGM2 = "BMI--kg/m2";

    private ArrayList<String> listName;
    private ArrayAdapter<String>arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMainView();
    }

    protected void setMainView(){
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.id_main_toolbar);
        toolbar.setTitle(R.string.app_name);

        myDB = MyDB.getInstance(this);
        listName = getListName();
        arrayAdapter =new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, listName);
        ListView listView = (ListView)findViewById(R.id.id_main_nameList);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ItemName = listName.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("不再记录‘" + ItemName + "’了吗？");
                builder.setPositiveButton("删了吧", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDB.deleteItemByName(ItemName);
                        myDB.deleteName(ItemName);
                        setMainView();
                    }
                });
                builder.setNegativeButton("留着吧", null);
                builder.create().show();
                return true;
            }
        });
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemName = listName.get(position);
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(NAME, ItemName);
                startActivity(intent);
            }
        });
    }

    //增加个记录名称
    public void newButton(View view){
        EditText addName = (EditText)findViewById(R.id.id_main_addName);
        ItemName = addName.getText().toString();
        if(ItemName.equalsIgnoreCase(BMI)||ItemName.equalsIgnoreCase(BMIKGM2)){
            Toast toast=Toast.makeText(MainActivity.this,R.string.data_auto_handle,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        if(ItemName.equalsIgnoreCase("")){
            Toast toast=Toast.makeText(MainActivity.this,R.string.name_empty,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        boolean isExist = false;
        for(int i=0;i<listName.size();i++){
            if(ItemName.equals(listName.get(i))){
                isExist = true;
                break;
            }

        }
        if(isExist){
            Toast toast=Toast.makeText(MainActivity.this,R.string.name_again,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        myDB.addName(ItemName);
        setMainView();
    }
    protected boolean isShowBMI(ArrayList<String> listName){
        int cnt=0;
        for(int i=0;i<listName.size();i++){
            if(listName.get(i).equals("身高cm"))
                cnt++;
            if(listName.get(i).equals("体重kg"))
                cnt++;
        }
        if(cnt>=2)
            return true;
        else
            return false;
    }
    protected ArrayList<String> getListName(){
        listName = myDB.queryName();
        if(isShowBMI(listName))
            listName.add(BMIKGM2);

        //将listName反转
        String temp1,temp2;
        int length = listName.size();
        for(int i=0; i<length/2; i++){
            temp1 = listName.get(i);
            temp2 = listName.get(length-1-i);
            listName.remove(i);
            listName.add(i, temp2);
            listName.remove(length - 1 - i);
            listName.add(length-1-i,temp1);
        }
        return listName;
    }
}
