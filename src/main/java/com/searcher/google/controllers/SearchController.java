package com.searcher.google.controllers;


import com.searcher.google.entities.WebPage;
import com.searcher.google.services.SearchService;
import com.searcher.google.services.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private SpiderService spiderService;

    @GetMapping("/api/search")
    public List<WebPage> search(@RequestParam Map<String, String> params) {
        String query = params.get("query");
        return searchService.search(query);
    }

    @GetMapping("/api/test")
    public String search() {
        return spiderService.indexWebPage();
    }
}
