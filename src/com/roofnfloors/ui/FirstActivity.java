package com.roofnfloors.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mapslist.R;
import com.roofnfloors.util.ToastHandler;
import com.roofnfloors.util.Utility;

public class FirstActivity extends Activity {

    private Button tabView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ac = getActionBar();
        ac.setTitle(R.string.main_page_title);
        ac.setDisplayShowHomeEnabled(true);
        setContentView(R.layout.first_activity);
        tabView = (Button) findViewById(R.id.tabsView);
        if (tabView != null) {
            tabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utility.isConnectionAvailable(getApplicationContext())) {
                        Intent in = new Intent(getApplicationContext(),
                                RoofnFloorsActivity.class);
                        startActivity(in);
                    } else {
                        ToastHandler.displayToast(R.string.network_error,
                                getApplicationContext());
                    }
                }
            });
        }

    }

}
