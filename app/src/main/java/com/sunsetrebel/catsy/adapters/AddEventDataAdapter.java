package com.sunsetrebel.catsy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.sunsetrebel.catsy.models.AddEventModel;

import java.util.List;
import com.sunsetrebel.catsy.R;

public class AddEventDataAdapter extends RecyclerView.Adapter<AddEventDataAdapter.MyViewHolder> {
    private List<AddEventModel> postagens;

    public AddEventDataAdapter(List<AddEventModel> listaPostagens) {
        this.postagens = listaPostagens;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        AddEventModel addEventModel = postagens.get(position);
        holder.textName.setText(addEventModel.getName());
        holder.textDate.setText(addEventModel.getDate());
        holder.textLocation.setText(addEventModel.getLocation());
        holder.textEventDescription.setText(addEventModel.get_event_description());
        holder.textEventCreatorName.setText(addEventModel.get_event_creator_name());
        holder.imageEventIcon.setImageResource(addEventModel.getEvent_image());
        holder.EventCreatorPhotoPostagem.setImageResource(addEventModel.get_event_creator_photo());
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


            textName = itemView.findViewById(R.id.textViewEventTitle);
            textDate = itemView.findViewById(R.id.textViewStartTime);
            textLocation = itemView.findViewById(R.id.textViewLocation);
            textEventDescription = itemView.findViewById(R.id.textViewEventDescription);
            textEventCreatorName = itemView.findViewById(R.id.textViewHostName);
            imageEventIcon = itemView.findViewById(R.id.imageViewEventAvatar);
            EventCreatorPhotoPostagem = itemView.findViewById(R.id.imageViewHostAvatar);


        }
    }

}
