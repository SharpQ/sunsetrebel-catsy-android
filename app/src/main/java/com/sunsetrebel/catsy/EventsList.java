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
        FloatingActionButton fab;
        fab = findViewById(R.id.fab);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void prepararPostagens() {
        Postagem post = new Postagem(
                "Kiev flight trip",
                "Tomorrow at 20:00",
                "Kiev, Podol",
                "Waiting for you!",
                "Sonya",
                R.drawable.imagem1,
                R.drawable.ic_cat_bright
               );
        this.postagens.add(post);

        post = new Postagem(
                "Downtown excurtion",
                "20.06.2021 at 13:00",
                "Kiev, Maidan",
                "You will see Kiev",
                "Valentin",
                R.drawable.imagem2,
                R.drawable.ic_cat_profile);
        this.postagens.add(post);

        post = new Postagem("Paris in Kiev afterparty",
                "Today at 19:00",
                "Kiev, France Q",
                "Come to Paris quarter",
                "Joseph",
                R.drawable.imagem3,
                R.drawable.ic_catsy_icon
        );
        this.postagens.add(post);

        post = new Postagem("Masha forest survive",
                "30.06.2021 at 10:00",
                "Kiev, Bilychi",
                "1 knife - 1 life",
                "Masha",
                R.drawable.imagem4,
                R.drawable.ic_cat_dark_40);
        this.postagens.add(post);

    }
}