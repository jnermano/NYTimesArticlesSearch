package ht.mbds.nytimesarticlessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import ht.mbds.nytimesarticlessearch.R;
import ht.mbds.nytimesarticlessearch.model.Article;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by Ermano
 * on 2/17/2018.
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Article> articles;
    Context context;

    private final int WITH_IMAGE = 0, WITHOUT_IMAGE = 1;

    public ArticleAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case WITH_IMAGE:
                View v1 = inflater.inflate(R.layout.item_layout_with_image, parent, false);
                viewHolder = new ViewHolderWithImage(v1);
                break;
            case WITHOUT_IMAGE:
                View v2 = inflater.inflate(R.layout.item_layout_without_image, parent, false);
                viewHolder = new ViewHolderWithoutImage(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.item_layout_without_image, parent, false);
                viewHolder = new ViewHolderWithoutImage(v3);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Article article = articles.get(position);

        switch (holder.getItemViewType()) {
            case WITH_IMAGE:
                ViewHolderWithImage viewHolderWithImage = (ViewHolderWithImage) holder;
                viewHolderWithImage.getItem_headline().setText(article.getHeadline());
                viewHolderWithImage.getItem_snippet().setText(article.getSnippet());

                Picasso.with(getContext())
                        .load("https://www.nytimes.com/" + article.getThumbnail())
                        .placeholder(R.drawable.loading)
                        .transform(new RoundedCornersTransformation(10, 10))
                        .into(viewHolderWithImage.getItem_img(), new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });

                break;
            case WITHOUT_IMAGE:
                ViewHolderWithoutImage viewHolderWithoutImage = (ViewHolderWithoutImage) holder;
                viewHolderWithoutImage.getItem_headline().setText(article.getHeadline());
                viewHolderWithoutImage.getItem_snippet().setText(article.getSnippet());
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (articles.get(position).getThumbnail() != null)
            return WITH_IMAGE;
        else
            return WITHOUT_IMAGE;

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public Article getArticle(int position) {
        return articles.get(position);
    }


}
