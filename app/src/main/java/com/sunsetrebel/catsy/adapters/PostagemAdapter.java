package com.sunsetrebel.catsy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.sunsetrebel.catsy.models.Postagem;

import java.util.List;
import com.sunsetrebel.catsy.R;

public class PostagemAdapter extends RecyclerView.Adapter<PostagemAdapter.MyViewHolder> {
    private List<Postagem> postagens;

    public PostagemAdapter(List<Postagem> listaPostagens) {
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
        Postagem postagem = postagens.get(position);
        holder.textNome.setText(postagem.getNome());
        holder.textPostagem.setText(postagem.getPostagem());
        holder.textHorario.setText(postagem.getHorario());
        holder.textEventDescription.setText(postagem.get_event_description());
        holder.textEventCreatorName.setText(postagem.get_event_creator_name());
        holder.imagemPostagem.setImageResource(postagem.getImagem());
        holder.EventCreatorPhotoPostagem.setImageResource(postagem.get_event_creator_photo());
    }

    @Override
    public int getItemCount() {
        return postagens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textNome;
        private TextView textPostagem;
        private TextView textHorario;
        private TextView textEventDescription;
        private TextView textEventCreatorName;
        private ImageView imagemPostagem;
        private ImageView EventCreatorPhotoPostagem;

        public MyViewHolder(View itemView) {
            super(itemView);

            textNome = itemView.findViewById(R.id.card_event_name);
            textPostagem = itemView.findViewById(R.id.card_event_date);
            textHorario = itemView.findViewById(R.id.card_event_location);
            textEventDescription = itemView.findViewById(R.id.card_event_detail_description);
            textEventCreatorName = itemView.findViewById(R.id.event_creator_name);
            imagemPostagem = itemView.findViewById(R.id.card_event_icon);
            EventCreatorPhotoPostagem = itemView.findViewById(R.id.event_creator_photo);
        }
    }
}
