package com.sunsetrebel.catsy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.fragments.EventListDetailedFragment;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.utils.EventThemes;
import com.sunsetrebel.catsy.utils.EventThemesUtil;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.viewmodel.EventListViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private List<EventModel> eventList;
    private Context context;
    private SimpleDateFormat simpleDateFormat;
    private EventThemesUtil eventThemesUtil;
    private Map<Enum<?>, String> eventThemesEnumList;
    private Random rand = new Random();
    private EventListViewModel eventListViewModel;


    public EventListAdapter(Context context, FragmentActivity fragmentActivity, List<EventModel> eventList) {
        this.eventList = eventList;
        this.context = context;
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
        eventThemesUtil = new EventThemesUtil(context.getResources());
        eventThemesEnumList = eventThemesUtil.getEventThemesList();
        eventListViewModel = new ViewModelProvider(fragmentActivity).get(EventListViewModel.class);
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
        ImageUtils.loadImageView(context, eventList.get(position).getEventAvatar(), holder.ivEventAvatar, R.drawable.im_event_avatar_placeholder_64);
        //Set host avatar
        ImageUtils.loadImageView(context, eventList.get(position).getHostProfileImg(), holder.ivHostAvatar, R.drawable.im_cat_hearts);
        holder.tvEventTitle.setText(eventList.get(position).getEventTitle());
        holder.tvHostName.setText(context.getString(R.string.event_list_host_placeholder) + eventList.get(position).getHostName());
        holder.tvEventStartTime.setText(simpleDateFormat.format(eventList.get(position).getEventStartTime()));
        holder.tvEventLocation.setText(eventList.get(position).getEventLocation());
        holder.tvEventDescription.setText(eventList.get(position).getEventDescr());
        holder.tvEventParticipants.setText(String.format(Locale.getDefault(), "%d", eventList.get(position).getEventParticipants()));
        if (eventListViewModel.isEventLikedByUser(eventList.get(position).getEventId())) {
            holder.likeButton.setVisibility(View.INVISIBLE);
            holder.likeButton.setEnabled(false);
        } else {
            holder.likeButton.setVisibility(View.VISIBLE);
            holder.likeButton.setEnabled(true);
        }

        List<EventThemes> eventThemes = eventList.get(position).getEventThemes();
        if (eventThemes != null) {
            int tvCurrentSize = 0;
            for (EventThemes theme : eventThemes) {
                tvCurrentSize = tvCurrentSize + theme.toString().length();
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
                if (tvCurrentSize < 15) {
                    holder.linearLayout.addView(tv);
                }
            }
        }

        holder.itemLayout.setOnClickListener(v -> {
            eventListViewModel.setSelectedEvent(eventList.get(position));
            FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
            manager.beginTransaction().addToBackStack("EventListFragment")
                    .replace(R.id.frameLayoutMain, new EventListDetailedFragment()).commit();
        });

        holder.likeButton.setOnClickListener(v -> eventListViewModel.likeEvent(value -> {
            if (value) {
                holder.likeButton.setVisibility(View.INVISIBLE);
                CustomToastUtil.showSuccessToast(context, context.getResources().getText(R.string.event_liked_success).toString() + eventList.get(position).getEventTitle());
                Log.d("INFO", "You liked event: " + eventList.get(position).getEventId());
            } else {
                holder.likeButton.setVisibility(View.VISIBLE);
                CustomToastUtil.showFailToast(context, context.getResources().getText(R.string.event_liked_fail).toString() + eventList.get(position).getEventTitle());
                Log.d("INFO", "Failed to like event: " + eventList.get(position).getEventId());
            }
        }, eventList.get(position).getEventId()));
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
        private ConstraintLayout itemLayout;
        private TextView tvEventTitle, tvHostName, tvEventStartTime, tvEventLocation,
                tvEventDescription, tvEventParticipants;
        private ImageView ivHostAvatar, ivEventAvatar;
        private LinearLayout linearLayout;
        private ImageButton likeButton, shareButton;

        public ViewHolder(View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.cl_item_event_list);
            tvEventTitle = itemView.findViewById(R.id.textViewEventTitle);
            tvHostName = itemView.findViewById(R.id.textViewHostName);
            tvEventStartTime = itemView.findViewById(R.id.til_start_time);
            tvEventLocation = itemView.findViewById(R.id.textViewLocation);
            tvEventDescription = itemView.findViewById(R.id.tiet_event_description);
            tvEventParticipants = itemView.findViewById(R.id.textViewParticipants);
            ivHostAvatar = itemView.findViewById(R.id.imageViewHostAvatar);
            ivEventAvatar = itemView.findViewById(R.id.imageViewEventAvatar);
            linearLayout = itemView.findViewById(R.id.ll_tags);
            likeButton = itemView.findViewById(R.id.imageButtonLike);
            shareButton = itemView.findViewById(R.id.imageButtonShare);
        }
    }
}
