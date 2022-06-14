package com.searcher.google.services;

import com.searcher.google.entities.WebPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class SpiderService {

    @Autowired
    private SearchService searchService;

    public void indexWebPage(){
        List<WebPage> linksToIndex = searchService.getLinksToIndex();
        linksToIndex.forEach(WebPage -> {
            try {
                indexarPagina(WebPage);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        });

    }

    private void indexarPagina(WebPage webPage) {
        String url = webPage.getUrl();
        //obtenemos el contenido de la pagina
        String content = getWebContect(url);
        //controlamos que no este vacio
        if (isBlank(content)){
            return;
        }
        String domain = getDomain(url);
        setAndSaveWebPage(webPage, content);
        saveLinks(domain, content);
    }

    private String getDomain(String url) {
        String[] aux = url.split("/");
        return aux[0] + "//" + aux[2];
    }

    public void saveLinks(String domain, String content) {
        List<String> links = getLinks(domain, content);
        links.stream().filter(link -> !searchService.exists(link))
                .map(link -> new WebPage(link))
                .forEach(webPage -> searchService.save(webPage));
    }

    public void setAndSaveWebPage(WebPage webPage, String content){
        //obtenemos el titulo y descripcion de la pagina
        String title = getTitle(content);
        String description = getDescription(content);
        //seteamos los valores de la pagina para guardar en base de datos
        webPage.setTitle(title);
        webPage.setDescription(description);

        //guardamos la pagina en base de datos
        searchService.save(webPage);

    }

    public List<String> getLinks(String domain, String content){
        List<String> links = new ArrayList<>();
        //cortamos el html de la pagina por los href
        String[] aux = content.split("<a href=\"");
        List<String> listHref = Arrays.asList(aux);
        //borramos el primer valor que no nos interesa
        listHref.remove(0);
        //recorremos la lista de href y obtenemos los links
        listHref.forEach(x -> {
            String[] aux2 = x.split("\"");
            links.add(aux2[0]);
        });

        return cleanLinks(domain, links);

    }

    private List<String> cleanLinks(String domain, List<String> links){
        String[] excludedExtensions = new String[]{"jpg", "png", "gif", "jpeg", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "zip", "rar", "css", "js", "json", "woff2"};
        List<String> resultLinks = links.stream().filter(link -> Arrays.stream(excludedExtensions)
                .noneMatch(link::endsWith))
                .map(link -> link.startsWith("/") ? domain + link : link)
                .toList();
        //Controlamos que no se repita el link creamos un hashset ya que no admite elementos repetidos, luego lo pasamos de nuevo a lista
        List<String> uniqueLinks = new ArrayList<>();
        uniqueLinks.addAll(new HashSet<String>(resultLinks));

        return uniqueLinks;
    }

    public String getTitle(String content){
        String[] aux = content.split("<title>");
        aux = aux[1].split("</title>");
        return aux[0];
    }

    public String getDescription(String content){
        String[] aux = content.split("<meta name=\"description\" content=\"");
        aux = aux[1].split("\">");
        return aux[0];
    }

    private String getWebContect(String link){
        try{
            URL url = new URL(link);

            //abrimos la concexion con la pagina
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //traemos la cabecera de la pagina para saber en que codigo esta
            String encoding = conn.getContentEncoding();

            InputStream input = conn.getInputStream();

            //descargamos el contenido de la pagina y lo guardamos en una variable
            Stream<String> lines = new BufferedReader(new InputStreamReader(input)).lines();

            return lines.collect(Collectors.joining());

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return "";
    }
}
