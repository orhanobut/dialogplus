package com.orhanobut.android.dialogplussample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.orhanobut.android.dialogplus.DialogPlus;
import com.orhanobut.android.dialogplus.GridHolder;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, new String[]{"adfssdf", "asdfasd","asdfasdf","asdfasdf"}
        );
        final DialogPlus dp = new DialogPlus.Builder()
                .from(this)
               // .setHeader(R.layout.item_bottomupdialog)
               // .setFooter(R.layout.item_bottomupdialog)
                .setHolder(new GridHolder(3))
                .setCancelable(true)
                .setGravity(Gravity.BOTTOM)
                .setAdapter(adapter)
                .setScreenType(DialogPlus.ScreenType.HALF)
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                })
                .create();
        dp.show();


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dp.show();
            }
        });
    }
}
