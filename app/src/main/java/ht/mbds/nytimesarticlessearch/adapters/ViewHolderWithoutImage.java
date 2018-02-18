package ht.mbds.nytimesarticlessearch.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ht.mbds.nytimesarticlessearch.R;

/**
 * Created by Ermano
 * on 2/17/2018.
 */

public class ViewHolderWithoutImage extends RecyclerView.ViewHolder {


    @BindView(R.id.item_headline_no_img)
    TextView itemHeadline;

    @BindView(R.id.item_snippet_no_img)
    TextView itemSnippet;


    public ViewHolderWithoutImage(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getItemHeadline() {
        return itemHeadline;
    }

    public void setItemHeadline(TextView itemHeadline) {
        this.itemHeadline = itemHeadline;
    }

    public TextView getItemSnippet() {
        return itemSnippet;
    }

    public void setItemSnippet(TextView itemSnippet) {
        this.itemSnippet = itemSnippet;
    }
}
