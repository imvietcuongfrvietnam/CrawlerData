import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Cryptonews {
    private static final String ARTICLE_TYPE = "News Article";

    public static void main(String[] args) {
        // Mở file CSV để ghi dữ liệu
        try (PrintWriter pw = new PrintWriter(new FileWriter("D:\\2023.2\\it3100\\CrawlerData\\TestCrawlData\\src\\data.csv"))) {
            pw.println("URL, Web Source, Article type, Article summary, Article title, Content, Creation date, Hashtag, Author, Category");

            for (int i = 1; i <= 1916; i++) {
                String urlSource = "https://cryptonews.com/";
                String urlPerPage = "https://cryptonews.com/news/blockchain-news/page/" + i + "/";

                try {
                    // Kết nối đến trang cụ thể
                    Document doc = Jsoup.connect(urlPerPage).get();
                    Elements articles = doc.select(".news-one-title a.article__title.article__title--md[href]");

                    System.out.println("Page " + i + ": Found " + articles.size() + " articles");

                    for (Element article : articles) {
                        String linkHref = article.attr("href");

                        try {
                            // Kết nối đến bài viết cụ thể
                            Document docURL = Jsoup.connect(linkHref).get();
                            Element articleTitleElement = docURL.selectFirst(".mb-10");
                            if (articleTitleElement == null) {
                                System.err.println("Không tìm thấy tiêu đề tại URL " + linkHref);
                                continue;
                            }

                            System.out.println("Tiêu đề bài viết: " + articleTitleElement.text());

                            Element articleSummaryElement = docURL.select(".wp-caption-text p").first();
                            if (articleSummaryElement == null) {
                                System.err.println("Không tìm thấy tóm tắt tại URL " + linkHref);
                            } else {
                                System.out.println("Tóm tắt: " + articleSummaryElement.text());
                            }

                            // Lấy tất cả các phần tử <p> trong class "article-single__content category_contents_details"
                            Elements paragraphElements = docURL.select(".article-single__content.category_contents_details p");

                            if (paragraphElements.isEmpty()) {
                                System.err.println("Không tìm thấy nội dung tại URL " + linkHref);
                                continue;
                            }

                            StringBuilder contentBuilder = new StringBuilder();

                            for (Element paragraphElement : paragraphElements) {
                                contentBuilder.append(paragraphElement.text()).append("\n");
                            }
                            String content = contentBuilder.toString().trim(); // Lấy nội dung cuối cùng và loại bỏ khoảng trắng thừa

                            System.out.println("Nội dung: " + content);

                            Element dateElement = docURL.selectFirst("div.fs-14.date-section time");
                            if (dateElement == null) {
                                System.err.println("Không tìm thấy thẻ time tại URL " + linkHref);
                            } else {
                                System.out.println("Ngày tạo: " + dateElement.text());
                            }

                            Element authorElement = docURL.selectFirst("div.author-title a");
                            if (authorElement == null) {
                                System.err.println("Không tìm thấy tác giả tại URL " + linkHref);
                            } else {
                                System.out.println("Tác giả: " + authorElement.text());
                            }

                            Element categoryElement = docURL.selectFirst("div.news-one-category a");
                            if (categoryElement == null) {
                                System.err.println("Không tìm thấy thể loại tại URL " + linkHref);
                            } else {
                                System.out.println("Thể loại: " + categoryElement.text());
                            }

                            String title = articleTitleElement.text();
                            String summary = articleSummaryElement != null ? articleSummaryElement.text() : "";
                            String date = dateElement != null ? dateElement.text() : "";
                            String author = authorElement != null ? authorElement.text() : "";
                            String category = categoryElement != null ? categoryElement.text() : "";

                            // Ghi dữ liệu vào file CSV
                            pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"\",\"%s\",\"%s\"\n",
                                    linkHref, urlSource, ARTICLE_TYPE, summary, title, content, date, author, category);

                            System.out.println("Đã ghi bài viết: " + title);
                        } catch (IOException e) {
                            System.err.println("Không thể kết nối đến URL: " + linkHref);
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Không thể kết nối đến trang: " + urlPerPage);
                    e.printStackTrace();
                }

                // Thêm khoảng nghỉ giữa các yêu cầu
                try {
                    Thread.sleep(2000); // Nghỉ 2 giây
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Dữ liệu đã được lưu vào file CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
