package com.mycompany.app;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver-win64\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.get("http://www.papercdcase.com/index.php");

            String artist = "", title = "";
            List<String> tracks = new java.util.ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader("../data/data.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Artist: ")) {
                    artist = line.replace("Artist: ", "").trim();
                } else if (line.startsWith("Title: ")) {
                    title = line.replace("Title: ", "").trim();
                } else if (line.matches("^\\d+\\.\\s+.+")) {
                    tracks.add(line.replaceFirst("^\\d+\\.\\s+", ""));
                }
            }
            reader.close();

            driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[1]/td[2]/input")).sendKeys(artist);
            driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[2]/td[2]/input")).sendKeys(title);

            String[] trackXPaths = {
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[1]/table/tbody/tr[1]/td[2]/input",
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]/input",
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]/input",
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[1]/table/tbody/tr[4]/td[2]/input",
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[1]/table/tbody/tr[5]/td[2]/input",
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[1]/table/tbody/tr[6]/td[2]/input",
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[1]/table/tbody/tr[7]/td[2]/input",
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[1]/table/tbody/tr[8]/td[2]/input",
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[2]/table/tbody/tr[1]/td[2]/input",
                    "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[2]/table/tbody/tr[2]/td[2]/input"
            };

            for (int i = 0; i < tracks.size() && i < 10; i++) {
                driver.findElement(By.xpath(trackXPaths[i])).sendKeys(tracks.get(i));
            }

            driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[4]/td[2]/input[2]")).click();
            driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[5]/td[2]/input[2]")).click();

            driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/p/input")).click();

            TimeUnit.SECONDS.sleep(2);
            String pdfUrl = driver.getCurrentUrl();

            InputStream in = new URL(pdfUrl).openStream();
            Files.createDirectories(Paths.get("../result"));
            Files.copy(in, Paths.get("../result/cd.pdf"), StandardCopyOption.REPLACE_EXISTING);
            in.close();

            System.out.println("PDF saved to ../result/cd.pdf");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
