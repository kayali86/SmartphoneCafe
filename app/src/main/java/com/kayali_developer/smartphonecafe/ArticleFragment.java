package com.kayali_developer.smartphonecafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kayali_developer.smartphonecafe.data.model.Article;
import com.kayali_developer.smartphonecafe.utilities.AppDateUtils;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ArticleFragment extends Fragment {
    public static final String TAG = "TAG";
    public static final String ARTICLE_KEY = "article_key";
    @BindView(R.id.iv_article_image)
    ImageView ivArticleImage;
    @BindView(R.id.tv_article_title)
    TextView tvArticleTitle;
    @BindView(R.id.tv_article_short_description)
    TextView tvArticleShortDescription;
    @BindView(R.id.tv_article_text)
    TextView tvArticleText;
    @BindView(R.id.tv_article_author)
    TextView tvArticleAuthor;
    @BindView(R.id.tv_article_publish_date)
    TextView tvArticlePublishDate;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            Article article = null;
            try {
                article = new Gson().fromJson(getArguments().getString(ArticleFragment.ARTICLE_KEY), Article.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (article != null) {
                populateArticle(article);
            }

        }
        return rootView;
    }

    private void populateArticle(Article article) {
        Picasso.get().load(article.getImageURL()).fit().centerInside()
                .error(R.drawable.no_image_available)
                .placeholder(R.drawable.no_image_available)
                .into(ivArticleImage);

        if (article.getTitle() != null){
            tvArticleTitle.setText(article.getTitle());
        }

        if (article.getShortDescription() != null){
            tvArticleShortDescription.setText(article.getShortDescription());
        }

        if (article.getText() != null){
            tvArticleText.setText(article.getText());
        }

        if (article.getAuthor() != null){
            tvArticleAuthor.setText(article.getAuthor());
        }

        String publishDateStr = AppDateUtils.longDateToDeFormat(article.getPublishDate());

        if (publishDateStr != null){
            tvArticlePublishDate.setText(publishDateStr);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null){
            unbinder.unbind();
        }
    }

}
