import dev.failsafe.internal.util.Assert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Cookie;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HomePage {
    WebDriver webDriver;

    public HomePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    private List<WebElement> getAllProductsOnPage(){
        return webDriver.findElements(By.xpath("//*[@id=\"shop-main-navigation\"]//div[@data-zta='product-box-list']"));
    }

    //add click functionality as the name suggests, or change the function to just fetch the values
    public List<List<WebElement>> addFirstVariantOfProductv0(int numberOfProductsToAdd){
        List<WebElement> allProducts = getAllProductsOnPage();
        List<List<WebElement>> addedProducts = new ArrayList<>();
        for (int i = 0; i < numberOfProductsToAdd ; i++){
            List<WebElement> productInfo = new ArrayList<>();
            productInfo.add(allProducts.get(i).findElement(By.xpath(".//*[contains(@data-zta, 'product-link')]")));
            productInfo.add(allProducts.get(i).findElement(By.xpath(".//*[contains(@class, 'ProductListItemVariant-module_variantDescription__36Mpm')]")));
            productInfo.add(allProducts.get(i).findElement(By.xpath(".//*[contains(@class, 'z-price__amount')]")));
            //check why adding cart button
            productInfo.add(allProducts.get(i).findElement(By.xpath(".//*[contains(@title, 'Add to basket')]")));
            addedProducts.add(productInfo);

        }
        return addedProducts;
    }

    public List<List<String>> addFirstVariantOfProductv1(int numberOfProductsToAdd){
        List<WebElement> allProducts = getAllProductsOnPage();
        List<List<String>> addedProducts = new ArrayList<>();
        for (int i = 0; i < numberOfProductsToAdd ; i++){
            List<String> productInfo = new ArrayList<>();
            String productName = allProducts.get(i).findElement(By.xpath(".//*[contains(@data-zta, 'product-link')]")).getText();
            //compensating for the tag New that some products might have
            productInfo.add(productName.endsWith("new") ? productName.substring(0, productName.length() - 3).trim() : productName);
            productInfo.add(allProducts.get(i).findElement(By.xpath(".//*[contains(@class, 'ProductListItemVariant-module_variantDescription__36Mpm')]")).getText());
            productInfo.add(allProducts.get(i).findElement(By.xpath(".//*[contains(@class, 'z-price__amount')]")).getText().substring(1));
            addedProducts.add(productInfo);
            allProducts.get(i).findElement(By.xpath(".//*[contains(@title, 'Add to basket')]")).click();
            waitForAddToCartTick(i+1);

        }
        return addedProducts;
    }
    public WebElement selectProduct(int productNumber, int variationNumber) {
        String basePathProducts = "//*[@id=\"shop-main-navigation\"]/div[6]/div[" + productNumber + "]/div/div/div[3]/div[2]/div[1]/" ;
        return webDriver.findElement(By.xpath(basePathProducts+"div["+ variationNumber + "]/form/div[2]/button"));
    }
    public void clickAgree(){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        try {
            WebElement agreeButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler")));
            agreeButton.click();
        } catch (TimeoutException e) {
            System.out.println("Cookies window not displayed for new session");
        }
    }

    public boolean waitForAddToCartTick(int numberOfProducts){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.jsReturnsValue("return document.evaluate('//*[local-name()=\"svg\" and @data-zta=\"cart-feedback-icon\"]/*[local-name()=\"path\"]', document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength === "+ numberOfProducts));
        return true;
    }

    public void openCart(){
        WebElement cartButton = webDriver.findElement(By.xpath("//*[@id=\"shop-header\"]/div[2]/div[4]/div/a"));
        cartButton.click();
    }

    public int getCartButtonItemCount(){
        String itemCount = webDriver.findElement(By.xpath("//*[@id=\"shop-header\"]//span[@data-testid='MiniCartItemsCount']")).getText();
        return Integer.parseInt(itemCount);
    }
    //change to private
    public Cookie getCookie(String cookieName){
        return webDriver.manage().getCookieNamed(cookieName);
    }

    public void setCookie(String cookieName, String cookieValue){
        if (getCookie(cookieName) == null)
        {
            webDriver.manage().deleteCookie(getCookie(cookieName));
        }

        Cookie cookie = new Cookie(cookieName,cookieValue);
        webDriver.manage().addCookie(cookie);
    }

}
