package com.example.feihe.healthcare;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {
    private String key;
    private Toolbar toolbar;

    private static final int MODE_PIC=0;
    private static final int MODE_LIST=1;
    private int viewMode = MODE_PIC;

    private static MyDB myDB;
    private FragList fragList;
    private FragPic fragPic;
    private static Window window;

    public static Window getMyWindow(){
        return window;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultview);
        toolbar = (Toolbar)findViewById(R.id.id_reusltview_toolbar);

        myDB = MyDB.getInstance(this);
        window = getWindow();
        fragList = (FragList)getFragmentManager().findFragmentById(R.id.id_frag_list);
        fragPic = (FragPic)getFragmentManager().findFragmentById(R.id.id_frag_pic);

        //获取传过来的数据
        Intent intent=getIntent();
        key=intent.getStringExtra(MainActivity.NAME);

        setResultPicView();
    }

    public void setResultPicView(){
        toolbar.setTitle(key);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragPic.loadData(key);
        FragmentTransaction transaction =  getFragmentManager().beginTransaction();
        transaction.hide(fragList);
        transaction.show(fragPic);
        transaction.commit();

        viewMode = MODE_PIC;
    }
    public void setResultListView(){
        toolbar.setTitle(key);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragList.loadData(key);
        FragmentTransaction transaction =  getFragmentManager().beginTransaction();
        transaction.hide(fragPic);
        transaction.show(fragList);
        transaction.commit();

        viewMode=MODE_LIST;
    }
    public void clearResultListView(){
        if(key.equalsIgnoreCase(MainActivity.BMIKGM2)) {
            Toast toast = Toast.makeText(ResultActivity.this, R.string.data_auto_handle, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return ;
        }

        if(myDB.queryItem(key).size()==0){
            Toast toast = Toast.makeText(ResultActivity.this, "没有数据了", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this).setMessage("删除所有‘"+key+"’的记录？");
        builder.setPositiveButton("删之", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myDB.deleteItemByName(key);
                if(viewMode == MODE_PIC)
                    setResultPicView();
                else
                    setResultListView();
            }
        });
        builder.setNegativeButton("算了", null);
        builder.create().show();
    }
    public void addResultItem(){
        if (key.equalsIgnoreCase(MainActivity.BMIKGM2)) {
            Toast toast = Toast.makeText(ResultActivity.this, R.string.data_auto_handle, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        LinearLayout dataInput = (LinearLayout)getLayoutInflater().inflate(R.layout.data_input, null);
        final EditText editText = (EditText)dataInput.findViewById(R.id.id_data);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(dataInput);
        builder.setPositiveButton("增加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String dataS = editText.getText().toString();
                if (!isNumber(dataS)) {
                    Toast toast = Toast.makeText(ResultActivity.this, R.string.data_err, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (dataS.equalsIgnoreCase("")) {
                    Toast toast = Toast.makeText(ResultActivity.this, R.string.data_empty, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                Float dataF = Float.parseFloat(dataS);
                float data = dataF;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
                Date currentTime = new Date();
                String ItemTime = formatter.format(currentTime);
                DataInfo dataInfo = new DataInfo(key, data, ItemTime);
                myDB.addItem(dataInfo);
                if (viewMode == MODE_PIC)
                    setResultPicView();
                else
                    setResultListView();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }
    public static boolean isNumber(String data){
        int cntDot=0;
        for(int i=0;i<data.length();i++){
            char ch=data.charAt(i);
            if((ch<'0')||(ch>'9')){
                if(ch=='.'){
                    cntDot++;
                    if(cntDot==1&&i==0)
                        return false;
                    if(cntDot>1)
                        return false;
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.clear();
        getMenuInflater().inflate(R.menu.choice, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.choice_change:
                if(viewMode == MODE_LIST)
                    setResultPicView();
                else
                    setResultListView();
                return true;
            case R.id.choice_clear:
                clearResultListView();
                return true;
            case R.id.choice_add:
                addResultItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected  static ArrayList<DataInfo> generateBmi() {
        ArrayList<DataInfo> heightInfo = myDB.queryItem("身高cm");
        float averageHeight = 0;
        for (int i = 0; i < heightInfo.size(); i++) {
            float height = heightInfo.get(i).getData();
            averageHeight += height;
        }
        averageHeight = averageHeight / heightInfo.size();
        averageHeight = averageHeight / 100;//因为BMI中使用的是m

        ArrayList<DataInfo> weightInfo = myDB.queryItem("体重kg");
        ArrayList<DataInfo> bmiInfo = new ArrayList<>();
        for (int j = 0; j < weightInfo.size(); j++) {
            DataInfo weightData = weightInfo.get(j);
            float weight = weightData.getData();
            float bmi = (weight / averageHeight) / averageHeight;
            DataInfo dataInfo = new DataInfo(weightData.getId(), MainActivity.BMIKGM2, bmi, weightData.getTime());
            bmiInfo.add(dataInfo);
        }
        return bmiInfo;
    }

}
