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
    TextView item_headline;

    @BindView(R.id.item_snippet_no_img)
    TextView item_snippet;


    public ViewHolderWithoutImage(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getItem_headline() {
        return item_headline;
    }

    public void setItem_headline(TextView item_headline) {
        this.item_headline = item_headline;
    }

    public TextView getItem_snippet() {
        return item_snippet;
    }

    public void setItem_snippet(TextView item_snippet) {
        this.item_snippet = item_snippet;
    }
}
