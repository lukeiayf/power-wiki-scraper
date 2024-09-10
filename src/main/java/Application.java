import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final String BASE_URL = "https://powerlisting.fandom.com";
    private static final String CATEGORY_URL = BASE_URL + "/wiki/Category:Powers";

    public static void main(String[] args) {
        List<String> powers = new ArrayList<>();

        try {
            // Start at the first page of the powers
            String nextPage = CATEGORY_URL;

            while (nextPage != null) {
                // Fetch and parse the current page
                Document doc = Jsoup.connect(nextPage).get();

                // Extract powers (all links under the .category-page__members element)
                Elements powerLinks = doc.select(".category-page__member-link");
                for (Element link : powerLinks) {
                    String powerName = link.text();
                    powers.add(powerName);
                    System.out.println("Extracted power: " + powerName);
                }

                // Check if there's a next page button and update the nextPage URL
                Element nextLink = doc.selectFirst(".category-page__pagination-next");
                nextPage = (nextLink != null) ? nextLink.attr("href") : null;
            }

            // Write the extracted powers to a CSV file
            writePowersToCSV(powers, "powers.csv");
            System.out.println("Finished scraping. Powers written to powers.csv");
            Thread.sleep(2000);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writePowersToCSV(List<String> powers, String fileName) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            // Write header
            writer.writeNext(new String[]{"Power Name"});

            // Write each power to a new line
            for (String power : powers) {
                writer.writeNext(new String[]{power});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
