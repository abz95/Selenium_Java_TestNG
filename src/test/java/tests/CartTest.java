package tests;

import model.Product;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.CartPage;
import pages.HomePage;


public class CartTest
{
    private WebDriver webDriver;
    private HomePage homePage;
    private CartPage cartPage;
    private SoftAssert softAssert;
    private String startingUrl;

    @BeforeMethod
    void Setup()
    {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        webDriver = new ChromeDriver();
        homePage = new HomePage(webDriver);
        cartPage = new CartPage(webDriver);
        softAssert = new SoftAssert();
        startingUrl = "https://www.zooplus.com/shop/cats/dry_cat_food";
        webDriver.manage().window().maximize();
    }

    @Test(description = "1. This test checks that the cart is displayed as empty when we open it without adding any products")
    public void emptyCartTest(){
        webDriver.get(startingUrl);
        homePage.clickAgree();
        homePage.openCart();
        Assert.assertTrue(webDriver.getCurrentUrl().contains("/cart"));
        Assert.assertTrue(cartPage.isCartEmpty());
    }

    @Test(description = "2. This test validates that Shopping Basket Count is correctly displayed when adding products")
    public void basketCountTest(){
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 4;
        homePage.addFirstVariantOfProduct(productCount);
        Assert.assertEquals(homePage.getCartButtonItemCount(),productCount);
    }

    @Test(description = "3. This test validates that Shipping country is changed successfully and relevant fees is applied")
    public void shippingMethodTest(){
        String country = "Portugal";
        String zipCode = "5000";
        double expectedFees = 6.99;
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 2;
        homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();
        cartPage.changeShippingCountry(country, zipCode);
        Assert.assertEquals(cartPage.getShippingFees(),expectedFees);
    }

    @Test(description = "4. This test validates that all products added from Products page are present in the cart")
    public void productAddedTest(){
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 4;
        List<Product> productsAdded = homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();
        List<Product> cartProducts = cartPage.getAllCartProducts();
        Assert.assertEquals(productsAdded.size(),cartProducts.size());
        Assert.assertTrue(cartProducts.equals(productsAdded));
    }

    @Test(description = "5. This test validates that all added products total price is correctly reflected in SubTotal Amount")
    public void productPriceSubTotalTest(){
        double subTotalDisplayed, subTotalCalculatedByProducts;
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 4;
        List<Product> productsAdded = homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();
        subTotalCalculatedByProducts = cartPage.getCartProductsTotalPrice();
        subTotalDisplayed = cartPage.getCartSubTotal();
        Assert.assertEquals(subTotalCalculatedByProducts, subTotalDisplayed);
    }

    @Test(description = "6. This test validates that Sub-Total + Shipping Fees is equals to Total Cart Price displayed")
    public void productPriceTotalTest(){
        double subTotalDisplayed, shippingFeesDisplayed, totalPrice;
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 2;
        List<Product> productsAdded = homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();
        shippingFeesDisplayed = cartPage.getShippingFees();
        subTotalDisplayed = cartPage.getCartSubTotal();
        totalPrice = cartPage.getCartAmountTotal();
        Assert.assertEquals(shippingFeesDisplayed + subTotalDisplayed, totalPrice);
    }

    @Test(description = "7. This test validates that product can be deleted/removed from cart (Highest Price)")
    public void deleteFromCartTest() throws InterruptedException {
        double highestPrice, totalPriceBefore, totalPriceAfter, cartCountBefore, cartCountAfter;
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 4;
        List<Product> productsAdded = homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();
        List<Product> cartProducts = cartPage.getAllCartProductPriceDesc();
        totalPriceBefore = cartPage.getCartAmountTotal();
        cartCountBefore = cartProducts.size();
        highestPrice = cartProducts.get(0).getPrice();
        int productsToBeDeleted = Product.countProductsByPrice(cartProducts, highestPrice);
        Assert.assertTrue(cartPage.deleteProductFromCartByPrice(highestPrice));
        Assert.assertTrue(cartPage.deleteAlertDisplayed());
        TimeUnit.SECONDS.sleep(3);
        cartProducts = cartPage.getAllCartProductPriceDesc();
        totalPriceAfter = cartPage.getCartAmountTotal();
        cartCountAfter = cartProducts.size();
        BigDecimal expectedTotal = new BigDecimal(totalPriceBefore - (highestPrice * productsToBeDeleted));
        expectedTotal = expectedTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        double expectedTotalRounded = expectedTotal.doubleValue();

        Assert.assertEquals(totalPriceAfter, expectedTotalRounded);
        Assert.assertEquals(cartCountAfter, cartCountBefore - productsToBeDeleted);
    }

    @Test(description = "8. This test validates that product can be incremented in cart (Lowest Price)")
    public void incrementFromCartTest() throws InterruptedException {
        double totalPriceBefore, totalPriceAfter, cartCountBefore, cartCountAfter, lowestPrice;
        int qtyToIncrease = 1;
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 4;
        List<Product> productsAdded = homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();
        List<Product> cartProducts = cartPage.getAllCartProductPriceDesc();
        totalPriceBefore = cartPage.getCartAmountTotal();
        cartCountBefore = cartProducts.size();
        lowestPrice = cartProducts.get(cartProducts.size()-1).getPrice();
        int productsToBeIncremented = Product.countProductsByPrice(cartProducts, lowestPrice);
        Assert.assertTrue(cartPage.incrementProductQtyByPrice(lowestPrice, qtyToIncrease));
        totalPriceAfter = cartPage.getCartAmountTotal();
        BigDecimal expectedTotal = new BigDecimal(totalPriceBefore + (productsToBeIncremented*(lowestPrice*qtyToIncrease)));
        expectedTotal = expectedTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        double expectedTotalRounded = expectedTotal.doubleValue();
        Assert.assertEquals(totalPriceAfter, expectedTotalRounded);
    }

    @Test(description = "9. This test validates that Order Value excluding shipping (Sub-Total) has to be greater than 19 to checkout")
    public void minimumPriceNotMetTest(){
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 1;
        List<Product> productsAdded = homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();

        if(productsAdded.get(0).getPrice()>18){
            Assert.fail("Product Added has met the minimum price");
        }
        Assert.assertTrue(cartPage.mimimumAmountNotMet());
    }

    @Test(description = "10. This test validates that deleting all products in cart displays empty cart")
    public void deleteAllProductsTest(){
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 1;
        List<Product> productsAdded = homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();
        List<Product> productsCart = cartPage.getAllCartProducts();
        Assert.assertTrue(cartPage.deleteProductFromCartByPrice(productsCart.get(0).getPrice()));
        Assert.assertTrue(cartPage.isCartEmpty());
    }

    @Test(description = "11. This test validates that we can add more products from Cart's recommendation section")
    public void addProductsFromRecommendationTest() throws InterruptedException {
        double subTotalDisplayed, subTotalCalculatedByProducts;
        int productsToAddRecommendation = 3;
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 1;
        List<Product> productsAdded = homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();
        List<Product> recommendationProducts = cartPage.addProductsFromRecommendations(productsToAddRecommendation);
        List<Product> allProductsAdded = new ArrayList<>();
        allProductsAdded.addAll(productsAdded);
        allProductsAdded.addAll(recommendationProducts);
        List<Product> productsInCart = cartPage.getAllCartProducts();
        subTotalCalculatedByProducts = cartPage.getCartProductsTotalPrice();
        subTotalDisplayed = cartPage.getCartSubTotal();
        Assert.assertTrue(Product.compareProductListsWithoutVariant(allProductsAdded,productsInCart));
        Assert.assertEquals(subTotalCalculatedByProducts, subTotalDisplayed);
    }

    @Test(description = "12. This test validates that with 'sid' cookie, we have out cart maintained")
    public void cartSessionTest() throws InterruptedException {
        double priceTotalBefore, priceTotalAfter;
        webDriver.get(startingUrl);
        homePage.setCookie("sid","abuzar-qureshi-test");
        homePage.clickAgree();
        //webDriver.navigate().refresh();
        int productCount = 5;
        List<Product> productsAdded = homePage.addFirstVariantOfProduct(productCount);
        homePage.openCart();
        List<Product> productsCartBefore = cartPage.getAllCartProducts();
        priceTotalBefore = cartPage.getCartAmountTotal();
        webDriver.quit();

        WebDriver newWebDriver = new ChromeDriver();
        newWebDriver.manage().window().maximize();
        HomePage homePage = new HomePage(newWebDriver);
        CartPage cartPage = new CartPage(newWebDriver);
        newWebDriver.get(startingUrl);
        homePage.clickAgree();
        homePage.setCookie("sid","abuzar-qureshi-test");
        //newWebDriver.navigate().refresh();
        homePage.openCart();
        List<Product> productsCartAfter = cartPage.getAllCartProducts();
        priceTotalAfter = cartPage.getCartAmountTotal();

        Assert.assertTrue(productsCartAfter.equals(productsCartBefore));
        Assert.assertEquals(priceTotalAfter, priceTotalBefore);
        newWebDriver.quit();
    }

    @Test(description = "13. This test validates that shipping becomes free on orders above certain value")
    public void freeShippingTest(){
        double totalPrice = 0;
        String country = "Belgium";
        String zipCode = "1731";
        webDriver.get(startingUrl);
        homePage.clickAgree();
        int productCount = 1;
        do {
            Product productAdded = homePage.addFirstVariantOfProductNumber(productCount);
            totalPrice += productAdded.getPrice();
            productCount++;
        }
        while (!(totalPrice > 49));

        homePage.openCart();
        cartPage.changeShippingCountry(country,zipCode);
        Assert.assertEquals(cartPage.getShippingFees(),0);
    }


    @AfterMethod
    void Teardown(){
        if (webDriver != null) {
            try {
                webDriver.quit();
            } catch (Exception e) {

            }
        }
    }
}

