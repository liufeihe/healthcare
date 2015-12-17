package com.example.feihe.healthcare;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by feihe on 2015/12/16.
 */
public class FragList extends Fragment {
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private View view;

    private MyDB myDB;
    private String name;
    private ArrayList<DataInfo>dataListInfo;
    private ArrayList<String>listName;
    private int length;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstance){
        view = inflater.inflate(R.layout.frag_list,container,false);
        listView = (ListView)view.findViewById(R.id.id_list_view);
        return view;
    }

    public void loadData(String key){
        name = key;
        myDB = MyDB.getInstance(getActivity());
        if(key.equalsIgnoreCase(MainActivity.BMIKGM2))
            dataListInfo=ResultActivity.generateBmi();
        else
            dataListInfo=myDB.queryItem(key);

        length = dataListInfo.size();
        listName = new ArrayList<>();
        for(int i=length-1; i>=0; i--){//listView把最新的数据显示在前面
            DataInfo dataInfo = dataListInfo.get(i);
            String str = "第"+(i+1)+"个-"+dataInfo.getData()+"\n\t-"+dataInfo.getTime();
            listName.add(str);
        }
        adapter= new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_single_choice,listName);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (name.equalsIgnoreCase(MainActivity.BMIKGM2)) {
                    Toast toast = Toast.makeText(getActivity(), R.string.data_auto_handle, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LinearLayout dataInput = (LinearLayout)ResultActivity.getMyWindow().getLayoutInflater().inflate(R.layout.data_input, null);
                final int dataIndex = length-1-position;
                final int itemId = dataListInfo.get(dataIndex).getId();//表项的主键值
                final EditText editText = (EditText) dataInput.findViewById(R.id.id_data);
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(dataInput);
                builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dataS = editText.getText().toString();
                        if (!ResultActivity.isNumber(dataS)) {
                            Toast toast = Toast.makeText(getActivity(), R.string.data_err, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        if (dataS.equalsIgnoreCase("")) {
                            Toast toast = Toast.makeText(getActivity(), R.string.data_empty, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        //更新数据库
                        Float dataF = Float.parseFloat(dataS);
                        float data = dataF;
                        myDB.updateItemById(itemId, data);
                        dataListInfo.get(dataIndex).setData(data);
                        loadData(name);
                    }
                });
                builder.setNegativeButton("删之", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDB.deleteItemById(itemId);
                        dataListInfo.remove(dataIndex);
                        loadData(name);
                    }
                });
                builder.create().show();
            }
        });
    }
}
