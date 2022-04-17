package com.sunsetrebel.catsy.adapters;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.CommonUserModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.utils.ExternalSocialsUtil;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.viewmodel.NewEventViewModel;

import java.util.List;

public class InviteFriendsAdapter extends RecyclerView.Adapter<InviteFriendsAdapter.ViewHolder> {
    private List<CommonUserModel> listOfUsers;
    private Context context;
    private Fragment fragment;
    private NewEventViewModel newEventViewModel;

    public InviteFriendsAdapter(Fragment fragment, List<CommonUserModel> listOfUsers) {
        this.listOfUsers = listOfUsers;
        this.context = fragment.getContext();
        this.fragment = fragment;
        newEventViewModel = new ViewModelProvider(fragment.requireActivity()).get(NewEventViewModel.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invite_friends, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageUtils.loadImageView(context, listOfUsers.get(position).getUserProfileImg(),
                holder.imageUserProfile, R.drawable.im_cat_hearts);
        holder.tvUsername.setText(listOfUsers.get(position).getUserFullName());
        holder.tvUserId.setText(listOfUsers.get(position).getUserId());
        holder.checkBoxInviteFriend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newEventViewModel.addUserToInvited(listOfUsers.get(position));
            } else {
                newEventViewModel.removeUserToInvited(listOfUsers.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listOfUsers != null) {
            return listOfUsers.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView imageUserProfile;
        private TextView tvUsername, tvUserId;
        private CheckBox checkBoxInviteFriend;

        public ViewHolder(View itemView) {
            super(itemView);
            imageUserProfile = itemView.findViewById(R.id.image_invite_friends);
            tvUsername = itemView.findViewById(R.id.tv_username_invite_friends);
            tvUserId = itemView.findViewById(R.id.tv_userid_invite_friends);
            checkBoxInviteFriend = itemView.findViewById(R.id.checkbox_item_invite_friends);

        }
    }

}
