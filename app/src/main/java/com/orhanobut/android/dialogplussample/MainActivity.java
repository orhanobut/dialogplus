package com.orhanobut.android.dialogplussample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DialogPlus.Gravity.BOTTOM);
            }
        });

        findViewById(R.id.button_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DialogPlus.Gravity.CENTER);
            }
        });

        findViewById(R.id.button_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DialogPlus.Gravity.TOP);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showDialog(DialogPlus.Gravity gravity) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.simple_list_item_1, new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}
        );

        final DialogPlus dialog = new DialogPlus.Builder(this)
                .setHolder(new ListHolder())            // Optional, default:BasicHolder
                .setHeader(R.layout.header)             // Optional
                .setFooter(R.layout.footer)             // Optional
                .setCancelable(true)                    // Optional default:true
                .setGravity(gravity)                    // Optional default: BOTTOM
                .setAdapter(adapter)                    // This must be added
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
}
