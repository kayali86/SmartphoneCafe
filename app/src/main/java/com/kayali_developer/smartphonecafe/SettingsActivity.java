package com.kayali_developer.smartphonecafe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kayali_developer.smartphonecafe.data.model.User;
import com.kayali_developer.smartphonecafe.utilities.Prefs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    public static final String CURRENT_MEMBER_KEY = "current_member_key";

    @BindView(R.id.sw_general_notification)
    Switch swGeneralNotification;
    @BindView(R.id.sw_new_event_notification)
    Switch swNewEventNotification;
    @BindView(R.id.sw_new_article_notification)
    Switch swNewArticleNotification;
    @BindView(R.id.sw_helpers_notification)
    Switch swHelpersNotification;

    private String pass = null;
    private User currentUser;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        mContext = this;

        if (getIntent() != null && getIntent().hasExtra(CURRENT_MEMBER_KEY)) {
            try {
                currentUser = new Gson().fromJson(getIntent().getStringExtra(CURRENT_MEMBER_KEY), User.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        if (currentUser != null && currentUser.getPermissionType() == User.DEVELOPER_PERMISSION_TYPE
        || currentUser != null && currentUser.getPermissionType() == User.SUPER_ADMIN_PERMISSION_TYPE
                || currentUser != null && currentUser.getPermissionType() == User.ADMIN_PERMISSION_TYPE
                || currentUser != null && currentUser.getPermissionType() == User.HELPER_PERMISSION_TYPE){
            swHelpersNotification.setVisibility(View.VISIBLE);
        }else{
            swHelpersNotification.setVisibility(View.GONE);
        }

        swGeneralNotification.setChecked(Prefs.getGeneralNotificationStatus(mContext));
        swNewEventNotification.setChecked(Prefs.getNewEventNotificationStatus(mContext));
        swNewArticleNotification.setChecked(Prefs.getNewArticleNotificationStatus(mContext));
        swHelpersNotification.setChecked(Prefs.getHelpersNotificationStatus(mContext));

        swGeneralNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    unsubscribeFromTopic(getString(R.string.general_topic));
                }else{
                    subscribeToTopic(getString(R.string.general_topic));
                }
            }
        });

        swNewEventNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    unsubscribeFromTopic(getString(R.string.news_event_topic));
                }else{
                    subscribeToTopic(getString(R.string.news_event_topic));
                }
            }
        });

        swNewArticleNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    unsubscribeFromTopic(getString(R.string.news_article_topic));
                }else{
                    subscribeToTopic(getString(R.string.news_article_topic));
                }
            }
        });

        swHelpersNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    unsubscribeFromTopic(getString(R.string.helper_topic));
                }else{
                    subscribeToTopic(getString(R.string.helper_topic));
                }
            }
        });
    }

    private void subscribeToTopic(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Subscription success", Toast.LENGTH_LONG).show();

                if (topic.equals(getString(R.string.general_topic))){
                    Prefs.setGeneralNotificationStatus(mContext, true);
                }
                else if (topic.equals(getString(R.string.news_event_topic))){
                    Prefs.setNewEventNotificationStatus(mContext, true);
                }
                else if (topic.equals(getString(R.string.news_article_topic))){
                    Prefs.setNewArticleNotificationStatus(mContext, true);
                }
                else if (topic.equals(getString(R.string.helper_topic))){
                    Prefs.setHelpersNotificationStatus(mContext, true);
                }
            }
        });
    }

    private void unsubscribeFromTopic(String topic){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Unsubscribed successfully", Toast.LENGTH_LONG).show();
                if (topic.equals(getString(R.string.general_topic))){
                    Prefs.setGeneralNotificationStatus(mContext, false);
                }
                else if (topic.equals(getString(R.string.news_event_topic))){
                    Prefs.setNewEventNotificationStatus(mContext, false);
                }
                else if (topic.equals(getString(R.string.news_article_topic))){
                    Prefs.setNewArticleNotificationStatus(mContext, false);
                }
                else if (topic.equals(getString(R.string.helper_topic))){
                    Prefs.setHelpersNotificationStatus(mContext, false);
                }
            }
        });
    }


    private void deleteMemberFromDb(User user) {
        FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
        DatabaseReference articlesReference = fbDatabase.getReference().child(AppConstants.USERS_CHILD);
        articlesReference.child(user.getUid()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(SettingsActivity.this, "User deleted!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

    }

    private void deleteMemberImage(User user) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imagePath = storageReference.child(AppConstants.PROFILE_IMAGE_REFERENCE).child(user.getUid());
        imagePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteAccount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(SettingsActivity.this, "Deletion failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAccount() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Get auth credentials from the user for re-authentication. The example below shows
            // email and password credentials but there are multiple possible providers,
            // such as GoogleAuthProvider or FacebookAuthProvider.
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), pass);

            // Prompt the user to re-provide their sign-in credentials

            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                deleteMemberFromDb(currentUser);
                                            }
                                        }
                                    });
                        }
                    });
        }

    }

    private void showPasswordAlertDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter your Password!");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pass = input.getText().toString();
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (user.getImageURL() != null) {
                                    deleteMemberImage(user);
                                } else {
                                    deleteAccount();
                                }

                            }
                        };
                showAccountDeletionDialog(discardButtonClickListener, "Do you want to delete your account?", "Delete", "Cancel");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    // Display an alert dialog
    private void showAccountDeletionDialog(DialogInterface.OnClickListener deleteButtonClickListener, String message, String positiveButtonCaption, String negativeButtonCaption) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @OnClick(R.id.btn_delete_account)
    public void onViewClicked() {
        showPasswordAlertDialog(currentUser);
    }
}
