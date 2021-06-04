package com.sunsetrebel.catsy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sunsetrebel.MapsActivity;
import com.sunsetrebel.adapter.PostagemAdapter;
import com.sunsetrebel.model.Postagem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class EventsList extends AppCompatActivity {
    ImageButton map_button;
    private RecyclerView recyclerPostagem;
    // Criando uma lista de postagens conforme model
    private List<Postagem> postagens = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }

        recyclerPostagem = findViewById(R.id.list_background);

        // Definir layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerPostagem.setLayoutManager(layoutManager);

        // Configuração HORIZONTAL da aplicação
        // LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        // layoutManager1.setOrientation(LinearLayout.HORIZONTAL);
        // recyclerPostagem.setLayoutManager(layoutManager1);

        // Configuração GRID da aplicação
        // RecyclerView.LayoutManager layoutManager3 = new GridLayoutManager(this, 2);
        // recyclerPostagem.setLayoutManager(layoutManager3);

        // Definir o adapter
        this.prepararPostagens();

        PostagemAdapter adapter = new PostagemAdapter(postagens);
        recyclerPostagem.setAdapter(adapter);
        LinearLayout switch_map;
        // ImageButton mBottton = findViewById(R.id.add_button);
        // events_button = findViewById(R.id.events_button);
        switch_map = findViewById(R.id.switch_map);
        switch_map = findViewById(R.id.switch_map);
        switch_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //Map button finishes listview activity and comes back to the map
           //     Intent intent = new Intent (EventsList.this, MapsActivity.class);
           //   startActivity(intent);
                finish();
            }
        });
        //CardView examples
    /*    CardView mBottton = findViewById(R.id.cardView_example_1);
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

        });*/
/*
        CardView mBottton_usual = findViewById(R.id.cardView_example_2);
        mBottton_usual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                showBottomSheetDialogUsual();
            }

            private void showBottomSheetDialogUsual() {

                final BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(EventsList.this);
                bottomSheetDialog2.setContentView(R.layout.bottom_sheet_dialog_usual_layout);

                LinearLayout copy = bottomSheetDialog2.findViewById(R.id.copyLinearLayout);
                LinearLayout share = bottomSheetDialog2.findViewById(R.id.shareLinearLayout);
                // LinearLayout upload = bottomSheetDialog.findViewById(R.id.uploadLinearLayout);
                LinearLayout download = bottomSheetDialog2.findViewById(R.id.download);
                LinearLayout delete = bottomSheetDialog2.findViewById(R.id.delete);

                bottomSheetDialog2.show();
            }

        });*/
    }

    public void prepararPostagens() {
        Postagem post = new Postagem(
                "Kiev flight trip",
                "Tomorrow at 20:00",
                "Kiev, Podol",
                "Sonya",
                R.drawable.imagem1);
        this.postagens.add(post);

        post = new Postagem(
                "Downtown excurtion",
                "20.06.2021 at 13:00",
                "Kiev, Maidan",
                "Valentin",
                R.drawable.imagem2);
        this.postagens.add(post);

        post = new Postagem("Paris in Kiev afterparty",
                "Today at 19:00",
                "Kiev, France Q",
                "Joseph",
                R.drawable.imagem3);
        this.postagens.add(post);

        post = new Postagem("Masha forest survive",
                "30.06.2021 at 10:00",
                "Kiev, Bilychi",
                "Masha",
                R.drawable.imagem4);
        this.postagens.add(post);

    }
}