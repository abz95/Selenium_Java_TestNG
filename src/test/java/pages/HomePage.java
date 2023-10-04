package pages;

import model.Product;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HomePage {
    private final WebDriver webDriver;
    private CartPage cartPage;
    private final By productBoxLocator = By.xpath("//*[@id=\"shop-main-navigation\"]//div[@data-zta='product-box-list']");
    private final By agreeButtonLocator = By.id("onetrust-accept-btn-handler");
    private final By productNameLocator = By.xpath(".//*[contains(@data-zta, 'product-link')]");
    private final By productVariantLocator = By.xpath(".//*[contains(@class, 'ProductListItemVariant-module_variantDescription__36Mpm')]");
    private final By productPriceLocator = By.xpath(".//*[contains(@class, 'z-price__amount')]");
    private final By productAddToBasketLocator = By.xpath(".//*[contains(@title, 'Add to basket')]");
    private final By cartButtonLocator = By.xpath("//*[@id=\"shop-header\"]/div[2]/div[4]/div/a");
    private final By cartItemCountLocator = By.xpath("//*[@id=\"shop-header\"]//span[@data-testid='MiniCartItemsCount']");
    private final By cartFooterLocator = By.xpath("//*[@id=\"checkout-frontend\"]/div/footer");

    public HomePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    private List<WebElement> getAllProductsOnPage(){
        return webDriver.findElements(productBoxLocator);
    }

    public List<Product> addFirstVariantOfProduct(int numberOfProductsToAdd){
        List<WebElement> allProducts = getAllProductsOnPage();
        List<Product> addedProducts = new ArrayList<>();
        for (int i = 0; i < numberOfProductsToAdd ; i++){
            String productName = allProducts.get(i).findElement(productNameLocator).getText();
            //compensating for the tag New that some products might have
            productName = productName.endsWith("new") ? productName.substring(0, productName.length() - 3).trim() : productName;
            String productVariant = allProducts.get(i).findElement(productVariantLocator).getText();
            Double productPrice = Double.parseDouble(allProducts.get(i).findElement(productPriceLocator).getText().substring(1));
            Product productInfo = new Product(productName, productVariant, productPrice);
            addedProducts.add(productInfo);
            allProducts.get(i).findElement(productAddToBasketLocator).click();
            waitForAddToCartTick(i+1);

        }
        return addedProducts;
    }

    public Product addFirstVariantOfProductNumber(int productNumber){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        List<WebElement> allProducts = getAllProductsOnPage();
        productNumber++;
        String productName = allProducts.get(productNumber).findElement(productNameLocator).getText();
        //compensating for the tag New that some products might have
        productName = productName.endsWith("new") ? productName.substring(0, productName.length() - 3).trim() : productName;
        String productVariant = allProducts.get(productNumber).findElement(productVariantLocator).getText();
        Double productPrice = Double.parseDouble(allProducts.get(productNumber).findElement(productPriceLocator).getText().substring(1));
        Product productInfo = new Product(productName, productVariant, productPrice);
        allProducts.get(productNumber).findElement(productAddToBasketLocator).click();
        wait.until(ExpectedConditions.elementToBeClickable(productAddToBasketLocator));

        return productInfo;
    }

    public void clickAgree(){
        WebElement agreeButton = clickablityOf(agreeButtonLocator);
        agreeButton.click();
    }

    private boolean waitForAddToCartTick(int numberOfProducts){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.jsReturnsValue("return document.evaluate('//*[local-name()=\"svg\" and @data-zta=\"cart-feedback-icon\"]/*[local-name()=\"path\"]', document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength === "+ numberOfProducts));
        return true;
    }

    public void openCart(){
        WebElement cartButton = visiblityOf(cartButtonLocator);
        cartButton.click();
        //waiting for cart page to load up
        visiblityOf(cartFooterLocator);
    }

    public int getCartButtonItemCount(){
        String itemCount = visiblityOf(cartItemCountLocator).getText();
        return Integer.parseInt(itemCount);
    }

    public Cookie getCookie(String cookieName){
        return webDriver.manage().getCookieNamed(cookieName);
    }

    public void setCookie(String cookieName, String cookieValue){
        if (getCookie(cookieName) != null)
        {
            webDriver.manage().deleteCookie(getCookie(cookieName));
        }
        Cookie cookie = new Cookie(cookieName,cookieValue);
        webDriver.manage().addCookie(cookie);
    }

    private WebElement clickablityOf(By element){
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        }
        catch (TimeoutException | NoSuchElementException e){
            System.out.println("Element wasn't clickable or visible on the page");
            throw (e);
        }
    }

    private WebElement visiblityOf(By element){
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(element));
        }
        catch (TimeoutException | NoSuchElementException e){
            System.out.println("Element wasn't visible on the page");
            throw (e);
        }
    }

}
