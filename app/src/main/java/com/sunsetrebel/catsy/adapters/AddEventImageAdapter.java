package com.sunsetrebel.catsy.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import com.sunsetrebel.catsy.R;


import java.io.File;
import java.util.ArrayList;

public class AddEventImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> list = new ArrayList<>();
    private Context context;

    public AddEventImageAdapter(Context context) {
        this.context = context;
    }

    public void addImage(ArrayList<String> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.add_event_image, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Uri imageUri = Uri.fromFile(new File(list.get(position)));// For files on device
        //Log.e("hello", "- " + imageUri.toString());
        File f = new File(list.get(position));
        Bitmap d = new BitmapDrawable(context.getResources(), f.getAbsolutePath()).getBitmap();
        /*Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(
            500f, com.fxn.utility.Utility.rotate(d,list.get(position).getOrientation()));*/
        ((Holder) holder).iv.setImageBitmap(d);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public ImageView iv;


        public Holder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.addEventImageView);
            iv.setClipToOutline(true);

        }
    }
}
