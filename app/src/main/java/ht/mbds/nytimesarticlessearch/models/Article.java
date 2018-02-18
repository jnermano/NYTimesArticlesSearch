package ht.mbds.nytimesarticlessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ermano
 * on 2/17/2018.
 */

public class Article {

    private String webUrl;
    private String snippet;
    private String headline;
    private String pubDate;
    private String thumbnail;

    public static Article fromJson(JSONObject jsonObject) {
        Article article = new Article();

        try {

            article.setWebUrl(jsonObject.getString("web_url"));
            article.setSnippet(jsonObject.getString("snippet"));
            article.setPubDate(jsonObject.getString("pub_date"));

            JSONObject jsonObjectHeadline = jsonObject.getJSONObject("headline");
            article.setHeadline(jsonObjectHeadline.getString("main"));

            JSONArray jsonArrayMultimedia = jsonObject.getJSONArray("multimedia");

            for (int i = 0; i < jsonArrayMultimedia.length(); i++) {
                JSONObject jsonObjectMedia = jsonArrayMultimedia.getJSONObject(i);
                if (jsonObjectMedia.getString("subtype").equals("xlarge")) {
                    article.setThumbnail(jsonObjectMedia.getString("url"));
                    break;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return article;

    }

    public static List<Article> fromJson(JSONArray jsonArray) {
        List<Article> articles = new ArrayList<>();
        JSONObject jsonObject;

        for (int i = 0; i < jsonArray.length(); i++) {

            try {

                jsonObject = jsonArray.getJSONObject(i);

                Article article = Article.fromJson(jsonObject);

                if (article != null)
                    articles.add(article);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        return articles;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
