package com.sunsetrebel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.sunsetrebel.model.Postagem;

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
        holder.textEventCreatorName.setText(postagem.get_event_creator_name());
        holder.imagemPostagem.setImageResource(postagem.getImagem());
    }

    @Override
    public int getItemCount() {
        return postagens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textNome;
        private TextView textPostagem;
        private TextView textHorario;
        private TextView textEventCreatorName;
        private ImageView imagemPostagem;

        public MyViewHolder(View itemView) {
            super(itemView);

            textNome = itemView.findViewById(R.id.card_event_name);
            textPostagem = itemView.findViewById(R.id.card_event_date);
            textHorario = itemView.findViewById(R.id.card_event_location);
            textEventCreatorName = itemView.findViewById(R.id.event_creator_name);
            imagemPostagem = itemView.findViewById(R.id.card_event_icon);
        }
    }
}
