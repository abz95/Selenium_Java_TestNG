package tests;

import model.Product;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.By;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.HomePage;


public class CartTest
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
    void productClass(){
        WebDriverWait wait = new WebDriverWait(webDriver,Duration.ofSeconds(10));
        webDriver.get("https://www.zooplus.com/shop/cats/dry_cat_food");

        homePage.clickAgree();

        List<Product> productsAdded = homePage.addFirstVariantOfProductv1(3);
        String url = webDriver.getCurrentUrl();
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
    void ProductsAddv1(){
        WebDriverWait wait = new WebDriverWait(webDriver,Duration.ofSeconds(10));
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        webDriver.get("https://www.zooplus.com/shop/cats/dry_cat_food");

        homePage.clickAgree();

        List<Product> productsAdded = homePage.addFirstVariantOfProductv1(2);

        //fix asserts arguments
        //softAssert.assertEquals(2,homePage.getCartButtonItemCount());

        homePage.openCart();

        WebElement cartFront = wait.until(ExpectedConditions.visibilityOf(webDriver.findElement((By.id("checkout-frontend")))));
        Assert.assertTrue(webDriver.getCurrentUrl().contains("cart"));
        List<Product> productsInCart = cartPage.getAllCartProducts();
        //Assert.assertEquals(productsAdded,productsInCart);
        Assert.assertTrue(productsAdded.equals(productsInCart));

        Assert.assertEquals(cartPage.getCartProductsTotalPrice(),cartPage.getCartSubTotal());

        //deleting by name, with the highest price
        List<Product> descPriceProducts = cartPage.getAllCartProductPriceDesc();
        //cartPage.deleteProductFromCartByName(descPriceProducts.get(0).getPrice());

        cartPage.changeShippingCountry("Portugal","5000");

        cartPage.deleteProductFromCartByPrice(descPriceProducts.get(0).getPrice());
        Assert.assertTrue(cartPage.deleteAlertDisplayed());
        List<Product> recommendationProducts = new ArrayList<>();
        try {
            recommendationProducts = cartPage.addProductsFromRecommendations(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



        /*List<Products> allProductsAdded = new ArrayList<>();
        allProductsAdded.addAll(productsAdded);
        allProductsAdded.addAll(recommendationProducts);
        productsInCart = cartPage.getAllCartProducts();
        Boolean compare = allProductsAdded.equalsWithoutVariant(productsInCart);*/



        descPriceProducts = cartPage.getAllCartProductPriceDesc();
        //cartPage.deleteProductFromCartByName(descPriceProducts.get(0).get(0));
        cartPage.incrementProductQtyByPrice(descPriceProducts.get(descPriceProducts.size()-1).getPrice(),3);

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

