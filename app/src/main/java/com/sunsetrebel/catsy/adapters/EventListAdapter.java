package com.sunsetrebel.catsy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sunsetrebel.catsy.models.EventModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.utils.EventThemes;
import com.sunsetrebel.catsy.utils.EventThemesService;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private List<EventModel> eventList;
    private Context context;
    private SimpleDateFormat simpleDateFormat;
    private EventThemesService eventThemesService;
    private Map<Enum<?>, String> eventThemesEnumList;
    private Random rand = new Random();


    public EventListAdapter(Context context, List<EventModel> eventList) {
        this.eventList = eventList;
        this.context = context;
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
        eventThemesService = new EventThemesService(context.getResources());
        eventThemesEnumList = eventThemesService.getEventThemesList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_list, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Set event avatar
        RequestOptions defaultOptionsEventAvatar = new RequestOptions()
                .error(R.drawable.im_background_concert);
        Glide.with(context)
                .setDefaultRequestOptions(defaultOptionsEventAvatar)
                .load(eventList.get(position).getEventAvatar())
                .into(holder.ivEventAvatar);
        //Set host avatar
        RequestOptions defaultOptionsHostAvatar = new RequestOptions()
                .error(R.drawable.im_cat_hearts);
        Glide.with(context)
                .setDefaultRequestOptions(defaultOptionsHostAvatar)
                .load(eventList.get(position).getUserProfileImg())
                .into(holder.ivHostAvatar);

        holder.tvEventTitle.setText(eventList.get(position).getEventTitle());
        holder.tvHostName.setText(context.getString(R.string.event_list_host_placeholder) + eventList.get(position).getUserName());
        holder.tvEventStartTime.setText(simpleDateFormat.format(eventList.get(position).getEventStartTime()));
        holder.tvEventLocation.setText(eventList.get(position).getEventLocation());
        holder.tvEventDescription.setText(eventList.get(position).getEventDescr());
        holder.tvEventParticipants.setText(String.format(Locale.getDefault(), "%d", eventList.get(position).getEventParticipants()));
        List<EventThemes> eventThemes = eventList.get(position).getEventThemes();
        if (eventThemes != null) {
            for (EventThemes theme : eventThemes) {
                TextView tv = new TextView(context);
                tv.setText("#" + eventThemesEnumList.get(theme));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                params.setMargins(0,0,0,0);
                tv.setLayoutParams(params);
                tv.setPadding(1,1,5,1);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextSize(14);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv.setTextColor(Color.rgb(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
                } else {
                    tv.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
                }
                Typeface typeface = ResourcesCompat.getFont(context, R.font.audiowide);
                tv.setTypeface(typeface);
                holder.linearLayout.addView(tv);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (eventList != null) {
            return eventList.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEventTitle;
        private TextView tvHostName;
        private TextView tvEventStartTime;
        private TextView tvEventLocation;
        private TextView tvEventDescription;
        private TextView tvEventParticipants;
        private ImageView ivHostAvatar;
        private ImageView ivEventAvatar;
        private LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.textViewEventTitle);
            tvHostName = itemView.findViewById(R.id.textViewHostName);
            tvEventStartTime = itemView.findViewById(R.id.til_start_time);
            tvEventLocation = itemView.findViewById(R.id.textViewLocation);
            tvEventDescription = itemView.findViewById(R.id.tiet_event_description);
            tvEventParticipants = itemView.findViewById(R.id.textViewParticipants);
            ivHostAvatar = itemView.findViewById(R.id.imageViewHostAvatar);
            ivEventAvatar = itemView.findViewById(R.id.imageViewEventAvatar);
            linearLayout = itemView.findViewById(R.id.ll_tags);
        }
    }
}
