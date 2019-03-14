package com.kayali_developer.smartphonecafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kayali_developer.smartphonecafe.data.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {
    private static final int THUMBNAIL_SIZE = 320;
    private List<User> users;
    private MembersAdapterListener membersAdapterListener;

    interface MembersAdapterListener{
        void onMemberLongClicked(User user);
        void onRateMemberClicked(User ratedUser);
    }

    public MembersAdapter(MembersAdapterListener membersAdapterListener) {
        this.users = new ArrayList<>();
        this.membersAdapterListener = membersAdapterListener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_member;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User currentUser = users.get(position);
        Picasso.get().load(currentUser.getImageURL())
                .transform(new CropCircleTransformation())
                .placeholder(R.drawable.no_image_available)
                .error(R.drawable.no_image_available)
                .into(holder.iv_member_thumbnail);
        if (currentUser.getFirstName() != null){
            holder.tv_first_name.setText(currentUser.getFirstName());
        }

        if (currentUser.getLastName() != null){
            holder.tv_last_name.setText(currentUser.getLastName());
        }

        if (currentUser.getEmail() != null){
            holder.tv_email.setText(currentUser.getEmail());
        }

        holder.iv_rating_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membersAdapterListener.onRateMemberClicked(currentUser);
            }
        });

        holder.member_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                membersAdapterListener.onMemberLongClicked(currentUser);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (users == null) return 0;
        return users.size();
    }

    public void setData(List<User> users){
        this.users = users;
        notifyDataSetChanged();
    }

    public void removeMember(User members){
        try {
            this.users.remove(members);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.member_layout)
        ConstraintLayout member_layout;

        @BindView(R.id.iv_member_thumbnail)
        ImageView iv_member_thumbnail;

        @BindView(R.id.tv_first_name)
        TextView tv_first_name;

        @BindView(R.id.tv_last_name)
        TextView tv_last_name;

        @BindView(R.id.tv_email)
        TextView tv_email;

        @BindView(R.id.iv_rating_bar)
        ImageView iv_rating_bar;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
