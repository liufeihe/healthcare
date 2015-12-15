package com.example.feihe.healthcare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {

    private String key;
    private ArrayList<DataInfo>dataListInfo;
    private int length = 0;

    private static final int MODE_PIC=0;
    private static final int MODE_LIST=1;
    private int viewMode = MODE_LIST;

    private MyDB myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = MyDB.getInstance(this);

        //获取传过来的数据
        Intent intent=getIntent();
        key=intent.getStringExtra(MainActivity.NAME);
        if(key.equalsIgnoreCase(MainActivity.BMIKGM2))
            dataListInfo = generateBmi();
        else
            dataListInfo=myDB.queryItem(key);

        setResultListView();
    }

    public void setResultPicView(){
        setContentView(R.layout.resultview_pic);
        Toolbar toolbar = (Toolbar)findViewById(R.id.id_resultview_pic_toolbar);
        toolbar.setTitle(key);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ResultView resultView = (ResultView)findViewById(R.id.id_resultView_pic);
        length = dataListInfo.size();
        float[] dataList = new float[length];
        for(int i=0; i<length; i++){
            DataInfo dataInfo = dataListInfo.get(i);
            dataList[i] = dataInfo.getData();
        }
        resultView.setData(key, dataList);

        viewMode = MODE_PIC;
    }
    public void setResultListView(){
        setContentView(R.layout.resultview_list);
        Toolbar toolbar = (Toolbar)findViewById(R.id.id_reusltview_list_toolbar);
        toolbar.setTitle(key);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> dataListStr = new ArrayList<>();
        length = dataListInfo.size();
        for(int i=length-1; i>=0; i--){//listView把最新的数据显示在前面
            DataInfo dataInfo = dataListInfo.get(i);
            String str = "第"+(i+1)+"个-"+dataInfo.getData()+"\n\t-"+dataInfo.getTime();
            dataListStr.add(str);
        }
        ArrayAdapter<String> arrayAdapter =new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, dataListStr);
        final ListView listView = (ListView)findViewById(R.id.id_resultView_list);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (key.equalsIgnoreCase(MainActivity.BMIKGM2)) {
                    Toast toast = Toast.makeText(ResultActivity.this, R.string.data_auto_handle, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                LinearLayout modify = (LinearLayout) getLayoutInflater().inflate(R.layout.modify, null);
                final int dataIndex = length - 1 - position;
                final int itemId = dataListInfo.get(dataIndex).getId();//表项的主键值
                final EditText editData = (EditText) modify.findViewById(R.id.id_modify_data);
                editData.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(modify);
                builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String modifyData = editData.getText().toString();
                        if (!isNumber(modifyData)) {
                            Toast toast = Toast.makeText(ResultActivity.this, R.string.data_err, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        if (modifyData.equalsIgnoreCase("")) {
                            Toast toast = Toast.makeText(ResultActivity.this, R.string.data_empty, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        //更新数据库
                        Float dataF = Float.parseFloat(modifyData);
                        float data = dataF;
                        myDB.updateItemById(itemId, data);
                        dataListInfo.get(dataIndex).setData(data);
                        setResultListView();
                    }
                });
                builder.setNegativeButton("删之", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDB.deleteItemById(itemId);
                        dataListInfo.remove(dataIndex);
                        setResultListView();
                    }
                });
                builder.create().show();
            }
        });

        viewMode=MODE_LIST;
    }
    public void clearResultListView(){
        if (key.equalsIgnoreCase(MainActivity.BMIKGM2)) {
            Toast toast = Toast.makeText(ResultActivity.this, R.string.data_auto_handle, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return ;
        }
        if(dataListInfo.size() == 0){
            Toast toast=Toast.makeText(ResultActivity.this,"已经没有‘"+key+"’数据了",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this).setMessage("删除所有‘"+key+"’的记录？");
        builder.setPositiveButton("删之", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myDB.deleteItemByName(key);
                for (int i = dataListInfo.size() - 1; i >= 0; i--) {
                    dataListInfo.remove(i);
                }
                setResultListView();
            }
        });
        builder.setNegativeButton("算了", null);
        builder.create().show();
    }
    public void addResultItem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        LinearLayout modify = (LinearLayout)getLayoutInflater().inflate(R.layout.modify,null);
        final EditText editText = (EditText)modify.findViewById(R.id.id_modify_data);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(modify);
        builder.setPositiveButton("增加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String addData = editText.getText().toString();
                if (!isNumber(addData)) {
                    Toast toast = Toast.makeText(ResultActivity.this, R.string.data_err, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (addData.equalsIgnoreCase("")) {
                    Toast toast = Toast.makeText(ResultActivity.this, R.string.data_empty, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                Float dataF = Float.parseFloat(addData);
                float data = dataF;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
                Date currentTime = new Date();
                String ItemTime = formatter.format(currentTime);
                DataInfo dataInfo = new DataInfo(key, data, ItemTime);
                myDB.addItem(dataInfo);
                dataListInfo.add(dataInfo);
                if(viewMode == MODE_PIC)
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
        if(viewMode == MODE_PIC)
            getMenuInflater().inflate(R.menu.resultpicview_choice, menu);
        else
            getMenuInflater().inflate(R.menu.resultlistview_choice, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.id_choice_resultPicView:
                setResultPicView();
                return true;
            case R.id.id_choice_resultListView:
                setResultListView();
                return true;
            case R.id.id_choice_listViewClear:
                clearResultListView();
                return true;
            case R.id.id_choice_addItem:
            case R.id.id_choice_addItem2:
                if (key.equalsIgnoreCase(MainActivity.BMIKGM2)) {
                    Toast toast = Toast.makeText(ResultActivity.this, R.string.data_auto_handle, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return true;
                }
                addResultItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected  ArrayList<DataInfo> generateBmi() {
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
            DataInfo dataInfo = new DataInfo(weightData.getId(), key, bmi, weightData.getTime());
            bmiInfo.add(dataInfo);
        }
        return bmiInfo;
    }

}
