package com.kayali_developer.smartphonecafe;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kayali_developer.smartphonecafe.data.model.Event;
import com.kayali_developer.smartphonecafe.data.model.FirebasePostMessageByTopic;
import com.kayali_developer.smartphonecafe.data.model.FirebasePostNotificationResponse;
import com.kayali_developer.smartphonecafe.data.model.User;
import com.kayali_developer.smartphonecafe.data.remote.APIService;
import com.kayali_developer.smartphonecafe.data.remote.ApiUtils;
import com.kayali_developer.smartphonecafe.utilities.AppDateUtils;
import com.kayali_developer.smartphonecafe.utilities.UsersUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NextEventFragment
        extends Fragment
implements MembersAdapter.MembersAdapterListener{

    public static final String TAG = "event_members_fragment_tag";

    @BindView(R.id.tv_event_topic)
    TextView tvEventTopic;
    @BindView(R.id.tv_event_location)
    TextView tvEventLocation;
    @BindView(R.id.tv_event_start_time)
    TextView tvEventStartTime;
    @BindView(R.id.tv_event_organizer_full_name)
    TextView tvEventOrganizerFullName;
    @BindView(R.id.tv_event_description)
    TextView tvEventDescription;
    @BindView(R.id.fl_btn_join_to_event)
    FloatingActionButton flBtnJoinToEvent;
    @BindView(R.id.fl_btn_add_event)
    com.getbase.floatingactionbutton.FloatingActionButton flBtnAddEvent;
    @BindView(R.id.rv_event_helpers)
    RecyclerView rvEventHelpers;
    @BindView(R.id.fl_menu_admin)
    FloatingActionsMenu flMenuAdmin;
    @BindView(R.id.fl_btn_join_to_event_admin)
    com.getbase.floatingactionbutton.FloatingActionButton flBtnJoinToEventAdmin;
    @BindView(R.id.fl_btn_show_event_members)
    com.getbase.floatingactionbutton.FloatingActionButton flBtnShowEventMembers;

    private Unbinder unbinder;
    private MainActivity mActivity;
    private FirebaseDatabase mFbDatabase;
    private DatabaseReference mEventsReference;
    private MembersAdapter helpersAdapter;

    private NextEventFragmentListener nextEventFragmentListener;
    private Event nextEvent = null;
    private Event newEvent = null;
    private EventMembersFragment eventMembersFragment;

    @Override
    public void onMemberLongClicked(User user) {

    }

    @Override
    public void onRateMemberClicked(User ratedUser) {

    }

    interface NextEventFragmentListener {
        void onAddEventClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        nextEventFragmentListener = ((NextEventFragmentListener) context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = ((MainActivity) getActivity());
        mFbDatabase = FirebaseDatabase.getInstance();
        mEventsReference = mFbDatabase.getReference().child("events");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_next_event, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nextEvent = mActivity.mViewModel.mNextEvent;
        showNextEventButtons();
        setData(nextEvent);

    }

    public void showNextEventButtons() {
        if (mActivity.mViewModel.mCurrentUser != null) {
            if (mActivity.mViewModel.mCurrentUser.getPermissionType() == User.SUPER_ADMIN_PERMISSION_TYPE ||
                    mActivity.mViewModel.mCurrentUser.getPermissionType() == User.ADMIN_PERMISSION_TYPE ||
                    mActivity.mViewModel.mCurrentUser.getPermissionType() == User.DEVELOPER_PERMISSION_TYPE) {
                flMenuAdmin.setVisibility(View.VISIBLE);
                flBtnJoinToEvent.setVisibility(View.GONE);
            } else {
                flMenuAdmin.setVisibility(View.GONE);
                flBtnJoinToEvent.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setData(Event nextEvent) {
        if (nextEvent != null) {
            tvEventTopic.setText(nextEvent.getTopic());
            tvEventLocation.setText(nextEvent.getLocation());
            tvEventStartTime.setText(AppDateUtils.longDateToDeFormat(nextEvent.getStartTime()));
            tvEventOrganizerFullName.setText(nextEvent.getOrganizerFullName());
            tvEventDescription.setText(nextEvent.getDescription());
            helpersAdapter = new MembersAdapter(this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvEventHelpers.setLayoutManager(layoutManager);
            rvEventHelpers.hasFixedSize();
            rvEventHelpers.setAdapter(helpersAdapter);

            List<User> eventHelpersList = new ArrayList<>();
            if (nextEvent.getMembersUIDs() != null && nextEvent.getMembersUIDs().size() > 0) {
                for (String userUID : nextEvent.getMembersUIDs()) {
                    eventHelpersList.add(mActivity.mViewModel.getMemberByUId(userUID));
                }
            }
            helpersAdapter.setData(UsersUtils.getHelpers(eventHelpersList));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public void updateData(List<User> helpers) {
        helpersAdapter.setData(helpers);
    }

    private void joinToEvent() {
        Query query = mEventsReference.orderByChild("eventId").equalTo(mActivity.mViewModel.mNextEvent.getEventId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Event event = null;
                    newEvent = null;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        try {
                            event = dataSnapshot1.getValue(Event.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (event != null) {
                        List<String> membersUids = event.getMembersUIDs();
                        if (membersUids == null) {
                            membersUids = new ArrayList<>();
                        }


                        boolean joined = false;
                        for (String uid : membersUids) {
                            if (uid.equals(mActivity.mViewModel.mCurrentUser.getUid())) {
                                joined = true;
                            }
                        }
                        if (joined) {
                            membersUids.remove(mActivity.mViewModel.mCurrentUser.getUid());
                            try {
                                newEvent = (Event) event.clone();
                                newEvent.setMembersUIDs(membersUids);
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                            if (newEvent != null) {
                                Timber.e(new Gson().toJson(newEvent));
                                mEventsReference.child(event.getEventId()).setValue(newEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mActivity.mViewModel.mNextEvent = newEvent;
                                        List<User> users = new ArrayList<>();
                                        for (String memberUID : newEvent.getMembersUIDs()) {
                                            if (mActivity.mViewModel.getMemberByUId(memberUID) != null) {
                                                users.add(mActivity.mViewModel.getMemberByUId(memberUID));
                                            }
                                        }
                                        updateData(UsersUtils.getHelpers(users));

                                        Toast.makeText(getContext(), "Unsubscribed from new Event", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            membersUids.add(mActivity.mViewModel.mCurrentUser.getUid());
                            try {
                                newEvent = (Event) event.clone();
                                newEvent.setMembersUIDs(membersUids);
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                            if (newEvent != null) {
                                Timber.e(new Gson().toJson(newEvent));
                                mEventsReference.child(event.getEventId()).setValue(newEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mActivity.mViewModel.mNextEvent = newEvent;
                                        List<User> users = new ArrayList<>();
                                        for (String memberUID : newEvent.getMembersUIDs()) {
                                            if (mActivity.mViewModel.getMemberByUId(memberUID) != null) {
                                                users.add(mActivity.mViewModel.getMemberByUId(memberUID));
                                            }
                                        }
                                        updateData(UsersUtils.getHelpers(users));

                                        DialogInterface.OnClickListener confirmButtonClickListener =
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        mActivity.addEventToCalendar(newEvent);
                                                    }
                                                };
                                        mActivity.showAddToCalendarConfirmDialog(confirmButtonClickListener, "Joined successfully to the new Event, Do you want to add this Event to Calendar?", "Add", "Cancel");

                                    }
                                });
                            }
                        }


                    } else {
                        Toast.makeText(getContext(), "Cannot join to new Event", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @OnClick({R.id.fl_btn_join_to_event, R.id.fl_btn_add_event, R.id.fl_btn_join_to_event_admin, R.id.fl_btn_show_event_members})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.fl_btn_join_to_event:
                joinToEvent();
                break;

            case R.id.fl_btn_add_event:
                flMenuAdmin.collapse();
                nextEventFragmentListener.onAddEventClicked();
                break;

            case R.id.fl_btn_join_to_event_admin:
                flMenuAdmin.collapse();
                joinToEvent();
                break;

            case R.id.fl_btn_show_event_members:
                mActivity.mainActivityFragmentContainer.setVisibility(View.VISIBLE);
                eventMembersFragment = new EventMembersFragment();
                if (mActivity.mFragmentManager != null){
                    mActivity.mFragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, eventMembersFragment, EventMembersFragment.EVENT_MEMBERS_TAG).commitNowAllowingStateLoss();
                    List<User> users = new ArrayList<>();
                    if (mActivity.mViewModel.mNextEvent.getMembersUIDs() != null && mActivity.mViewModel.mNextEvent.getMembersUIDs().size() > 0){
                        for (String memberUID : mActivity.mViewModel.mNextEvent.getMembersUIDs()){
                            User user = mActivity.mViewModel.getMemberByUId(memberUID);
                            if (user != null){
                                users.add(user);
                            }
                        }
                    }
                    if (UsersUtils.getMembersOnly(users) != null && UsersUtils.getMembersOnly(users).size() > 0){
                        eventMembersFragment.updateData(UsersUtils.getMembersOnly(users));
                    }
                }
                break;
        }
    }

    public void postEventByTopic() {
        APIService mAPIService = ApiUtils.getAPIService();
        if (getContext() != null) {
            FirebasePostMessageByTopic firebasePostMessageByTopic = new FirebasePostMessageByTopic(getContext().getString(R.string.news_event_topic), mActivity.mViewModel.mNextEvent);
            mAPIService.postEventByTopic(firebasePostMessageByTopic).enqueue(new Callback<FirebasePostNotificationResponse>() {
                @Override
                public void onResponse(Call<FirebasePostNotificationResponse> call, Response<FirebasePostNotificationResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Toast.makeText(getContext(), "Notification sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<FirebasePostNotificationResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
