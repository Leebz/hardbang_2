package com.sukaiyi.hardbang;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView.OnScrollListener;
public class activity_personal_activity extends AppCompatActivity implements OnItemClickListener ,OnScrollListener{


    private ListView listView;
    private SimpleAdapter simple_adapter;
    private List<Map<String, Object>> list;
    private int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_activity);

//        listView = (ListView) findViewById(R.id.personal_list);
//        // 数据适配器的定义
//        String[] data = new String[] { "java", "C++", "JavaScript", "Php",
//                "Python" };
//        simple_adapter = new SimpleAdapter(activity_personal_activity.this,getData()
//				, R.layout.activity_personal_activity,
//				new String[] { "image", "text" }, new int[] { R.id.image,
//						R.id.text });
//		listView.setAdapter(simple_adapter);
//        listView.setAdapter(simple_adapter);
        // 设置ListView的元素被选中时的事件处理监听器
    }

    public List<Map<String, Object>> getData(){
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
