package com.wang.customlinear;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wang.customlinear.adapter.AppAdapter;
import com.wang.customlinear.bean.AppBean;

import java.util.ArrayList;
import java.util.List;

public class SatrtActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AppAdapter adapter;
    List<AppBean> data = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satrt);
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        adapter = new AppAdapter(this);
        recyclerView.setAdapter(adapter);
        initData();
    }

    private void initData() {
        final int[] imageid = {R.mipmap.ic_aqy,R.mipmap.ic_bb,R.mipmap.ic_cz,R.mipmap.ic_kk,R.mipmap.ic_kr,
                R.mipmap.ic_qq,R.mipmap.ic_sg,R.mipmap.ic_xl,R.mipmap.ic_yk,R.mipmap.ic_yyy
        };
        final String[] names ={"爱奇艺","哔哩哔哩","赤足","快看","KingRoot","QQ","搜狗","迅雷","优酷","网易云"};

        for (int i = 0; i < imageid.length; i++) {
            data.add(new AppBean(imageid[i],names[i]));
        }
        adapter.setRefreshData(data);
        adapter.setmOnClickListener(new AppAdapter.MOnClickListener() {
            @Override
            public void mOnClick(View view, int position) {
                Intent intent = new Intent(SatrtActivity.this, DetailActivity.class);
                int viewMarginTop = view.getTop() + getResources().getDimensionPixelOffset(R.dimen.bar_view_height);
                intent.putExtra("viewMarginTop", viewMarginTop);
                intent.putExtra("imageId", imageid[position]);
                intent.putExtra("appName", names[position]);
                startActivity(intent);
                //去掉跳转动画实现无缝衔接
                overridePendingTransition(0, 0);
            }
        });
    }
}
