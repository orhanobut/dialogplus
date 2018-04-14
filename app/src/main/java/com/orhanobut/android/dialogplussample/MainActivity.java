package com.orhanobut.android.dialogplussample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.orhanobut.dialogplus.*;

public class MainActivity extends AppCompatActivity {

  private RadioGroup holderRadioGroup;
  private RadioGroup positionRadioGroup;
  private CheckBox headerCheckBox;
  private CheckBox footerCheckBox;
  private CheckBox expandedCheckBox;
  private CheckBox fixedHeaderCheckBox;
  private CheckBox fixedFooterCheckBox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    holderRadioGroup = findViewById(R.id.radio_group);
    positionRadioGroup = findViewById(R.id.positionRadioGroup);
    headerCheckBox = findViewById(R.id.header_check_box);
    footerCheckBox = findViewById(R.id.footer_check_box);
    expandedCheckBox = findViewById(R.id.expanded_check_box);
    fixedHeaderCheckBox = findViewById(R.id.fixedHeaderCheckBox);
    fixedFooterCheckBox = findViewById(R.id.fixedFooterCheckBox);

    findViewById(R.id.showDialogButton).setOnClickListener(v -> showDialogPlus());

  }

  private void showDialogPlus() {

    int holderId = holderRadioGroup.getCheckedRadioButtonId();
    boolean showHeader = headerCheckBox.isChecked();
    boolean showFooter = footerCheckBox.isChecked();
    boolean fixedHeader = fixedHeaderCheckBox.isChecked();
    boolean fixedFooter = fixedFooterCheckBox.isChecked();
    boolean expanded = expandedCheckBox.isChecked();
    int gravity;
    switch (positionRadioGroup.getCheckedRadioButtonId()) {
      case R.id.topPosition:
        gravity = Gravity.TOP;
        break;
      case R.id.centerPosition:
        gravity = Gravity.CENTER;
        break;
      default:
        gravity = Gravity.BOTTOM;
    }

    boolean isGrid;
    Holder holder;
    switch (holderId) {
      case R.id.basic_holder_radio_button:
        holder = new ViewHolder(R.layout.content);
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
    DialogPlusBuilder builder = DialogPlus.newDialog(this)
        .setContentHolder(holder);

    int header = showHeader ? R.layout.header : -1;
    if (header != -1) {
      builder.setHeader(R.layout.header, fixedHeader);
    }

    int footer = showFooter ? R.layout.footer : -1;
    if (footer != -1) {
      builder.setFooter(R.layout.footer, fixedFooter);
    }
    builder.setCancelable(true)
        .setGravity(gravity)
        .setAdapter(adapter)
        .setOnClickListener((dialog, view) -> {
          if (view instanceof TextView) {
            TextView textView = (TextView) view;
            toast(textView.getText().toString());
          }
        })
        .setOnItemClickListener((dialog, item, view, position) -> {
          TextView textView = view.findViewById(R.id.text_view);
          toast(textView.getText().toString());
        })
//        .setOnDismissListener(dismissListener)
        .setExpanded(expanded)
//        .setContentWidth(800)
        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        .setOnCancelListener(dialog -> toast("cancelled"))
        .setOverlayBackgroundResource(android.R.color.transparent);
//        .setContentBackgroundResource(R.drawable.corner_background)
    //                .setOutMostMargin(0, 100, 0, 0)
    builder.create().show();
  }

  private void toast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}
