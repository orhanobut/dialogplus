package com.orhanobut.android.dialogplussample;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.orhanobut.dialogplus.BasicHolder;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;


public class MainActivity extends Activity {

    private RadioGroup radioGroup;
    private CheckBox headerCheckBox;
    private CheckBox footerCheckBox;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        headerCheckBox = (CheckBox) findViewById(R.id.header_check_box);
        footerCheckBox = (CheckBox) findViewById(R.id.footer_check_box);

        findViewById(R.id.button_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(
                        radioGroup.getCheckedRadioButtonId(),
                        DialogPlus.Gravity.BOTTOM,
                        headerCheckBox.isChecked(),
                        footerCheckBox.isChecked()
                );
            }
        });

        findViewById(R.id.button_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(
                        radioGroup.getCheckedRadioButtonId(),
                        DialogPlus.Gravity.CENTER,
                        headerCheckBox.isChecked(),
                        footerCheckBox.isChecked()
                );
            }
        });

        findViewById(R.id.button_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(
                        radioGroup.getCheckedRadioButtonId(),
                        DialogPlus.Gravity.TOP,
                        headerCheckBox.isChecked(),
                        footerCheckBox.isChecked()
                );
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showDialog(int holderId, DialogPlus.Gravity gravity, boolean showHeader, boolean showFooter) {
        boolean isGrid;
        Holder holder;
        switch (holderId) {
            case R.id.basic_holder_radio_button:
                holder = new BasicHolder();
                isGrid = false;
                break;
            case R.id.list_holder_radio_button:
                holder = new ListHolder();
                isGrid = false;
                break;
            default:
                holder = new GridHolder(3);
                isGrid = true;
        }

        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, isGrid);
        if (showHeader && showFooter) {
            showCompleteDialog(holder, gravity, adapter);
            return;
        }

        if (showHeader && !showFooter) {
            showNoFooterDialog(holder, gravity, adapter);
            return;
        }

        if (!showHeader && showFooter) {
            showNoHeaderDialog(holder, gravity, adapter);
            return;
        }

        showOnlyContentDialog(holder, gravity, adapter);
    }

    private void showCompleteDialog(Holder holder, DialogPlus.Gravity gravity, BaseAdapter adapter) {
        final DialogPlus dialog = new DialogPlus.Builder(this)
                .setHolder(holder)                      // Optional, default:BasicHolder
                .setHeader(R.layout.header)             // Optional
                .setFooter(R.layout.footer)             // Optional
                .setCancelable(true)                    // Optional default:true
                .setGravity(gravity)                    // Optional default: BOTTOM
                .setAdapter(adapter)                    // This must be added
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Item clicked", Toast.LENGTH_LONG).show();
                    }
                })
                .create();
        dialog.show();
    }

    private void showNoFooterDialog(Holder holder, DialogPlus.Gravity gravity, BaseAdapter adapter) {
        final DialogPlus dialog = new DialogPlus.Builder(this)
                .setHolder(holder)                      // Optional, default:BasicHolder
                .setHeader(R.layout.header)             // Optional
                .setCancelable(true)                    // Optional default:true
                .setGravity(gravity)                    // Optional default: BOTTOM
                .setAdapter(adapter)                    // This must be added
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Item clicked", Toast.LENGTH_LONG).show();
                    }
                })
                .create();
        dialog.show();
    }

    private void showNoHeaderDialog(Holder holder, DialogPlus.Gravity gravity, BaseAdapter adapter) {
        View footer = LayoutInflater.from(this).inflate(R.layout.footer, null);
        final DialogPlus dialog = new DialogPlus.Builder(this)
                .setHolder(holder)                      // Optional, default:BasicHolder
                .setFooter(footer)             // Optional
                .setCancelable(true)                    // Optional default:true
                .setGravity(gravity)                    // Optional default: BOTTOM
                .setAdapter(adapter)                    // This must be added
                .setOnViewClickLister()
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Item clicked", Toast.LENGTH_LONG).show();
                    }
                }).create();

        footer.findViewById(R.id.footer_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showOnlyContentDialog(Holder holder, DialogPlus.Gravity gravity, BaseAdapter adapter) {
        final DialogPlus dialog = new DialogPlus.Builder(this)
                .setHolder(holder)                      // Optional, default:BasicHolder
                .setCancelable(true)                    // Optional default:true
                .setGravity(gravity)                    // Optional default: BOTTOM
                .setAdapter(adapter)                    // This must be added
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Item clicked", Toast.LENGTH_LONG).show();
                    }
                })
                .create();
        dialog.show();
    }
}
