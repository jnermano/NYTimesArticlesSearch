package ht.mbds.nytimesarticlessearch.model;

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

    private String web_url;
    private String snippet;
    private String headline;
    private String pub_date;
    private String thumbnail;

    public static Article fromJson(JSONObject jsonObject) {
        Article article = new Article();

        try {

            article.setWeb_url(jsonObject.getString("web_url"));
            article.setSnippet(jsonObject.getString("snippet"));
            article.setPub_date(jsonObject.getString("pub_date"));

            JSONObject jsonObjectHeadline = jsonObject.getJSONObject("headline");
            article.setHeadline(jsonObjectHeadline.getString("main"));

            JSONArray jsonArrayMultimedia = jsonObject.getJSONArray("multimedia");

            for (int i = 0; i < jsonArrayMultimedia.length(); i++) {
                JSONObject jsonObjectMedia = jsonArrayMultimedia.getJSONObject(i);
                if (jsonObjectMedia.getString("subtype").equals("thumbnail")) {
                    article.setThumbnail(jsonObjectMedia.getString("thumbnail"));
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

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
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

    public String getPub_date() {
        return pub_date;
    }

    public void setPub_date(String pub_date) {
        this.pub_date = pub_date;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
