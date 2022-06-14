package com.searcher.google.entities;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "webpage")
@ToString @EqualsAndHashCode
@Getter @Setter @NoArgsConstructor
public class WebPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    public WebPage(String url) {
        this.url = url;
    }

}
