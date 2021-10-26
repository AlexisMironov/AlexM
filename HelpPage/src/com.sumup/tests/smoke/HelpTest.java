package smoke;

import org.junit.Assert;
import org.junit.Test;
import pages.*;

public class HelpTest extends BaseTest {
    @Test
    public void searchTerm()
    {
        HelpPage helpPage = new HelpPage(driver);
        helpPage.selectCountry("United States");
        helpPage.searchTerm("invoice"); // "касов ордер"
        SearchResult lastResult = helpPage.enumerateResults();
        Assert.assertEquals(23, lastResult.numberOfResults);
        String titleText = helpPage.verifyResult(lastResult);
        Assert.assertEquals(lastResult.title, titleText);
        Assert.assertEquals("https://sumup.com/", helpPage.goToHome());
    }
}
