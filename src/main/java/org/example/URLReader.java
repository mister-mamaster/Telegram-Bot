package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.*;

public class URLReader {

    private final String ROOT_URL = "https://dnd.su/spells/";

    public URLReader() throws MalformedURLException {
    }

    public String getData(String name) throws Exception {
        Indexing indexing = new Indexing();
        name = indexing.getIndex(name);
        Document document = Jsoup.connect(ROOT_URL + name + "/").get();

        StringBuilder answer = new StringBuilder();

        Elements element = document.select("div.card-body > ul.params");

        answer.append(element.select("li.size-type-alignment").text()).append("\n");
        answer.append(element.select("li > strong").get(0).text()).append(element.select("li").get(1).text().replace(element.select("li > strong").get(0).text(), "")).append("\n");
        answer.append(element.select("li > strong").get(1).text()).append(element.select("li").get(2).text().replace(element.select("li > strong").get(1).text(), "")).append("\n");
        answer.append(element.select("li > strong").get(2).text()).append(element.select("li").get(3).text().replace(element.select("li > strong").get(2).text(), "")).append("\n");
        answer.append(element.select("li > strong").get(3).text()).append(element.select("li").get(4).text().replace(element.select("li > strong").get(3).text(), "")).append("\n");
        answer.append(element.select("li > strong").get(4).text()).append(element.select("li").get(5).text().replace(element.select("li > strong").get(4).text(), "")).append("\n");
        answer.append(element.select("li > strong").get(5).text()).append(element.select("li").get(6).text().replace(element.select("li > strong").get(5).text(), "")).append("\n");
        try {
            answer.append(element.select("li > strong").get(6).text()).append(element.select("li").get(7).text().replace(element.select("li > strong").get(6).text(), "")).append("\n");
        } catch (IndexOutOfBoundsException ex){}

        int index = 0;
        boolean flag = false;
        while(!flag){
            try {
                answer.append(element.select("div > p").get(index).text());
                index++;
            } catch (IndexOutOfBoundsException ex){
                flag = true;
            }
        }

        return answer.toString();
    }
}
