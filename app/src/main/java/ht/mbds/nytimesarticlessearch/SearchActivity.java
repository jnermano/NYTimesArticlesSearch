package ht.mbds.nytimesarticlessearch;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ht.mbds.nytimesarticlessearch.adapters.ArticleAdapter;
import ht.mbds.nytimesarticlessearch.customtab.CustomTabActivityHelper;
import ht.mbds.nytimesarticlessearch.customtab.WebviewFallback;
import ht.mbds.nytimesarticlessearch.fragments.FilterFragment;
import ht.mbds.nytimesarticlessearch.models.Article;
import ht.mbds.nytimesarticlessearch.utils.EndlessRecyclerViewScrollListener;
import ht.mbds.nytimesarticlessearch.utils.ItemClickSupport;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity
        implements FilterFragment.FilterFragmentListener,
        CustomTabActivityHelper.ConnectionCallback{

    @BindView(R.id.rvItems)
    RecyclerView rvArticles;

    List<Article> articles;

    ArticleAdapter adapter;

    EndlessRecyclerViewScrollListener scrollListener;

    // Filter variables
    String queryFilter;
    String beginDate;
    String sortOrder;
    String newsDesk;


    private CustomTabActivityHelper customTabActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init Bind of views
        ButterKnife.bind(this);

        // init recycleview compos
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        articles = new ArrayList<Article>();

        adapter = new ArticleAdapter(this, articles);

        rvArticles.setLayoutManager(gridLayoutManager);
        rvArticles.setAdapter(adapter);

        // set up scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        rvArticles.addOnScrollListener(scrollListener);

        /**
         * On item click listener
         */
        ItemClickSupport.addTo(rvArticles).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                String url = adapter.getArticle(position).getWebUrl();

                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder(customTabActivityHelper.getSession());

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);

                int requestCode = 100;

                PendingIntent pendingIntent = PendingIntent.getActivity(SearchActivity.this,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                intentBuilder.setActionButton(bitmap, "Share Link", pendingIntent, true);


                intentBuilder.setStartAnimations(SearchActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
                intentBuilder.setExitAnimations(SearchActivity.this, android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

                CustomTabActivityHelper.openCustomTab(
                        SearchActivity.this, intentBuilder.build(), Uri.parse(url), new WebviewFallback());

            }
        });

        /// First load of data
        loadNextDataFromApi(0);

        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customTabActivityHelper.setConnectionCallback(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }


    /**
     * Load data from api
     * @param offset
     */
    public void loadNextDataFromApi(int offset) {

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.nytimes.com/svc/search/v2/articlesearch.json").newBuilder();
        urlBuilder.addQueryParameter("api-key", getResources().getString(R.string.api_key));
        urlBuilder.addQueryParameter("page", offset + "");

        if (queryFilter != null) {
            urlBuilder.addQueryParameter("query", queryFilter);
        }

        if (beginDate != null && beginDate.length() == 10)
            urlBuilder.addQueryParameter("begin_date", beginDate.replace("-", ""));

        if (sortOrder != null)
            urlBuilder.addQueryParameter("sort", sortOrder);

        if (newsDesk != null && newsDesk.length() > 0)
            urlBuilder.addQueryParameter("fq", String.format(Locale.US, "news_desk:(%s)", newsDesk));

        String url = urlBuilder.build().toString();

        Log.v("query", url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please wait.");
        pd.setCancelable(false);

        if (offset == 0)
            pd.show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pd.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();

                    JSONObject json = new JSONObject(responseData);

                    JSONObject jsonObjectResponse = json.getJSONObject("response");

                    final JSONArray jsonArrayDoc = jsonObjectResponse.getJSONArray("docs");

                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            List<Article> response_articles = Article.fromJson(jsonArrayDoc);

                            int curSize = adapter.getItemCount();

                            articles.addAll(response_articles);

                            adapter.notifyItemRangeInserted(curSize, response_articles.size());

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }
        });

    }

    /**
     * Menu items management
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setBackgroundResource(android.R.drawable.edit_text);

        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.BLUE);
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(Color.GRAY);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                queryFilter = null;

                resetEndlessScrollState();

                loadNextDataFromApi(0);

                return true;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599

                searchView.clearFocus();

                queryFilter = query;

                resetEndlessScrollState();

                loadNextDataFromApi(0);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Menu item click management
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {

            showFilterDialog();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Show advanced filter
     */
    private void showFilterDialog(){
        FragmentManager fm = getSupportFragmentManager();
        FilterFragment filterFragment = FilterFragment.newInstance("filter", beginDate, sortOrder, newsDesk);
        filterFragment.show(fm, "filter_fragment");
    }


    /**
     * Listen for filter submited by user
     * @param bDate
     * @param order
     * @param nDesk
     */
    @Override
    public void onSubmitFilters(String bDate, String order, String nDesk) {
        Log.v("filters", String.format(Locale.US, "%s - %s - %s", bDate, order, nDesk));
        this.beginDate  = bDate;
        this.sortOrder = order;
        this.newsDesk = nDesk;

        resetEndlessScrollState();

        loadNextDataFromApi(0);

    }

    /**
     * Reset endless scroll state
     */
    private void resetEndlessScrollState() {
        articles.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    /**
     * Check is device is connected to the internet
     * @return
     */
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCustomTabsConnected() {
        Log.v("Search Activity", "Custom Tabs Connected");
    }

    @Override
    public void onCustomTabsDisconnected() {
        Log.v("Search Activity", "Custom Tabs Disconnected");
    }
}
