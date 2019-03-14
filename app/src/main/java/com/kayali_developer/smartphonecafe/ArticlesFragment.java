package com.kayali_developer.smartphonecafe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.kayali_developer.smartphonecafe.data.model.Article;
import com.kayali_developer.smartphonecafe.data.model.User;

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

public class ArticlesFragment extends Fragment implements ArticlesAdapter.ArticlesFragmentAdapterListener {
    public static final String TAG = "articles_fragment_tag";

    @BindView(R.id.rv_articles_list)
    RecyclerView rvArticlesList;
    @BindView(R.id.fl_btn_add_article)
    FloatingActionButton flBtnAddArticle;

    private MainActivity mActivity;
    private Unbinder unbinder;
    private ArticlesAdapter mAdapter;

    private ArticlesFragmentListener articlesFragmentListener;

    interface ArticlesFragmentListener{
        void onAddArticleClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        articlesFragmentListener = ((ArticlesFragmentListener)context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = ((MainActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_articles, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvArticlesList.setHasFixedSize(true);
        rvArticlesList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ArticlesAdapter(this);
        rvArticlesList.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mActivity.mViewModel.mAllArticles != null && mActivity.mViewModel.mAllArticles.size() > 0) {
            updateData(mActivity.mViewModel.mAllArticles);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onArticleClicked(Article currentArticle) {
        if (currentArticle != null) {
            ArticleFragment mArticleFragment = new ArticleFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ArticleFragment.ARTICLE_KEY, new Gson().toJson(currentArticle));
            mArticleFragment.setArguments(bundle);
            mActivity.mFragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, mArticleFragment, ArticleFragment.TAG).commitAllowingStateLoss();
        }
    }

    @Override
    public void onArticleLongClicked(Article currentArticle) {
        if (mActivity.mViewModel.mCurrentUser.getPermissionType() == User.DEVELOPER_PERMISSION_TYPE
            || mActivity.mViewModel.mCurrentUser.getPermissionType() == User.SUPER_ADMIN_PERMISSION_TYPE
                || mActivity.mViewModel.mCurrentUser.getPermissionType() == User.ADMIN_PERMISSION_TYPE){

            DialogInterface.OnClickListener confirmButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteArticle(currentArticle);
                        }
                    };
            showDeletionConfirmDialog(confirmButtonClickListener, "Do you want to delete this Article?", "Delete", "Cancel");


        }
    }

    // Display an alert dialog
    private void showDeletionConfirmDialog(DialogInterface.OnClickListener deleteButtonClickListener, String message, String positiveButtonCaption, String negativeButtonCaption) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    private void deleteArticle(Article article){
        FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
        DatabaseReference articlesReference = fbDatabase.getReference().child(AppConstants.ARTICLES_CHILD);
        articlesReference.child(article.getArticleId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                mActivity.mViewModel.mAllArticles.remove(article);
                deleteArticleImage(article);
            }
        });

    }

    private void deleteArticleImage(Article article){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imagePath = storageReference.child(AppConstants.ARTICLE_IMAGE_REFERENCE).child(article.getArticleId());
        imagePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mAdapter.removeArticle(article);
                Toast.makeText(getContext(), "Article deleted!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(getContext(), "Deletion failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateData(List<Article> articles) {
        mAdapter.setData(articles);
    }

    public void setAddArticleButtonVisibility() {
        if (mActivity.mViewModel.mCurrentUser != null){
            if (mActivity.mViewModel.mCurrentUser.getPermissionType() == User.SUPER_ADMIN_PERMISSION_TYPE ||
                    mActivity.mViewModel.mCurrentUser.getPermissionType() == User.ADMIN_PERMISSION_TYPE ||
                    mActivity.mViewModel.mCurrentUser.getPermissionType() == User.DEVELOPER_PERMISSION_TYPE){
                flBtnAddArticle.setVisibility(View.VISIBLE);
            }else{
                flBtnAddArticle.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setAddArticleButtonVisibility();
    }

    @OnClick(R.id.fl_btn_add_article)
    public void onViewClicked() {
        articlesFragmentListener.onAddArticleClicked();
    }


}
