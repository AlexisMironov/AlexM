package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HelpPage {
    private WebDriver driver;
    private Actions actions;

    private By countrySelectorSearchExpr = By.xpath("//div[@class='react-select__indicators css-1wy0on6']");
    private By availableCountriesSearchExpr = By.xpath("//p[@class='css-d8igb0-text-text--mega-text--no-margin e2zwrc919']");
    private By findArticleSearchExpr = By.xpath("//form/input[@id='query']");
    private By searchResultsContainerSearchExpr = By.xpath("//div[@class='search-results-container']");
    private By summaryTextSearchExpr = By.xpath("//p[@class='article-count text--mega']");
    private By searchResultSearchExpr = By.xpath("./section/div");
    private By resultLinkSearchExpr = By.xpath("./a");
    private By resultSubjectSearchExpr = By.xpath("./a/h2");
    private By paginationSearchExpr = By.xpath("//div[@class='pagination']");
    private By resultPageNextSearchExpr = By.xpath("./nav/ul/li/a[@rel='next nofollow'][text()='›']");
    private By homeButtonSearchExpr = By.xpath("//div[@class='header-controls']/a/span[@class='text--kilo']");

    public HelpPage(WebDriver driver) {
        this.driver = driver;
        actions = new Actions(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
    }

    public void moveToElement(WebElement element) {
        actions.moveToElement(element).perform();
    }

    private WebElement locateElementBy(By selector) {
        List<WebElement> wes = driver.findElements(selector);
        if (wes.size() > 0) {
            return wes.get(0);
        }
        else {
            throw new RuntimeException("Element was not found!");
        }
    }

    private WebElement locateElementBy(WebElement container, By selector) {
        List<WebElement> wes = container.findElements(selector);
        if (wes.size() > 0) {
            return wes.get(0);
        }
        else {
            throw new RuntimeException("Element was not found!");
        }
    }

    private boolean waitForElementToAppear(By searchExpression) {
        final int timeoutSeconds = 5;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(searchExpression));
            return true;
        }
        catch(TimeoutException ex){
            System.out.println("Element did not appear!");
            return false;
        }
    }

    private void selectCountryLanguage(String language) {
        for (WebElement option: driver.findElements(availableCountriesSearchExpr)) {
            String lang = option.getText();
            if (language.equals(lang)) {
                option.click();
                return;
            }
        }
        throw new RuntimeException("Language '" + language + "' was not selected!");
    }

    public void selectCountry(String country) {
        waitForElementToAppear(countrySelectorSearchExpr);
        WebElement countrySelector = locateElementBy(countrySelectorSearchExpr);
        moveToElement(countrySelector);
        countrySelector.click();
        selectCountryLanguage(country);
    }

    public void searchTerm(String term) {
        waitForElementToAppear(findArticleSearchExpr);
        WebElement findArticle = locateElementBy(findArticleSearchExpr);
        moveToElement(findArticle);
        findArticle.sendKeys(term);
        findArticle.sendKeys(Keys.ENTER);
    }

    private boolean moveToNextButton(WebElement container, By searchExpr) {
        List<WebElement> nextButton = container.findElements(searchExpr);
        if (nextButton.size() > 0) {
            nextButton.get(0).click();
            return true;
        }
        else {
            return false;
        }
    }

    public SearchResult enumerateResults() {
        waitForElementToAppear(searchResultsContainerSearchExpr);
        SearchResult lastSearchResult = new SearchResult();
        lastSearchResult.numberOfResults = 0;
        for (int pageIndex = 1; pageIndex <= 100; pageIndex++) {
            WebElement resultsContainer = locateElementBy(searchResultsContainerSearchExpr);
            if (pageIndex == 1) {
                WebElement summaryText = locateElementBy(resultsContainer, summaryTextSearchExpr);
                System.out.println("Summary: " + summaryText.getText());
            }
            for (WebElement result: resultsContainer.findElements(searchResultSearchExpr)) {
                WebElement resultSubject = result.findElement(resultSubjectSearchExpr);
                moveToElement(resultSubject);
                lastSearchResult.title = resultSubject.getText();
                lastSearchResult.link = result.findElement(resultLinkSearchExpr).getAttribute("href");
                lastSearchResult.numberOfResults++;
                System.out.println(lastSearchResult.numberOfResults + ". " + lastSearchResult.title);
            }
            WebElement pagination = locateElementBy(paginationSearchExpr);
            if (!moveToNextButton(pagination, resultPageNextSearchExpr)) {
                break;
            }
        }
        return lastSearchResult;
    }

    public String verifyResult(SearchResult result) {
        WebElement we = locateElementBy(By.xpath("//a[contains('" + result.link + "', @href)]/h2"));
        we.click();
        WebElement title = locateElementBy(By.xpath("//header/h1[@title='" + result.title + "']"));
        return title.getText();
    }

    public String goToHome() {
        String currentWindowHandle = driver.getWindowHandle();
        locateElementBy(homeButtonSearchExpr).click();
        for (String wndHandle: driver.getWindowHandles()) {
            if (!wndHandle.equals(currentWindowHandle)) {
                return driver.switchTo().window(wndHandle).getCurrentUrl();
            }
        }
        return driver.getCurrentUrl();
    }
}
