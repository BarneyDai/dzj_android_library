package com.dzj.app.basiclibrary.BasicActivity.HeaderActivity;

import android.os.Bundle;
import android.view.Window;

import com.dzj.app.basiclibrary.BasicActivity.BasicActivity;
import com.dzj.app.basiclibrary.BasicActivity.HeaderActivity.Views.HeaderView;
import com.dzj.app.basiclibrary.R;

/**
 * 拥有标题的Activity
 */
public class HeaderActivity extends BasicActivity {

    private HeaderView headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.activityTitlebarNoSearch);
        requestWindowFeature(Window.FEATURE_NO_TITLE | Window.FEATURE_CUSTOM_TITLE);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.view_header);
//        headView =(HeaderView) findViewById(R.id.headView);

    }


}
