package com.sunsetrebel.catsy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.fragments.EventListDetailedFragment;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.utils.EventThemesUtil;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.viewmodel.EventListViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private List<EventModel> eventList;
    private Context context;
    private Fragment fragment;
    private SimpleDateFormat simpleDateFormat;
    private EventListViewModel eventListViewModel;
    private EventThemesUtil eventThemesUtil;


    public EventListAdapter(Fragment fragment, List<EventModel> eventList) {
        this.eventList = eventList;
        this.context = fragment.getContext();
        this.fragment = fragment;
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
        eventThemesUtil = EventThemesUtil.getInstance(context.getResources());
        eventListViewModel = new ViewModelProvider(fragment.requireActivity()).get(EventListViewModel.class);
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
        String usersCountValue = String.format(Locale.getDefault(), "%d", eventList.get(position).getEventParticipants());
        Integer eventMaxPersonInt = eventList.get(position).getEventMaxPerson();
        if (eventMaxPersonInt != null) {
            String eventMaxPersonString = String.format(Locale.getDefault(), "%d", eventMaxPersonInt);
            usersCountValue = usersCountValue.concat(" / ").concat(eventMaxPersonString);
        }
        //Set event avatar
        ImageUtils.loadImageView(context, eventList.get(position).getEventAvatar(), holder.ivEventAvatar, R.drawable.im_event_avatar_placeholder_64);
        //Set host avatar
        ImageUtils.loadImageView(context, eventList.get(position).getHostProfileImg(), holder.ivHostAvatar, R.drawable.im_cat_hearts);
        holder.tvEventTitle.setText(eventList.get(position).getEventTitle());
        holder.tvHostName.setText(eventList.get(position).getHostName());
        holder.tvEventStartTime.setText(simpleDateFormat.format(eventList.get(position).getEventStartTime()));
        holder.tvEventLocation.setText(eventList.get(position).getEventLocation());
        holder.tvEventDescription.setText(eventList.get(position).getEventDescr());
        holder.tvEventParticipants.setText(usersCountValue);
        if (eventListViewModel.isEventLikedByUser(eventList.get(position).getEventId())) {
            holder.likeButton.setVisibility(View.INVISIBLE);
            holder.likeButton.setEnabled(false);
        } else {
            holder.likeButton.setVisibility(View.VISIBLE);
            holder.likeButton.setEnabled(true);
        }

        eventThemesUtil.setEventThemesUI(eventList.get(position).getEventThemes(), fragment, holder.linearLayout, null);

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
                Log.d("DEBUG", "You liked event: " + eventList.get(position).getEventId());
            } else {
                holder.likeButton.setVisibility(View.VISIBLE);
                CustomToastUtil.showFailToast(context, context.getResources().getText(R.string.event_liked_fail).toString() + eventList.get(position).getEventTitle());
                Log.d("DEBUG", "Failed to like event: " + eventList.get(position).getEventId());
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
