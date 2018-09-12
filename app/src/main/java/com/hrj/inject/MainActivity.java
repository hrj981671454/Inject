package com.hrj.inject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eaju.inject.ContentView;
import com.eaju.inject.OnClick;
import com.eaju.inject.OnLongClick;
import com.eaju.inject.ViewInject;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.tvText)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView.setText("哈哈哈哈哈哈哈");
        textView.setTextSize(25);
    }


    @OnClick({R.id.tvText})
    public void onClick(View view) {
        Toast.makeText(this, "我被点了", Toast.LENGTH_SHORT).show();
    }

    @OnLongClick(R.id.tvText)
    public boolean onLongClick(View view) {
        Toast.makeText(this, "我被长按了", Toast.LENGTH_SHORT).show();
        return false;
    }

}
