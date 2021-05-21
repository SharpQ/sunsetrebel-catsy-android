package com.sunsetrebel.catsy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sunsetrebel.MapsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class EventsList extends AppCompatActivity {
    ImageButton map_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }
        map_button = findViewById(R.id.map_button);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (EventsList.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        CardView mBottton = findViewById(R.id.cardView);
        mBottton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }

            private void showBottomSheetDialog() {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EventsList.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

                LinearLayout copy = bottomSheetDialog.findViewById(R.id.copyLinearLayout);
                LinearLayout share = bottomSheetDialog.findViewById(R.id.shareLinearLayout);
                // LinearLayout upload = bottomSheetDialog.findViewById(R.id.uploadLinearLayout);
                LinearLayout download = bottomSheetDialog.findViewById(R.id.download);
                LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);

                bottomSheetDialog.show();
            }

        });
    }
}