package com.kayali_developer.smartphonecafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kayali_developer.smartphonecafe.data.model.User;
import com.kayali_developer.smartphonecafe.utilities.ValidationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity
        extends AppCompatActivity{

    public static final int GALLERY_REQUEST = 123;

    @BindView(R.id.etv_email)
    EditText etvEmail;
    @BindView(R.id.etv_password)
    EditText etvPassword;
    @BindView(R.id.btn_sign_up)
    Button btnSignUp;
    @BindView(R.id.snack_bar_layout)
    CoordinatorLayout snackBarLayout;
    @BindView(R.id.ib_member_image)
    ImageButton ibMemberImage;
    @BindView(R.id.etv_first_name)
    EditText etvFirstName;
    @BindView(R.id.etv_last_name)
    EditText etvLastName;
    @BindView(R.id.etv_phone)
    EditText etvPhone;
    @BindView(R.id.etv_confirm_password)
    EditText etvConfirmPassword;
    private Snackbar mSnackBar;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mFbDatabase;
    private DatabaseReference mDBReference;
    DatabaseReference usersReference;

    private User newUser = null;
    private Uri mImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFbDatabase = FirebaseDatabase.getInstance();
        mDBReference = mFbDatabase.getReference();
        newUser = new User();
        setTextListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mCurrentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK & data != null) {
            mImageURI = data.getData();
            ibMemberImage.setImageURI(data.getData());
        }
    }

    private void setTextListeners(){

        etvEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null){
                    newUser.setEmail(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etvFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null){
                    newUser.setFirstName(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etvLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null){
                    newUser.setLastName(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etvPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null){
                    newUser.setPhoneNr(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
        if (mainIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mainIntent);
        }
    }


    private void startLoginActivity() {
        Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        if (loginIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(loginIntent);
        }
    }

    public void addImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("Image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.select_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, GALLERY_REQUEST);

    }

    private void uploadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference imagePath = storageReference.child(AppConstants.PROFILE_IMAGE_REFERENCE).child(mCurrentUser.getUid());
        UploadTask uploadTask = imagePath.putFile(mImageURI);


        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Continue with the task to get the download URL
                return imagePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null){
                        newUser.setImageURL(downloadUri.toString());
                        usersReference.child(mCurrentUser.getUid()).setValue(newUser);
                    }

                    Toast.makeText(SignUpActivity.this, "Profile Image uploaded Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Cannot Upload image!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUp() {
        hideKeyboard();

        String password = null;
        try {
            password = etvPassword.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String confirmPassword = null;
        try {
            confirmPassword = etvConfirmPassword.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (confirmPassword == null || TextUtils.isEmpty(confirmPassword) || confirmPassword.length() < 10) {
            initializeSnackBar("Enter confirm password", Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }

        if (password == null || TextUtils.isEmpty(password) || password.length() < 10) {
            initializeSnackBar(getString(R.string.valid_password_warning), Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }

        if (password != null && !password.equals(confirmPassword)) {
            initializeSnackBar("Password and confirm password are not the same", Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }

        if (newUser.getEmail() == null || TextUtils.isEmpty(newUser.getEmail()) || !ValidationUtils.checkEmailValidation(newUser.getEmail())) {
            initializeSnackBar(getString(R.string.valid_email_warning), Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }

        if (newUser.getLastName() == null || TextUtils.isEmpty(newUser.getLastName())) {
            initializeSnackBar("Enter last name", Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }

        if (newUser.getFirstName() == null || TextUtils.isEmpty(newUser.getFirstName())) {
            initializeSnackBar("Enter first name", Snackbar.LENGTH_SHORT);
            mSnackBar.show();
        }


        if (password != null && !TextUtils.isEmpty(password) && password.length() >= 10 &&
                confirmPassword != null && !TextUtils.isEmpty(confirmPassword) && password.equals(confirmPassword) &&
                newUser.getEmail() != null && !TextUtils.isEmpty(newUser.getEmail()) && ValidationUtils.checkEmailValidation(newUser.getEmail())
                && newUser.getFirstName() != null && !TextUtils.isEmpty(newUser.getFirstName())
                && newUser.getLastName() != null && !TextUtils.isEmpty(newUser.getLastName())) {

            mAuth.createUserWithEmailAndPassword(newUser.getEmail(), password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                mCurrentUser = mAuth.getCurrentUser();
                                FirebaseDatabase mFbDatabase = FirebaseDatabase.getInstance();
                                usersReference = mFbDatabase.getReference().child(AppConstants.USERS_CHILD);
                                newUser.setUid(mCurrentUser.getUid());
                                newUser.setPermissionType(User.INACTIVE_USER_PERMISSION_TYPE);
                                newUser.setFeedback(0);
                                newUser.setFeedbackCount(0);
                                uploadImage();


                                //usersReference.child(mCurrentUser.getUid()).setValue(newUser);


                                sendEmailVerification(mCurrentUser);
                            } else {
                                // If sign in fails, display a message to the user.
                                initializeSnackBar(getString(R.string.sign_up_error_message), Snackbar.LENGTH_SHORT);
                                mSnackBar.show();
                            }

                        }
                    });
        }
    }

    private void sendEmailVerification(FirebaseUser currentUser) {
        currentUser.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            initializeSnackBar(getString(R.string.email_verification_warning), Snackbar.LENGTH_SHORT);
                            mSnackBar.show();
                            new CountDownTimer(2000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    if (currentUser.isEmailVerified()) {
                                        startMainActivity();
                                    } else {
                                        startLoginActivity();
                                    }
                                    finish();
                                }
                            }.start();
                        } else {
                            initializeSnackBar(getString(R.string.registration_error), Snackbar.LENGTH_SHORT);
                            mSnackBar.show();
                        }
                    }
                });
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

    @OnClick({R.id.btn_sign_up, R.id.ib_member_image})
    public void onViewClicked(View view) {
        switch (view.getId()){

            case R.id.btn_sign_up:
                signUp();
            break;

            case R.id.ib_member_image:
                addImage();
                break;
        }
    }
}
