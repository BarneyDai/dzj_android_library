package com.dzj.app.dzj_20160411;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.dzj.app.basiclibrary.BasicActivity.BasicActivity;
import com.dzj.app.customcamera.CaptureActivity;
import com.dzj.app.xlistview.XListView;

public class MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        super.initView();
        XListView x = (XListView) findViewById(R.id.XListView_MainActivity);

        Resources res = getResources();
        String[] planets = res.getStringArray(R.array.array_main_modules);

        x.setAdapter(new ArrayAdapter<>(this,R.layout.adapter_main,planets));

        x.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 6:

                        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                        MainActivity.this.startActivity(intent);

                        break;
                }
            }
        });
    }
}
