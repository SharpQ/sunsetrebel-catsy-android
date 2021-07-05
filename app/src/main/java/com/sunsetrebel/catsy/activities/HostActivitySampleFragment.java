package com.sunsetrebel.catsy.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.Size;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.fragments.SampleFragment;

import java.util.List;

public class HostActivitySampleFragment extends AppCompatActivity implements Testable {
    private SampleFragment fragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_activity_sample_fragment);
        fragment = (SampleFragment) getSupportFragmentManager().findFragmentById(R.id.sample_fragment);
    }

    @Override public List<String> getFilePaths() {
        return fragment.getFilePaths();
    }

    @Override public Size getSize() {
        return fragment.getSize();
    }

    @Override
    public List<FileData> getFileDatas() {
        return fragment.getFileDatas();
    }
}
