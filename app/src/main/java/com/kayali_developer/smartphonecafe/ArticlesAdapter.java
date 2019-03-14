package com.kayali_developer.smartphonecafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kayali_developer.smartphonecafe.data.model.Article;
import com.kayali_developer.smartphonecafe.utilities.AppDateUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticlesAdapterViewHolder> {
    private static final int THUMBNAIL_SIZE = 320;
    private List<Article> mArticles;

    private ArticlesFragmentAdapterListener mArticlesFragmentAdapterListener;

    interface ArticlesFragmentAdapterListener {
        void onArticleClicked(Article currentArticle);
        void onArticleLongClicked(Article currentArticle);
    }

    public ArticlesAdapter(ArticlesFragmentAdapterListener mArticlesFragmentAdapterListener) {
        this.mArticlesFragmentAdapterListener = mArticlesFragmentAdapterListener;
    }

    @NonNull
    @Override
    public ArticlesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_article;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ArticlesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticlesAdapterViewHolder holder, int position) {
        Article currentArticle = mArticles.get(position);

        Picasso.get().load(currentArticle.getImageURL())
                .resize(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.no_image_available)
                .into(holder.iv_article_thumbnail);

        if (currentArticle.getTitle() != null){
            holder.tv_article_title.setText(currentArticle.getTitle());
        }

        if (currentArticle.getShortDescription() != null){
            holder.tv_article_description.setText(currentArticle.getShortDescription());
        }

        if (currentArticle.getAuthor()!= null){
            holder.tv_author.setText(currentArticle.getAuthor());
        }

        String publishDate = null;
        try {
            publishDate = AppDateUtils.longDateToDeFormat(currentArticle.getPublishDate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (publishDate != null){
            holder.tv_publish_date.setText(publishDate);
        }

        holder.article_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mArticlesFragmentAdapterListener.onArticleClicked(currentArticle);
            }
        });

        holder.article_item_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mArticlesFragmentAdapterListener.onArticleLongClicked(currentArticle);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mArticles == null) return 0;
        return mArticles.size();
    }

    public void setData(List<Article> allArticles){
        mArticles = allArticles;
        notifyDataSetChanged();
    }

    public void removeArticle(Article article){
        try {
            mArticles.remove(article);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ArticlesAdapterViewHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout article_item_layout;
        private ImageView iv_article_thumbnail;
        private TextView tv_article_title;
        private TextView tv_article_description;
        private TextView tv_author;
        private TextView tv_publish_date;

        ArticlesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            article_item_layout = itemView.findViewById(R.id.article_item_layout);
            iv_article_thumbnail = itemView.findViewById(R.id.iv_article_thumbnail);
            tv_article_title = itemView.findViewById(R.id.tv_article_title);
            tv_article_description = itemView.findViewById(R.id.tv_article_description);
            tv_author = itemView.findViewById(R.id.tv_author);
            tv_publish_date = itemView.findViewById(R.id.tv_publish_date);
        }

    }
}
