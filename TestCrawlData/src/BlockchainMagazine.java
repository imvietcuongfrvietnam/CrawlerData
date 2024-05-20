import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BlockchainMagazine {
    public static String articleType = "News Article";

    public static void main(String[] args) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter("D:\\2023.2\\it3100\\CrawlerData\\TestCrawlData\\src/data.csv", true))) {
            for (int i = 1; i <= 218; i++) {
                String urlSource = "https://shorturl.at/KOHRD";
                String urlPerPage = "https://blockchainmagazine.net/blockchain-news/page/" + i + "/?_gl=1%2A1f4tsgq%2A_ga%2AMTA0OTg0NTQxNy4xNzExNjkxMDIy%2A_ga_5SYDREVZTY%2AMTcxNTk1NzY0OC4yLjEuMTcxNTk1Nzc4Ny4zMi4wLjA";

                Document doc = Jsoup.connect(urlPerPage).get();
                Elements articles = doc.select(".stm_news_grid__image a[href]");

                for (Element article : articles) {
                    String linkHref = article.attr("href");

                    try {
                        Document docURL = Jsoup.connect(linkHref).get();
                        Element articleTitleElement = docURL.selectFirst("div.stm_post_view .stm_post_view__title");
                        if (articleTitleElement == null) {
                            System.err.println("Không tìm thấy tiêu đề tại URL " + linkHref);
                            continue;
                        }

                        Element articleSummaryElement = docURL.selectFirst("div.stm_post_view__content > p");
                        if (articleSummaryElement == null) {
                            System.err.println("Không tìm thấy tóm tắt tại URL " + linkHref);
                            continue;
                        }

                        Element articleContentElement = docURL.select(".stm_post_view__content").first();
                        if (articleContentElement == null) {
                            System.err.println("Không tìm thấy nội dung tại URL " + linkHref);
                            continue;
                        }

                        Element dateElement = docURL.selectFirst("div.stm_post_view__info i.stm-clock6");
                        if (dateElement == null) {
                            System.err.println("Không tìm thấy ngày tạo tại URL " + linkHref);
                            continue;
                        }

                        Element authorElement = docURL.selectFirst("div.stm_post_view__info strong");
                        if (authorElement == null) {
                            System.err.println("Không tìm thấy tác giả tại URL " + linkHref);
                            continue;
                        }

                        Elements categoryElements = docURL.select(".stm_post_view__categories a");
                        String categories = "";
                        for (Element categoryElement : categoryElements) {
                            categories += categoryElement.text() + ", ";
                        }
// Xóa dấu phẩy thừa ở cuối chuỗi
                        categories = categories.replaceAll(", $", "");


                        String title = articleTitleElement.text();
                        String summary = articleSummaryElement.text();
                        String content = articleContentElement.text();
                        String date = dateElement.text().split(" by")[0].trim();
                        String author = authorElement.text();

                        printWriter.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"\",\"%s\",\"%s\"\n",
                                linkHref, urlSource, articleType, summary, title, content, date, author, categories);
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

