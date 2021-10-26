package smoke;

import org.junit.After;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import pages.Utils;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class BaseTest {
    protected WebDriver driver;
    private String URL;
    private String browser;
    private Boolean headless;

    public BaseTest() {
        loadConfig();
    }

    private void loadConfig() {
        Properties props = new Properties();
        try {
            FileReader fr = new FileReader("app.config");
            props.load(fr);
            URL = props.getProperty("URL");
            browser = props.getProperty("browser");
            headless = Boolean.parseBoolean(props.getProperty("headless"));
            fr.close();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException();
        }
    }

    @Before
    public void setUp() {
        if (browser.equalsIgnoreCase("chrome")) {
            String operatingSystem = System.getProperty("os.name");
            if (operatingSystem.startsWith("Windows")) {
                System.setProperty("webdriver.chrome.driver", "Resources/chromedriver.exe");
            } else {
                throw new IllegalArgumentException("OS '" + operatingSystem + "' is not supported!");
            }

            ChromeOptions options = new ChromeOptions();
            if (headless) {
                options.addArguments("--headless");
                options.addArguments("User-Agent=" + Utils.getUserAgent());
            }
            driver = (WebDriver) new ChromeDriver(options);
        }
        else if (browser.equalsIgnoreCase("firefox")) {
            String operatingSystem = System.getProperty("os.name");
            if (operatingSystem.startsWith("Windows")) {
                System.setProperty("webdriver.gecko.driver", "Resources/geckodriver.exe");
            } else {
                throw new IllegalArgumentException("OS '" + operatingSystem + "' is not supported!");
            }

            FirefoxOptions options = new FirefoxOptions();
            if (headless) {
                options.setHeadless(true);
            }
            driver = (WebDriver) new FirefoxDriver(options);
        }
        else if (browser.equalsIgnoreCase("edge")) {
            String operatingSystem = System.getProperty("os.name");
            if (operatingSystem.startsWith("Windows")) {
                System.setProperty("webdriver.edge.driver", "Resources/msedgedriver.exe");
            } else {
                throw new IllegalArgumentException("OS '" + operatingSystem + "' is not supported!");
            }

            EdgeOptions options = new EdgeOptions();
            if (headless) {
                options.setHeadless(true);
            }
            driver = (WebDriver) new EdgeDriver(options);
        }
        else {
            throw new IllegalArgumentException("Browser '" + browser + "' is not supported!");
        }
        driver.get(URL);
        adjustWindowSize();
    }

    public void adjustWindowSize() {
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(1920, 1080));
    }

    @After
    public void clear() {
        driver.quit();
    }
}
