package com.searcher.google.services;


import com.searcher.google.entities.WebPage;
import com.searcher.google.repositories.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    public List<WebPage> search(String textSearch) {
        return searchRepository.search(textSearch);
    }

    public void save(WebPage webPage) {
        searchRepository.save(webPage);
    }

    public boolean exists(String link) {
        return searchRepository.exists(link);
    }

    public List<WebPage> getLinksToIndex() {
        return searchRepository.getLinksToIndex();
    }
}
