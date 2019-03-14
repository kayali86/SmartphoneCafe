package com.kayali_developer.smartphonecafe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kayali_developer.smartphonecafe.data.model.Article;
import com.kayali_developer.smartphonecafe.data.model.Event;
import com.kayali_developer.smartphonecafe.data.model.User;
import com.kayali_developer.smartphonecafe.utilities.Prefs;
import com.kayali_developer.smartphonecafe.utilities.UsersUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity
        extends AppCompatActivity
        implements NextEventFragment.NextEventFragmentListener,
        ArticlesFragment.ArticlesFragmentListener,
        HelpersFragment.HelpersFragmentListener {

    public static final String JOIN_TO_EVENT_ACTION = "JOIN_TO_EVENT_ACTION";
    public static final String NEXT_EVENT_ID_KEY = "NEXT_EVENT_ID_KEY";
    public static final String NEW_ARTICLE_KEY = "NEW_ARTICLE_KEY";

    @BindView(R.id.main_tab_layout)
    TabLayout mainTabLayout;
    @BindView(R.id.main_view_pager)
    ViewPager mainViewPager;
    @BindView(R.id.main_activity_fragment_container)
    FrameLayout mainActivityFragmentContainer;
    @BindView(R.id.snack_bar_layout)
    CoordinatorLayout snackBarLayout;

    private Context mContext;
    public MainViewModel mViewModel;
    public FirebaseAuth mAuth;
    private FirebaseUser mCurrentFbUser;
    private FirebaseDatabase mFbDatabase;
    private DatabaseReference mDBReference;
    private DatabaseReference mEventsReference;
    private DatabaseReference mUsersReference;
    private DatabaseReference mMessageReference;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FragmentManager mFragmentManager;
    private MainActivityListener mMainActivityListener;
    public MainViewPagerAdapter mViewPagerAdapter;

    private Event newEvent = null;

    interface MainActivityListener {
        void onImageSelected(Uri uri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = this;
        Timber.plant(new Timber.DebugTree());
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mFragmentManager = getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        mCurrentFbUser = mAuth.getCurrentUser();
        mFbDatabase = FirebaseDatabase.getInstance();
        mDBReference = FirebaseDatabase.getInstance().getReference();

        mEventsReference = mFbDatabase.getReference().child("events");
        mUsersReference = mFbDatabase.getReference("users");
        mMessageReference = mFbDatabase.getReference("message");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mCurrentFbUser == null) {
                    logout();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        if (!Prefs.getNotificationInitializationStatus(mContext)) {
            subscribeToTopic(getString(R.string.general_topic));
            Prefs.setGeneralNotificationStatus(mContext, true);
            subscribeToTopic(getString(R.string.news_event_topic));
            Prefs.setNewEventNotificationStatus(mContext, true);
            subscribeToTopic(getString(R.string.news_article_topic));
            Prefs.setNewArticleNotificationStatus(mContext, true);
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MainActivity", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        Log.e("MainActivity", token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        if (savedInstanceState == null) {

            mViewPagerAdapter = new MainViewPagerAdapter(mContext, mFragmentManager);
            mainViewPager.setAdapter(mViewPagerAdapter);
            // Give the TabLayout the ViewPager
            mainTabLayout.setupWithViewPager(mainViewPager);
            setTabsIcons(mainTabLayout);

        }

        mViewModel.mAllMembersLive.observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mViewModel.mAllUsers = users;
                if (mViewModel.mAllEvents == null) {
                    mViewModel.loadAllEvents();
                }
            }
        });

        mViewModel.mAllEventsLive.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                mViewModel.mAllEvents = events;
                if (mViewModel.mAllArticles == null) {
                    mViewModel.loadAllArticles();
                }
            }
        });

        mViewModel.mAllArticlesLive.observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                mViewModel.mAllArticles = articles;
                if (mViewModel.mCurrentUser != null) {
                    mViewPagerAdapter.getArticlesFragment().setAddArticleButtonVisibility();
                }
                if (mViewPagerAdapter != null && mViewPagerAdapter.getArticlesFragment() != null) {
                    mViewPagerAdapter.getArticlesFragment().updateData(mViewModel.mAllArticles);
                }
                if (mViewPagerAdapter != null && mViewPagerAdapter.getNextEventFragment() != null && mViewModel.mNextEvent != null) {
                    mViewPagerAdapter.getNextEventFragment().setData(mViewModel.mNextEvent);
                    mViewPagerAdapter.getNextEventFragment().showNextEventButtons();
                }
            }
        });

        if (getIntent() != null) {
            if (getIntent().getAction() != null && getIntent().getAction().equals(JOIN_TO_EVENT_ACTION) && getIntent().hasExtra(NEXT_EVENT_ID_KEY) && getIntent().getStringExtra(NEXT_EVENT_ID_KEY) != null) {
                joinToEvent(getIntent().getStringExtra(NEXT_EVENT_ID_KEY));
            } else if (getIntent().hasExtra(NEW_ARTICLE_KEY) && getIntent().getStringExtra(NEW_ARTICLE_KEY) != null) {
                Article article = null;
                try {
                    article = new Gson().fromJson(getIntent().getStringExtra(NEW_ARTICLE_KEY), Article.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (article != null) {
                    mainActivityFragmentContainer.setVisibility(View.VISIBLE);
                    ArticleFragment mArticleFragment = new ArticleFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ArticleFragment.ARTICLE_KEY, new Gson().toJson(article));
                    mArticleFragment.setArguments(bundle);
                    mFragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, mArticleFragment, ArticleFragment.TAG).commitAllowingStateLoss();
                }
            }
        }
    }

    private void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (topic.equals(getString(R.string.general_topic))) {
                    Prefs.setGeneralNotificationStatus(mContext, true);
                } else if (topic.equals(getString(R.string.news_event_topic))) {
                    Prefs.setNewEventNotificationStatus(mContext, true);
                } else if (topic.equals(getString(R.string.news_article_topic))) {
                    Prefs.setNewArticleNotificationStatus(mContext, true);
                }
            }
        });
    }

    private void setTabsIcons(TabLayout tabLayout) {
        try {
            tabLayout.getTabAt(0).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
            tabLayout.getTabAt(1).setIcon(android.R.drawable.star_on);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainActivityListener(MainActivityListener mainActivityListener) {
        mMainActivityListener = mainActivityListener;
    }

    public void rateUser(User ratedUser, Double givenFeedback) {
        if (ratedUser != null && givenFeedback != null){
            Query query = mUsersReference.orderByChild("uid").equalTo(ratedUser.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = null;
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            try {
                                user = dataSnapshot1.getValue(User.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (user != null){
                            double oldFeedback = user.getFeedback();
                            int oldFeedbackCount = user.getFeedbackCount();
                            List<String> oldRatersUid = user.getRatersUid();

                            int newFeedbackCount = oldFeedbackCount + 1;

                            double newFeedback;
                            if (oldFeedbackCount > 0 && oldRatersUid.size() > 0){
                                newFeedback = (oldFeedback * oldFeedbackCount + givenFeedback) / newFeedbackCount;
                            }else{
                                newFeedback = givenFeedback;
                            }

                            List<String> newRatersUid = oldRatersUid;
                            if (newRatersUid == null){
                                newRatersUid = new ArrayList<>();
                            }

                            newRatersUid.add(mViewModel.mCurrentUser.getUid());

                            User updatedUser = null;
                            try {
                                updatedUser = (User) user.clone();
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                            if (updatedUser != null){
                                updatedUser.setFeedback(newFeedback);
                                updatedUser.setFeedbackCount(newFeedbackCount);
                                Toast.makeText(mContext, String.valueOf(updatedUser.getFeedback()), Toast.LENGTH_LONG).show();
                            }

                        }else{
                            Toast.makeText(mContext, "You already rated this Helper!", Toast.LENGTH_SHORT).show();
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

/*
    public void postEventNotificationByTopic() {
        APIService mAPIService = ApiUtils.getAPIService();
        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        long endTime = cal.getTimeInMillis() + 60 * 60 * 1000;
        Event event = new Event(startTime, endTime, getString(R.string.helper_topic), "Gießen", "my Description");
        List<String> tokens = new ArrayList<>();
        String token = "dt_bJjR7VtY:APA91bE0nlar_MlDXSFPASm0KftI5VqSYsZ0UhPrR48u7XKJaEAvD0ZsJsmP_omepSNCQHRLYlbs4f_K38bkKnNNscLT26dvLCkSz0WUxaxgcFk1jtxNMDspveOBxC2n0tq4C7EKM0oU";
        tokens.add(token);
        FirebasePostMessageByToken FirebasePostMessageByToken = new FirebasePostMessageByToken(tokens, event);
        mAPIService.postEventNotificationByTopic(FirebasePostMessageByToken).enqueue(new Callback<FirebasePostNotificationResponse>() {
            @Override
            public void onResponse(Call<FirebasePostNotificationResponse> call, Response<FirebasePostNotificationResponse> response) {
                tv.setText(String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    tv.setText(String.valueOf(response.code()));
                    if (response.body() != null) {
                        tv.setText(response.body().toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<FirebasePostNotificationResponse> call, Throwable t) {
                tv.setText("Error");
            }
        });
    }
*/


    private void logout() {
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startLoginActivity();
    }


    private void startLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        if (loginIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(loginIntent);
        }
    }


    private void startSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        if (mViewModel.mCurrentUser != null) {
            settingsIntent.putExtra(SettingsActivity.CURRENT_MEMBER_KEY, new Gson().toJson(mViewModel.mCurrentUser));
        }
        if (settingsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(settingsIntent);
        }
    }

    @Override
    public void onAddEventClicked() {
        mainActivityFragmentContainer.setVisibility(View.VISIBLE);
        AddNewEventFragment addNewEventFragment = new AddNewEventFragment();
        if (mFragmentManager != null) {
            mFragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, addNewEventFragment, AddNewEventFragment.TAG).addToBackStack(null).commitAllowingStateLoss();
        }
    }

    @Override
    public void onAddArticleClicked() {
        mainActivityFragmentContainer.setVisibility(View.VISIBLE);
        AddNewArticleFragment addNewArticleFragment = new AddNewArticleFragment();
        if (mFragmentManager != null) {
            mFragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, addNewArticleFragment, AddNewArticleFragment.TAG).addToBackStack(null).commitAllowingStateLoss();
        }
    }

    @Override
    public void onRateMemberClicked(User ratedUser) {

        // ToDo:
        rateUser(ratedUser, 5.0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK & data != null) {
            mMainActivityListener.onImageSelected(data.getData());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                logout();
                break;

            case R.id.action_settings:
                startSettingsActivity();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    public void updateEventsHistory() {
        mViewModel.loadAllEvents();
        if (mViewPagerAdapter.getEventsHistoryFragment() != null) {
            mViewPagerAdapter.getEventsHistoryFragment().updateData(mViewModel.mAllEvents);
        }
    }

    // Display an alert dialog
    public void showAddToCalendarConfirmDialog(DialogInterface.OnClickListener deleteButtonClickListener, String message, String positiveButtonCaption, String negativeButtonCaption) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonCaption, deleteButtonClickListener);
        builder.setNegativeButton(negativeButtonCaption, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void addEventToCalendar(Event event) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStartTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEndTime());

        intent.putExtra(CalendarContract.Events.TITLE, "Smartphone Cafe");
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getTopic());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());

        startActivity(intent);
    }

    private void joinToEvent(String eventId) {
        Query query = mEventsReference.orderByChild("eventId").equalTo(eventId);
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
                        } else {
                            Toast.makeText(mContext, "Cannot join to new Event", Toast.LENGTH_SHORT).show();
                        }


                        boolean joined = false;
                        for (String uid : membersUids) {
                            if (uid.equals(mViewModel.mCurrentUser.getUid())) {
                                joined = true;
                            }
                        }

                        if (!joined) {
                            membersUids.add(mViewModel.mCurrentUser.getUid());
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
                                        mViewModel.mNextEvent = newEvent;
                                        if (mViewPagerAdapter.getNextEventFragment() != null) {
                                            List<User> users = new ArrayList<>();
                                            for (String memberUID : newEvent.getMembersUIDs()) {
                                                if (mViewModel.getMemberByUId(memberUID) != null) {
                                                    users.add(mViewModel.getMemberByUId(memberUID));
                                                }
                                            }
                                            mViewPagerAdapter.getNextEventFragment().updateData(UsersUtils.getHelpers(users));
                                        }
                                        DialogInterface.OnClickListener confirmButtonClickListener =
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        addEventToCalendar(newEvent);
                                                    }
                                                };
                                        showAddToCalendarConfirmDialog(confirmButtonClickListener, "Joined successfully to the new Event, Do you want to add this Event to Calendar?", "Add", "Cancel");

                                    }
                                });
                            }
                        } else {
                            Toast.makeText(mContext, "You are already joined to new Event", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(mContext, "Cannot join to new Event", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

/*
    @OnClick(R.id.btn)
    public void onViewClicked() {

        User user = new User(mCurrentFbUser.getUid(), "Abdulkadir", "Kayali", 0, "015115717039", null, null, null);

        mUsersReference.child(mCurrentFbUser.getUid()).setValue(user);


        //Article article = new Article("https://cdn.pixabay.com/photo/2017/04/05/11/56/image-in-the-image-2204798_1280.jpg",
                //"Demo article", "Article description", "Abdulkadir kayli", userId, Calendar.getInstance().getTimeInMillis());
        //mFbDatabase.getReference("articles").push().setValue(article);


        //Event event = new Event("testId" ,Calendar.getInstance().getTimeInMillis(), Calendar.getInstance().getTimeInMillis(), "My Topic", "Gießen", "My Description", "Organizer UID", "Organizer Full name", null, true);
        //mFbDatabase.getReference("events").push().setValue(event);
        //mMessageReference.child("1").child("title").setValue("Hello, World!");
        Toast.makeText(mContext, null, Toast.LENGTH_SHORT).show();

        //postEventNotificationByTopic();
    }
*/
}
