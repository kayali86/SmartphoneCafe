package com.kayali_developer.smartphonecafe;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kayali_developer.smartphonecafe.data.model.User;
import com.kayali_developer.smartphonecafe.utilities.ValidationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.etv_email)
    EditText etvEmail;
    @BindView(R.id.etv_password)
    EditText etvPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.snack_bar_layout)
    CoordinatorLayout snackBarLayout;
    private Snackbar mSnackBar;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null && mCurrentUser.isEmailVerified()) {
            startMainActivity();
            finish();
        } else if (mCurrentUser != null) {
            initializeSnackBar(getString(R.string.email_verification_warning), Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }
    }

    private void checkUpdateUserInFirebaseDb() {
        FirebaseDatabase mFbDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = mFbDatabase.getReference().child(AppConstants.USERS_CHILD);
        Query query = usersReference.orderByChild(AppConstants.UID_CHILD).equalTo(mCurrentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() || dataSnapshot.getChildrenCount() <= 0) {
                    User user = new User();
                    user.setUid(mCurrentUser.getUid());
                    user.setPermissionType(User.MEMBER_PERMISSION_TYPE);
                    user.setEmail(mCurrentUser.getEmail());
                    usersReference.child(mCurrentUser.getUid()).setValue(user);

                } else if (dataSnapshot.exists() && dataSnapshot.hasChild(mCurrentUser.getUid())) {
                    User user = null;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        try {
                            User user1 = dataSnapshot1.getValue(User.class);
                            if (user1 != null && user1.getUid().equals(mCurrentUser.getUid())) {
                                user = user1;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (user != null) {
                        if (user.getPermissionType() == User.INACTIVE_USER_PERMISSION_TYPE) {
                            user.setPermissionType(User.MEMBER_PERMISSION_TYPE);
                            usersReference.child(mCurrentUser.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    startMainActivity();
                                }
                            });
                        } else {
                            startMainActivity();
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startSignUpActivity() {
        Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        if (signUpIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(signUpIntent);
        }
    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        if (mainIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mainIntent);
        }
    }

    private void showEmailAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter your Email address!");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ValidationUtils.checkEmailValidation(input.getText().toString())){
                    sendPasswordResetEmail(input.getText().toString());
                }else{
                    initializeSnackBar("Please enter a valid Email address!", Snackbar.LENGTH_LONG);
                    mSnackBar.show();
                }

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

    private void sendPasswordResetEmail(String email) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            initializeSnackBar("An Email has been sent to you, Please check your email to reset password.", Snackbar.LENGTH_LONG);
                            mSnackBar.show();
                        } else {
                            initializeSnackBar("Cannot reset password, Please contact the Admin.", Snackbar.LENGTH_LONG);
                            mSnackBar.show();
                        }
                    }
                });
    }

    private void signIn() {
        hideKeyboard();
        String email = null;
        try {
            email = etvEmail.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String password = null;
        try {
            password = etvPassword.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (password == null || TextUtils.isEmpty(password)) {
            initializeSnackBar(getString(R.string.valid_password_warning), Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }else if (password.length() < 10){
            initializeSnackBar("The Password should has at least Ten Letters!", Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }

        if (email == null || TextUtils.isEmpty(email) || !ValidationUtils.checkEmailValidation(email)) {
            initializeSnackBar(getString(R.string.valid_email_warning), Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }

        if (password != null && !TextUtils.isEmpty(password) && password.length() >= 10 &&
                email != null && !TextUtils.isEmpty(email) && ValidationUtils.checkEmailValidation(email)) {

            mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    mCurrentUser = mAuth.getCurrentUser();
                    if (mCurrentUser != null && mCurrentUser.isEmailVerified()) {
                        // Sign in success, update UI with the signed-in user's information
                        checkUpdateUserInFirebaseDb();
                    } else if (mCurrentUser != null && !mCurrentUser.isEmailVerified()) {
                        initializeSnackBar(getString(R.string.email_verification_warning), Snackbar.LENGTH_LONG);
                        mSnackBar.show();
                    } else {
                        // If sign in fails, display a message to the user.
                        initializeSnackBar(getString(R.string.login_error_message), Snackbar.LENGTH_LONG);
                        mSnackBar.show();
                    }
                }
            })

            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    initializeSnackBar(e.getMessage(), Snackbar.LENGTH_LONG);
                    mSnackBar.show();
                }
            })
                    /*
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mCurrentUser = mAuth.getCurrentUser();
                            if (task.isSuccessful() && mCurrentUser != null && mCurrentUser.isEmailVerified()) {
                                // Sign in success, update UI with the signed-in user's information
                                checkUpdateUserInFirebaseDb();
                            } else if (mCurrentUser != null && !mCurrentUser.isEmailVerified()) {
                                initializeSnackBar(getString(R.string.email_verification_warning), Snackbar.LENGTH_SHORT);
                                mSnackBar.show();
                            } else {
                                // If sign in fails, display a message to the user.
                                initializeSnackBar(getString(R.string.login_error_message), Snackbar.LENGTH_SHORT);
                                mSnackBar.show();
                            }
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                        initializeSnackBar("You have no Permission to login", Snackbar.LENGTH_SHORT);
                        mSnackBar.show();
                }
            })

                    .addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {

                        initializeSnackBar("You have no Permission to login", Snackbar.LENGTH_SHORT);
                        mSnackBar.show();

                }
            })
            */
            ;
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void initializeSnackBar(String text, int duration) {
        mSnackBar = Snackbar
                .make(snackBarLayout, text, duration);
        View sbView = mSnackBar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(Color.WHITE);
    }

    @OnClick({R.id.btn_login, R.id.tv_sign_up, R.id.tv_forgot_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btn_login:
                signIn();
                break;

            case R.id.tv_sign_up:
                startSignUpActivity();
                break;

            case R.id.tv_forgot_password:
                showEmailAlertDialog();
                break;

        }
    }

}
