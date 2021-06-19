package com.sunsetrebel.catsy.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import com.sunsetrebel.catsy.adapters.AddEventAdapter;
import com.sunsetrebel.catsy.models.AddEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sunsetrebel.catsy.R;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventsListActivity extends AppCompatActivity {
    ImageButton map_button;
    private RecyclerView recyclerPostagem;
    private List<AddEvent> postagens = new ArrayList<>();
    TextView infoTv;

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

        AddEventAdapter adapter = new AddEventAdapter(postagens);
        recyclerPostagem.setAdapter(adapter);

        //Event creation dialog opening
        LinearLayout EventlistFilter;
        EventlistFilter = findViewById(R.id.eventlist_filter);
        EventlistFilter = findViewById(R.id.eventlist_filter);
        EventlistFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });


    }

    //Function to display the custom dialog.
    void showCustomDialog() {
        final Dialog dialog = new Dialog(EventsListActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.event_create_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.ui_rounded_corners);
        //Initializing the views of the dialog.
        final EditText textName = dialog.findViewById(R.id.card_event_name);
        final EditText textDate = dialog.findViewById(R.id.card_event_date);
        final EditText  textLocation = dialog.findViewById(R.id.card_event_location);
        final EditText  textEventDescription = dialog.findViewById(R.id.card_event_detail_description);
        final EditText textEventCreatorName = dialog.findViewById(R.id.event_type);
        final CheckBox termsCb = dialog.findViewById(R.id.terms_cb);
        Button submitButton = dialog.findViewById(R.id.submit_button);
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.DialogTheme);



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textName.getText().toString();
                String date = textDate.getText().toString();
                String location = textLocation.getText().toString();
                String event_description = textEventDescription.getText().toString();
                String event_creator_name = textEventCreatorName.getText().toString();
                int event_image = 1;
                int event_creator_photo = 1;
                Boolean hasAccepted = termsCb.isChecked();
                // populateInfoTv(name, age, hasAccepted);
                dialog.dismiss();
               addPostagens(name, date, location, event_description, event_creator_name, event_image, event_creator_photo);
            }
        });

        dialog.show();

    }

    public void addPostagens(String name, String date, String location, String event_description, String event_creator_name, 
                             int event_image, int event_creator_photo) {
        AddEvent post = new AddEvent(
                "Kiev flight trip",
                "Tomorrow at 20:00",
                "Kiev, Podol",
                "Waiting for you!",
                "Sonya",
                R.drawable.im_event_icon_example_1,
                R.drawable.im_cat_bright
        );
        this.postagens.add(post);}

    public void prepararPostagens() {
        AddEvent post = new AddEvent(
                "Kiev flight trip",
                "Tomorrow at 20:00",
                "Kiev, Podol",
                "Waiting for you!",
                "Sonya",
                R.drawable.im_event_icon_example_1,
                R.drawable.im_cat_bright
               );
        this.postagens.add(post);

        post = new AddEvent(
                "Downtown excurtion",
                "20.06.2021 at 13:00",
                "Kiev, Maidan",
                "You will see Kiev",
                "Valentin",
                R.drawable.im_event_icon_example_2,
                R.drawable.im_cat_profile);
        this.postagens.add(post);

        post = new AddEvent("Paris in Kiev afterparty",
                "Today at 19:00",
                "Kiev, France Q",
                "Come to Paris quarter",
                "Joseph",
                R.drawable.im_event_icon_example_3,
                R.drawable.ic_catsy_icon
        );
        this.postagens.add(post);

        post = new AddEvent("Masha forest survive",
                "30.06.2021 at 10:00",
                "Kiev, Bilychi",
                "1 knife - 1 life",
                "Masha",
                R.drawable.im_event_icon_example_4,
                R.drawable.im_cat_dark_40);
        this.postagens.add(post);

    }
}