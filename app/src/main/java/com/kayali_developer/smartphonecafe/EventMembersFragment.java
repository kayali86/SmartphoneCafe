package com.kayali_developer.smartphonecafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kayali_developer.smartphonecafe.data.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventMembersFragment
        extends Fragment
        implements MembersAdapter.MembersAdapterListener {

    public static final String EVENT_MEMBERS_TAG = "EventMembersFragmentTag";

    @BindView(R.id.rv_members)
    RecyclerView rvMembers;

    private MainActivity mActivity;
    private Unbinder unbinder;
    private MembersAdapter mAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = ((MainActivity) getContext());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMembers.setHasFixedSize(true);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MembersAdapter(this);
        rvMembers.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public void updateData(List<User> users) {
        mAdapter.setData(users);
    }

    @Override
    public void onMemberLongClicked(User user) {

    }

    @Override
    public void onRateMemberClicked(User ratedUser) {

    }
}
