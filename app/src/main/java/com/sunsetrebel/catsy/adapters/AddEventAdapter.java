package com.sunsetrebel.catsy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.sunsetrebel.catsy.models.AddEvent;

import java.util.List;
import com.sunsetrebel.catsy.R;

public class AddEventAdapter extends RecyclerView.Adapter<AddEventAdapter.MyViewHolder> {
    private List<AddEvent> postagens;

    public AddEventAdapter(List<AddEvent> listaPostagens) {
        this.postagens = listaPostagens;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card_detailed, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AddEvent addEvent = postagens.get(position);
        holder.textName.setText(addEvent.getName());
        holder.textDate.setText(addEvent.getDate());
        holder.textLocation.setText(addEvent.getLocation());
        holder.textEventDescription.setText(addEvent.get_event_description());
        holder.textEventCreatorName.setText(addEvent.get_event_creator_name());
        holder.imageEventIcon.setImageResource(addEvent.getEvent_image());
        holder.EventCreatorPhotoPostagem.setImageResource(addEvent.get_event_creator_photo());
    }

    @Override
    public int getItemCount() {
        return postagens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textName;
        private TextView textDate;
        private TextView textLocation;
        private TextView textEventDescription;
        private TextView textEventCreatorName;
        private ImageView imageEventIcon;
        private ImageView EventCreatorPhotoPostagem;

        public MyViewHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.card_event_name);
            textDate = itemView.findViewById(R.id.card_event_date);
            textLocation = itemView.findViewById(R.id.card_event_location);
            textEventDescription = itemView.findViewById(R.id.card_event_detail_description);
            textEventCreatorName = itemView.findViewById(R.id.event_type);
            imageEventIcon = itemView.findViewById(R.id.card_event_icon);
            EventCreatorPhotoPostagem = itemView.findViewById(R.id.event_creator_photo);
        }
    }
}
