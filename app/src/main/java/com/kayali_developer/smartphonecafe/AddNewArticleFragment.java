package com.kayali_developer.smartphonecafe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kayali_developer.smartphonecafe.data.model.Article;
import com.kayali_developer.smartphonecafe.data.model.FirebasePostMessageByTopic;
import com.kayali_developer.smartphonecafe.data.model.FirebasePostNotificationResponse;
import com.kayali_developer.smartphonecafe.data.remote.APIService;
import com.kayali_developer.smartphonecafe.data.remote.ApiUtils;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewArticleFragment extends Fragment implements MainActivity.MainActivityListener {
    public static final String TAG = "AddNewArticleFragmentTag";
    public static final int GALLERY_REQUEST = 123;

    @BindView(R.id.ib_article_image)
    ImageButton ibArticleImage;
    @BindView(R.id.etv_article_title)
    EditText etvArticleTitle;
    @BindView(R.id.etv_article_short_description)
    EditText etvArticleShortDescription;
    @BindView(R.id.etv_article_text)
    EditText etvArticleText;
    @BindView(R.id.btn_publish)
    Button btnPublish;
    private Unbinder unbinder;
    private FirebaseDatabase mFbDatabase;
    private DatabaseReference mDBReference;
    public FirebaseAuth mAuth;
    private FirebaseUser mCurrentFbUser;
    private StorageReference storageReference;
    private MainActivity mActivity;
    private Uri mImageURI;
    private UploadTask uploadTask;
    private Uri mDownloadUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_new_article, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mActivity = ((MainActivity) getActivity());
        mFbDatabase = FirebaseDatabase.getInstance();
        mDBReference = mFbDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentFbUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        if (mActivity != null && mActivity.mViewModel.mAllUsers != null && mActivity.mViewModel.mAllUsers.size() > 0) {
            mActivity.setMainActivityListener(this::onImageSelected);


        }


        return rootView;
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

    private void publish() {
        String title = etvArticleTitle.getText().toString();
        String shortDescription = etvArticleShortDescription.getText().toString();
        String text = etvArticleText.getText().toString();
        String author = mActivity.mViewModel.mCurrentUser.getFirstName() + " " + mActivity.mViewModel.mCurrentUser.getLastName();
        String authorId = mCurrentFbUser.getUid();
        long publishDate = Calendar.getInstance().getTimeInMillis();

        final DatabaseReference newArticleRef = mDBReference.child(AppConstants.ARTICLES_CHILD).push();
        Article article = new Article(newArticleRef.getKey(), null, title, shortDescription, text, author, authorId, publishDate, 0.0, 0, null);
        newArticleRef.setValue(article);
        final StorageReference imagePath = storageReference.child(AppConstants.ARTICLE_IMAGE_REFERENCE).child(newArticleRef.getKey());
        uploadTask = imagePath.putFile(mImageURI);


        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imagePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    mDownloadUri = task.getResult();

                    newArticleRef.child("imageURL").setValue(mDownloadUri.toString());
                    postArticleNotificationByTopic(article);
                    Toast.makeText(getContext(), "Information saved", Toast.LENGTH_SHORT).show();
                    mActivity.mViewModel.loadAllArticles();
                    mActivity.mFragmentManager.beginTransaction().remove(AddNewArticleFragment.this).commitAllowingStateLoss();
                    mActivity.mainActivityFragmentContainer.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Please make sure that all Fields with * are not empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void postArticleNotificationByTopic(Article newArticle) {
        APIService mAPIService = ApiUtils.getAPIService();
        if (getContext() != null) {
            FirebasePostMessageByTopic firebasePostMessageByTopic = new FirebasePostMessageByTopic(getContext().getString(R.string.news_article_topic), newArticle);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick({R.id.ib_article_image, R.id.btn_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_article_image:
                addImage();
                break;
            case R.id.btn_publish:
                publish();
                break;
        }
    }

    @Override
    public void onImageSelected(Uri uri) {
        mImageURI = uri;
        ibArticleImage.setImageURI(uri);
    }
}
