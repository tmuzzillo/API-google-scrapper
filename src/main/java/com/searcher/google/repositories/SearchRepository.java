package com.searcher.google.repositories;

import com.searcher.google.entities.WebPage;

import java.util.List;

public interface SearchRepository {

    List<WebPage> search(String textSearch);

    void save(WebPage webPage);

    boolean exists(String link);

    WebPage getByUrl(String url);

    List<WebPage> getLinksToIndex();
}
