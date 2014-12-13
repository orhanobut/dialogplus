package com.orhanobut.android.dialogplussample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.orhanobut.android.dialogplus.BasicHolder;
import com.orhanobut.android.dialogplus.DialogPlus;
import com.orhanobut.android.dialogplus.GridHolder;
import com.orhanobut.android.dialogplus.ListHolder;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.simple_list_item_1, new String[]{"Item 1", "Item 2","Item 3","Item 4"}
        );
        final DialogPlus dialog = new DialogPlus.Builder(this)
                .setHolder(new ListHolder())    // Optional, default:BasicHolder
                .setHeader(R.layout.header)     // Optional
                .setFooter(R.layout.footer)     // Optional
                .setCancelable(true)            // Optional default:true
                .setGravity(Gravity.BOTTOM)     // Optional default:true
                .setAdapter(adapter)            // This must be added
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                })
                .create();
        dialog.show();


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }
}
