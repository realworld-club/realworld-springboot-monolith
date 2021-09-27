package com.example.realworld.application.articles.dto;

import com.example.realworld.application.articles.persistence.Article;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class ResponseMultiArticles {

    private final List<ResponseArticle> articles;
    private final int articleCount;

    private ResponseMultiArticles(List<ResponseArticle> articles, int articleCount) {
        this.articles = articles;
        this.articleCount = articleCount;
    }

    public static ResponseMultiArticles of(final List<Article> articles) {
        List<ResponseArticle> responseArticles = articles.stream()
                .map(ResponseArticle::from)
                .collect(Collectors.toList());
        return new ResponseMultiArticles(responseArticles, articles.size());
    }
}
