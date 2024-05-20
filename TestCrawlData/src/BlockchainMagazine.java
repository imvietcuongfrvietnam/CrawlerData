import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

                        Element dateElement = docURL.selectFirst("div.stm_post_view__info .date");
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
                        StringBuilder categories = new StringBuilder();
                        for (Element categoryElement : categoryElements) {
                            categories.append(categoryElement.text()).append(", ");
                        }
                        // Xóa dấu phẩy thừa ở cuối chuỗi
                        if (!categories.isEmpty()) {
                            categories.setLength(categories.length() - 2);
                        }

                        String title = articleTitleElement.text();
                        String summary = articleSummaryElement.text();
                        String content = articleContentElement.text();
                        String date = parseDate(dateElement.text().split(" by")[0].trim());
                        String author = authorElement.text();
                        title = title.replace("\"", "\"\"");
                        summary = summary.replace("\"", "\"\"");
                        content = content.replace("\"", "\"\"");
                        date = date.replace("\"", "\"\"");
                        author = author.replace("\"", "\"\"");
                        printWriter.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"\",\"%s\",\"%s\"\n",
                                linkHref, urlSource, articleType, summary, title, content, date, author, categories.toString());
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

    private static String parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            LocalDate date = LocalDate.parse(dateStr, formatter);
            return date.toString();
        } catch (DateTimeParseException e) {
            System.err.println("Lỗi khi phân tích ngày: " + dateStr);
            return "";
        }
    }
}
