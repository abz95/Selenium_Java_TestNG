import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.By;
import java.time.Duration;
import java.util.List;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class TestCases
{

    public static WebDriver webDriver = new ChromeDriver();
    HomePage homePage = new HomePage(webDriver);
    CartPage cartPage = new CartPage(webDriver);

    SoftAssert softAssert = new SoftAssert();

    @BeforeTest
    void Setup()
    {
        //System.setProperty("webDriver.chrome.driver",System.getProperty("user.dir") + "/src/test/chromedriver/chromedriver.exe")
        webDriver.manage().window().maximize();
        //webDriver.get("https://www.google.com");
    }

    @Test
    void urlCheck(){
        String url = webDriver.getCurrentUrl();
        WebDriverWait wait = new WebDriverWait(webDriver,Duration.ofSeconds(10));
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement((By.name("q")))));
        searchBar.sendKeys("Abuzar Qureshi");
        WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement((By.name("btnK")))));
        okButton.click();
        WebElement recentsPage = wait.until(ExpectedConditions.visibilityOf(webDriver.findElement((By.id("rcnt")))));
        try {
            WebElement myImage = wait.until(ExpectedConditions.visibilityOf(webDriver.findElement((By.xpath("//*[@id=\"dimg_19\"]")))));
        } catch (NoSuchElementException e) {
            Assert.fail("Image not found");
            // You can choose to log the error, take a screenshot, or perform any other appropriate action here
        }

    }
    @Test
    void ProductAdd(){
        WebDriverWait wait = new WebDriverWait(webDriver,Duration.ofSeconds(10));
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        webDriver.get("https://www.zooplus.com/shop/cats/dry_cat_food");

        homePage.clickAgree();

        WebElement addToCart = homePage.selectProduct(1,1);
        addToCart.click();

        homePage.waitForAddToCartTick(1);

        addToCart = homePage.selectProduct(2,1);
        addToCart.click();

        homePage.waitForAddToCartTick(2);

        addToCart = homePage.selectProduct(3,1);
        addToCart.click();

        Assert.assertTrue(homePage.waitForAddToCartTick(3));

        homePage.openCart();

        WebElement cartFront = wait.until(ExpectedConditions.visibilityOf(webDriver.findElement((By.id("checkout-frontend")))));
        Assert.assertTrue(webDriver.getCurrentUrl().contains("cart"));

        //List<WebElement> productsInCart = webDriver.findElements(By.xpath("//article[@class='Hfp1Ts5oeSm5Qkefdz4a']//div[@data-zta='articleQuantitySubtotal']//span[@data-zta='productStandardPriceAmount']"));
        List<List<String>> productsInCart = cartPage.getAllCartProducts();
        Assert.assertEquals(productsInCart.size(),3);
    }

    @Test
    void ProductsAddv0(){
        WebDriverWait wait = new WebDriverWait(webDriver,Duration.ofSeconds(10));
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        webDriver.get("https://www.zooplus.com/shop/cats/dry_cat_food");

        homePage.clickAgree();

        WebElement addToCart;

        List<List<WebElement>> totalProducts = homePage.addFirstVariantOfProductv0(3);

        for (int i = 0; i < totalProducts.size() ; i++){
            addToCart = totalProducts.get(i).get(3);
            addToCart.click();
            homePage.waitForAddToCartTick(i+1);
            System.out.println(totalProducts.get(i).get(2).getText());
        }

        homePage.openCart();

        WebElement cartFront = wait.until(ExpectedConditions.visibilityOf(webDriver.findElement((By.id("checkout-frontend")))));
        Assert.assertTrue(webDriver.getCurrentUrl().contains("cart"));
    }

    @Test
    void ProductsAddv1(){
        WebDriverWait wait = new WebDriverWait(webDriver,Duration.ofSeconds(10));
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        webDriver.get("https://www.zooplus.com/shop/cats/dry_cat_food");

        homePage.clickAgree();

        List<List<String>> productsAdded = homePage.addFirstVariantOfProductv1(2);

        //fix asserts arguments
        //softAssert.assertEquals(2,homePage.getCartButtonItemCount());

        homePage.openCart();

        WebElement cartFront = wait.until(ExpectedConditions.visibilityOf(webDriver.findElement((By.id("checkout-frontend")))));
        Assert.assertTrue(webDriver.getCurrentUrl().contains("cart"));
        List<List<String>> productsInCart = cartPage.getAllCartProducts();
        Assert.assertEquals(productsAdded,productsInCart);

        Assert.assertEquals(cartPage.getCartProductsTotalPrice(),cartPage.getCartSubTotal());

        //deleting by name, with the highest price
        List<List<String>> descPriceProducts = cartPage.getAllCartProductPriceDesc();
        //cartPage.deleteProductFromCartByName(descPriceProducts.get(0).get(0));

        cartPage.changeShippingCountry("Portugal","5000");

        //cartPage.deleteProductFromCartByPrice(descPriceProducts.get(0).get(2));
        //Assert.assertTrue(cartPage.deleteAlertDisplayed());
        try {
            List<List<String>> recommendationProducts = cartPage.addProductsFromRecommendations(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        descPriceProducts = cartPage.getAllCartProductPriceDesc();
        //cartPage.deleteProductFromCartByName(descPriceProducts.get(0).get(0));
        cartPage.incrementProductQtyByPrice(descPriceProducts.get(descPriceProducts.size()-1).get(2),3);

        Assert.assertEquals(cartPage.getCartSubTotal() + cartPage.getShippingFees(),cartPage.getCartAmountTotal());

        System.out.println(homePage.getCookie("sid"));

        homePage.setCookie("sid","abuzar-qureshi-test");

        System.out.println(homePage.getCookie("sid"));
        softAssert.assertAll();

    }

    @Test
    void CheckEmptyCart()
    {
        webDriver.get("https://www.zooplus.com/checkout/cart");
        homePage.clickAgree();
        Boolean newBool = cartPage.isCartEmpty();
    }

    @AfterTest
    void Teardown(){
        webDriver.quit();
    }
}

