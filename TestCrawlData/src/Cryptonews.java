import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Cryptonews {
    private static String articleType = "News Article";
    public static void main(String[] args) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter("src/data.csv", true))) {
            for (int i = 1; i <= 1916; i++) {
                String urlSource = "https://cryptonews.com/";
                String urlPerPage = "https://cryptonews.com/news/page/" + i + "/";

                Document doc = Jsoup.connect(urlPerPage).get();
                Elements articles = doc.select(".article__title article__title--md a[href]");

                for (Element article : articles) {
                    String linkHref = article.attr("href");

                    try {
                        Document docURL = Jsoup.connect(linkHref).get();
                        Element articleTitleElement = docURL.selectFirst(".mb-10");
                        if (articleTitleElement == null) {
                            System.err.println("Không tìm thấy tiêu đề tại URL " + linkHref);
                            continue;
                        }

                        Element articleSummaryElement = docURL.selectFirst(".wp-caption-text > p");
                        if (articleSummaryElement == null) {
                            System.err.println("Không tìm thấy tóm tắt tại URL " + linkHref);
                            continue;
                        }

                        Element articleContentElement = docURL.select(".article-single__content #category_contents_details").first();
                        if (articleContentElement == null) {
                            System.err.println("Không tìm thấy nội dung tại URL " + linkHref);
                            continue;
                        }
                        Elements paragraphElements = articleContentElement.select("p");
                        StringBuilder contentBuilder = new StringBuilder();
                        for (Element paragraphElement : paragraphElements) {
                            contentBuilder.append(paragraphElement.text()).append("\n");
                        }

                        Element dateElement = docURL.selectFirst("div.fs-14.date-section time");
                        if (dateElement == null) {
                            System.err.println("Không tìm thấy thẻ time tại URL " + linkHref);
                            continue;
                        }

                        Element authorElement = docURL.selectFirst("div.author-title a");
                        if (authorElement == null) {
                            System.err.println("Không tìm thấy tác giả tại URL " + linkHref);
                            continue;
                        }

                        Element categoryElement = docURL.select("div.news-one-category a").first();
                        if (categoryElement == null) {
                            System.err.println("Không tìm thấy thể loại tại URL " + linkHref);
                            continue;
                        }

                        String title = articleTitleElement.text();
                        String summary = articleSummaryElement.text();
                        String content = contentBuilder.toString().trim();
                        String date = dateElement.text();
                        String author = authorElement.text();
                        String category = categoryElement.text();

                        printWriter.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"\",\"%s\",\"%s\"\n",
                                linkHref, urlSource, articleType, summary, title, content, date, author, category);
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
