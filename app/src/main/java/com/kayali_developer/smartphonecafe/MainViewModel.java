package com.kayali_developer.smartphonecafe;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kayali_developer.smartphonecafe.data.model.Article;
import com.kayali_developer.smartphonecafe.data.model.Event;
import com.kayali_developer.smartphonecafe.data.model.User;
import com.kayali_developer.smartphonecafe.utilities.SingleEventMutableLive;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class MainViewModel extends AndroidViewModel {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentFbUser;
    private FirebaseDatabase mFbDatabase;
    private DatabaseReference mDBReference;

    public User mCurrentUser = null;

    public Event mNextEvent;

    public List<User> mAllUsers;
    public MutableLiveData<List<User>> mAllMembersLive;

    public List<Article> mAllArticles;
    public MutableLiveData<List<Article>> mAllArticlesLive;

    public List<Event> mAllEvents;
    public MutableLiveData<List<Event>> mAllEventsLive;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mAllArticlesLive = new SingleEventMutableLive<>();
        mAllMembersLive = new SingleEventMutableLive<>();
        mAllEventsLive = new SingleEventMutableLive<>();
        mAuth = FirebaseAuth.getInstance();
        mCurrentFbUser = mAuth.getCurrentUser();
        mFbDatabase = FirebaseDatabase.getInstance();
        mDBReference = mFbDatabase.getReference();
        loadAllActiveMembers();
    }

    List<User> loadAllActiveMembers(){
        mDBReference.child(AppConstants.USERS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> allUsers = new ArrayList<>();
                for (DataSnapshot memberData : dataSnapshot.getChildren()){
                    User user = null;
                    try {
                        user = memberData.getValue(User.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (user != null && user.getPermissionType() != User.INACTIVE_USER_PERMISSION_TYPE){
                        allUsers.add(user);
                    }

                }
                for (User user : allUsers){
                    if (mCurrentFbUser != null && user.getUid().equals(mCurrentFbUser.getUid())){
                        mCurrentUser = user;
                    }
                }
                mAllMembersLive.postValue(allUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return null;
    }


    List<Article> loadAllArticles(){
        mDBReference.child(AppConstants.ARTICLES_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Timber.e("%s", dataSnapshot.getChildrenCount());
                List<Article> allArticles = new ArrayList<>();
                for (DataSnapshot articleData : dataSnapshot.getChildren()){
                    allArticles.add(articleData.getValue(Article.class));
                    Timber.e("allArticles %s", allArticles.size());
                }
                mAllArticlesLive.postValue(allArticles);
                mNextEvent = getNextEvent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return null;
    }


    List<Event> loadAllEvents(){
        mDBReference.child(AppConstants.EVENTS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Timber.e("%s", dataSnapshot.getChildrenCount());
                List<Event> allEvents = new ArrayList<>();
                for (DataSnapshot eventData : dataSnapshot.getChildren()){
                    allEvents.add(eventData.getValue(Event.class));
                    Timber.e("allEvents %s", allEvents.size());
                }
                mAllEventsLive.postValue(allEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return null;
    }

    private Event getNextEvent(){
        if (mAllEvents == null || mAllEvents.size() == 0) return null;
        Event nextEvent = null;
        for (Event event : mAllEvents){
            if (event.isNextEvent()){
                nextEvent = event;
            }
        }
        return nextEvent;
    }


    public List<User> getAllHelpers(){
        if (mAllUsers == null) return null;
        List<User> allHelpers = new ArrayList<>();
        for (User user : mAllUsers){
            if (user.getPermissionType() == User.DEVELOPER_PERMISSION_TYPE || user.getPermissionType() == User.SUPER_ADMIN_PERMISSION_TYPE ||
                    user.getPermissionType() == User.ADMIN_PERMISSION_TYPE || user.getPermissionType() == User.HELPER_PERMISSION_TYPE){
                allHelpers.add(user);
            }
        }
        return allHelpers;
    }

    public List<User> getAllAdmins(){
        if (mAllUsers == null) return null;
        List<User> allAdmins = new ArrayList<>();
        for (User user : mAllUsers){
            if (user.getPermissionType() == User.ADMIN_PERMISSION_TYPE){
                allAdmins.add(user);
            }
        }
        return allAdmins;
    }


    public List<User> getAllSuperAdmins(){
        if (mAllUsers == null) return null;
        List<User> allSuperAdmins = new ArrayList<>();
        for (User user : mAllUsers){
            if (user.getPermissionType() == User.SUPER_ADMIN_PERMISSION_TYPE){
                allSuperAdmins.add(user);
            }
        }
        return allSuperAdmins;
    }


    public List<User> getAllUsers(){
        if (mAllUsers == null) return null;
        List<User> allUsers = new ArrayList<>();
        for (User user : mAllUsers){
            if (user.getPermissionType() == User.MEMBER_PERMISSION_TYPE){
                allUsers.add(user);
            }
        }
        return allUsers;
    }

    public User getMemberByUId(String uid){
        if (mAllUsers == null) return null;
        User user = null;
        for (User member : mAllUsers){
            if (member.getUid().equals(uid)){
                user = member;
            }
        }
        return user;
    }

    public int getMemberIndexByUId(String uid){
        if (mAllUsers == null) return -1;
        int index = -1;
        for (int i = 0; i < mAllUsers.size(); i++){
            if (mAllUsers.get(i).getUid().equals(uid)){
                index = i;
            }
        }
        return index;
    }

    public User getMemberByFullName(String memberFullName){
        if (mAllUsers == null) return null;
        User user = null;
        for (User member : mAllUsers){
            String fullName = member.getFirstName() + " " + member.getLastName();
            if (fullName.equals(memberFullName)){
                user = member;
            }
        }
        return user;
    }


    public void changeMemberPermissionByUID(String memberUID, int newPermissionType){
        int memberIndex = getMemberIndexByUId(memberUID);
        if (memberIndex > 0){
            User user = getMemberByUId(memberUID);
            user.setPermissionType(newPermissionType);
            mAllUsers.remove(memberIndex);
            mAllUsers.add(memberIndex, user);
        }
    }

    public void deleteMemberByUID(String memberUID){
        int memberIndex = getMemberIndexByUId(memberUID);
        if (memberIndex > 0){
            mAllUsers.remove(memberIndex);
        }
    }

    public Article getArticleByUId(String id){
        if (mAllArticles == null) return null;
        Article article = null;
        for (Article article1 : mAllArticles){
            if (article1.getArticleId().equals(id)){
                article = article1;
            }
        }
        return article;
    }

}
