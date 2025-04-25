import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebScraper {
    public static void main(String[] args) {
        try {
            String site = "https://www.bbc.com/news";
            Document doc = Jsoup.connect(site).get();
            String title = doc.title();
            Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
            for(Element heading: headings){
                System.out.println(heading);
            }
            Elements links = doc.select("a[href]");
            for (Element link: links){
                System.out.println(link);
            }
            List<NewsArticle> articles = new ArrayList<>();

            for (Element link : links) {
                String url = link.attr("abs:href");
                System.out.println("URL to fetch: " + url);
                if (url.isEmpty() || url.equals("undefined")) {
                    continue;
                }
                try {
                    Document docLink = Jsoup.connect(url).get();

                    String headline = "N/A";
                    String date = "N/A";
                    String author = "N/A";

                    Element innerHeading = docLink.selectFirst("h1");
                    if (innerHeading != null) {
                        headline = innerHeading.text();
                    }

                    Element innerDate = docLink.selectFirst("time");
                    if (innerDate != null) {
                        date = innerDate.text();
                    }

                    Element innerAuthor = docLink.selectFirst("div[data-testid=byline-new-contributors] span");
                    if (innerAuthor != null) {
                        author = innerAuthor.text();
                    }
                    articles.add(new NewsArticle(headline, date, author));
                } catch (IOException e) {
                    System.err.println("Error fetching URL: " + url);
                    e.printStackTrace();
                }
            }

            for (NewsArticle article : articles) {
                System.out.println(article);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
