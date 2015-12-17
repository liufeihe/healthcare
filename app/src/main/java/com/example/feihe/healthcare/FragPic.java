package com.example.feihe.healthcare;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

/**
 * Created by feihe on 2015/12/16.
 */
public class FragPic extends Fragment {
    private View view;

    private MyDB myDB;
    private ArrayList<DataInfo> dataListInfo;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstance){
        view = inflater.inflate(R.layout.frag_pic,container,false);
        return view;
    }

    public void loadData(String key){
        myDB = MyDB.getInstance(getActivity());
        if(key.equalsIgnoreCase(MainActivity.BMIKGM2))
            dataListInfo=ResultActivity.generateBmi();
        else
            dataListInfo=myDB.queryItem(key);

        int length = dataListInfo.size();
        float[] data = new float[length];
        for(int i=0; i<length; i++){
            DataInfo dataInfo = dataListInfo.get(i);
            data[i] = dataInfo.getData();
        }

        ResultView picView = (ResultView)view.findViewById(R.id.id_pic_view);
        picView.setData(key, data);
        picView.invalidate();
    }
}
