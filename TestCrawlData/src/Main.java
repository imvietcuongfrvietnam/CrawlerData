import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("D:\\2023.2\\it3100\\CrawlerData\\TestCrawlData\\src/data.csv"))) {
            pw.println("URL, Web Source, Article type, Article summary, Article title, Content, Creation date, Hashtag, Author, Category");

            for (int i = 1; i <= 426; i++) {
                String urlPerPage = "https://blockonomi.com/all/page/" + i + "/";
                Document doc = Jsoup.connect(urlPerPage).get();
                Elements urlPerPageLinks = doc.select("article.l-post.grid-post.grid-base-post div.media a[href]");
                for (Element link : urlPerPageLinks) {
                    String linkHref = link.attr("href");
                    try {
                        Document docURL = Jsoup.connect(linkHref).get();
                        Element articleTitle = docURL.selectFirst(".is-title.post-title");
                        String articleType = "News Article"; // Assuming all articles are news articles
                        String articleSummary = ""; // No summary extraction here
                        String articleTitleText = articleTitle != null ? articleTitle.text() : "";
                        StringBuilder contentBuilder = new StringBuilder();
                        Element contentElement = docURL.selectFirst(".post-content.cf.entry-content.content-spacious");
                        if (contentElement != null) {
                            Elements paragraphs = contentElement.select("p");
                            for (Element p : paragraphs) {
                                contentBuilder.append(p.text()).append(" ");
                            }
                        }
                        String content = contentBuilder.toString();
                        Elements postDates = docURL.select(".post-date");
                        String postDateText = postDates.isEmpty() ? "" : postDates.first().text();
                        Elements categories = docURL.select("[rel=category]");
                        String categoryText = categories.isEmpty() ? "" : categories.text();
                        Element author = docURL.selectFirst("[rel=author]");
                        String authorText = author != null ? author.text() : "";

                        pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"\",\"%s\",\"%s\"\n",
                                linkHref, "https://blockonomi.com/", articleType, articleSummary, articleTitleText, content, postDateText, authorText, categoryText);
                    } catch (IOException e) {
                        System.err.println("Không thể kết nối đến URL: " + linkHref);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Dữ liệu đã được lưu vào file CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}