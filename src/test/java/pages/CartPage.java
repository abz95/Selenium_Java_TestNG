package pages;

import model.Product;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.math.BigDecimal;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CartPage {
    private final WebDriver webDriver;
    private final By cartProductsLocator = By.xpath("//*[@id='checkout-frontend']//article[@data-zta='standard_article']");
    private final By cartEmptyTextLocator = By.xpath("//*[@id='checkout-frontend']//h1[@data-zta='H1UIC']");
    private final By productNameLocator = By.xpath(".//*[contains(@data-zta, 'productName')]");
    private final By productVariantLocator = By.xpath(".//*[contains(@data-zta, 'productVariant')]");
    private final By productPriceLocator = By.xpath(".//*[contains(@data-zta, 'productStandardPriceAmount')]");
    private final By cartSubTotalLocator = By.xpath("//*[@id=\"cartSummary\"]//p[@data-zta='overviewSubTotalValue']");
    private final By cartShippingFeesLocator = By.xpath("//*[@id=\"cartSummary\"]//p[@data-zta='shippingCostValueOverview']");
    private final By cartTotalAmountLocator = By.xpath("//*[@id=\"cartSummary\"]//h3[@data-zta='total__price__value']");
    private final By deleteAlertLocator = By.xpath("//*[@id=\"checkout-frontend\"]//div[@data-zta='reAddArticleAlert']");
    private final By recommendationsLocator = By.xpath("//*[@id='checkout-frontend']//div[contains(@class, 'recommendations-slider-module_wrapper__gSjnL')]");
    private final By recommendedProductLocator = By.xpath(".//*[contains(@class, 'splide__slide') and not(contains(@aria-hidden, 'true'))]");
    private final By recommendedProductNameLocator = By.xpath(".//*[contains(@data-zta, 'P1UIC')]");
    private final By recommendedProductPriceLocator = By.xpath(".//*[contains(@class, 'z-price__amount')]");
    private final By recommendedProductAddButtonLocator = By.xpath(".//*[contains(@class, 'z-btn')]");
    private final By recommendedProductsNextButtonLocator = By.xpath(".//*[contains(@class, 'splide__arrow--next')]");
    private final By shippingCountryLocator = By.xpath("//*[@id='cartSummary']//a[@data-zta='shippingCountryName']");
    private final By shippingDropDownLocator = By.xpath("//*[@data-zta='dropdownMenuProxySelect']");
    private final By shippingDropDownMenuLocator = By.xpath("//*[@data-zta='dropdownMenuMenu']");
    private final By shippingCostZipcodeLocator = By.xpath("//*[@data-zta='shippingCostZipcode']/input");
    private final By shippingUpdateButtonLocator = By.xpath("//*[@data-zta='shippingCostPopoverAction']");
    private final By productQtyInputLocator = By.xpath(".//*[contains(@data-zta, 'quantityStepperInput')]");
    private final By productQtyDropdownLocator = By.xpath(".//*[contains(@data-zta, 'quantityPickerSelect')]");
    private final By productChangeAnimatorLocator = By.xpath("//*[@id='checkout-frontend']//div[@class='beMKS6MTj3RKepGT1hvS']");


    public CartPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    //make private maybe later
    public List<WebElement> cartProducts(){
        return webDriver.findElements(cartProductsLocator);
    }

    public boolean isCartEmpty(){
        WebElement cartEmptyText = visiblityOf(cartEmptyTextLocator);
        return cartEmptyText.getText().equals("Your shopping basket is empty");
    }

    public List<Product> getAllCartProducts(){
        List<WebElement> cartProducts = cartProducts();
        List<Product> addedProducts = new ArrayList<>();
        for (WebElement cartProduct : cartProducts) {
            String productName = cartProduct.findElement(productNameLocator).getText();
            String productVariant = cartProduct.findElement(productVariantLocator).getText().substring(2);
            Double productPrice = Double.parseDouble(cartProduct.findElement(productPriceLocator).getText().substring(1));
            Product productInfo = new Product(productName, productVariant, productPrice);
            addedProducts.add(productInfo);
        }
        return addedProducts;
    }

    public List<Product> getAllCartProductPriceDesc(){
        List<Product> allCartProducts = getAllCartProducts();
        allCartProducts.sort(Comparator.comparing(Product::getPrice, Comparator.reverseOrder()));
        return allCartProducts;
    }

    public double getCartProductsTotalPrice(){
        BigDecimal productTotal = new BigDecimal(0);
        List<Product> products = getAllCartProductPriceDesc();

        for (Product product : products) {
            productTotal = productTotal.add(BigDecimal.valueOf(product.getPrice()));
        }
        productTotal = productTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return productTotal.doubleValue();
    }


    public double getCartSubTotal(){
        String subTotal = visiblityOf(cartSubTotalLocator).getText();
        return Double.parseDouble(subTotal.substring(1));
    }

    public double getShippingFees(){
        String subTotal = visiblityOf(cartShippingFeesLocator).getText();
        return subTotal.equals("Free") ? 0 : Double.parseDouble(subTotal.substring(1));
    }

    public double getCartAmountTotal(){
        String totalAmount = visiblityOf(cartTotalAmountLocator).getText();
        return Double.parseDouble(totalAmount.substring(1));
    }
    public void deleteProductFromCartByName(String productName) {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(3));
        WebElement lineItem;
        try {
            lineItem = webDriver.findElement(By.xpath("//*[contains(text(), '" + productName + "')]"));
            lineItem = lineItem.findElement(By.xpath("ancestor::article[@data-zta='standard_article']"));
        }
        catch (NoSuchElementException e){
            System.out.println("No matching product found with the given name");
            throw (e);
        }

        try {
            WebElement qtyDropdown = lineItem.findElement(productQtyDropdownLocator);
            Select dropdown = new Select(qtyDropdown);
            dropdown.selectByVisibleText("0");
        } catch (Exception e1) {
            // qtyDropdown is not present, finding qtyMinusButton
            try {
                WebElement qtyTextField = lineItem.findElement(productQtyInputLocator);
                qtyTextField.sendKeys(Keys.BACK_SPACE);
            } catch (Exception e2) {
                System.out.println("Deleting the Product from cart is not possible");
                throw (e2);
            }

        }
    }

    public boolean deleteProductFromCartByPrice(Double priceValue){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(3));
        String price = priceValue.toString();
        price = "€" + price;
        List<WebElement> matchingProducts = webDriver.findElements(By.xpath("//div[@data-zta='articleQuantitySubtotal']//*[contains(text(), '" + price + "')]"));
        if (matchingProducts.isEmpty()) {
            return false;
        }
        for (WebElement matchingProduct : matchingProducts) {
            WebElement lineItem = matchingProduct.findElement(By.xpath("ancestor::article[@data-zta='standard_article']"));

            try {
                WebElement qtyDropdown = lineItem.findElement(productQtyDropdownLocator);
                Select dropdown = new Select(qtyDropdown);
                dropdown.selectByVisibleText("0");
            } catch (Exception e1) {
                // qtyDropdown is not present, finding qtyMinusButton
                try {
                    WebElement qtyTextField = lineItem.findElement(productQtyInputLocator);
                    qtyTextField.sendKeys(Keys.BACK_SPACE);
                } catch (Exception e2) {
                    System.out.println("Deleting the Product from cart is not possible");
                    throw (e2);
                }

            }
        }
    return true;
    }

    public boolean deleteAlertDisplayed(){
        WebElement deleteAlert = visiblityOf(deleteAlertLocator);
        return deleteAlert != null;
    }

    public List<Product> addProductsFromRecommendations(int numberOfProductsToAdd) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        visiblityOf(recommendationsLocator);
        List<WebElement> sliders = webDriver.findElements(recommendationsLocator);
        WebElement recommendationSlider = sliders.get(1);
        List<Product> addedProducts = new ArrayList<>();
        for (int i = 0; i < numberOfProductsToAdd ; i++){
            WebElement recommendedProduct = recommendationSlider.findElement(recommendedProductLocator);
            String productName = recommendedProduct.findElement(recommendedProductNameLocator).getText();
            //compensating for the tag New that some products might have
            productName = productName.endsWith("new") ? productName.substring(0, productName.length() - 3).trim() : productName;
            //Variant is not displayed, adding "Unknown as the variant"
            String productVariant = "Unknown";
            String productPrice = recommendedProduct.findElement(recommendedProductPriceLocator).getText();
            productPrice = productPrice.startsWith("Now ") ? productPrice.substring(5) : productPrice.substring(1);
            Double productPriceValue = Double.parseDouble(productPrice);
            Product productInfo = new Product(productName, productVariant, productPriceValue);
            addedProducts.add(productInfo);

            recommendedProduct.findElement(recommendedProductAddButtonLocator).click();
            //checking if the product is added

            Integer cartSize = cartProducts().size();
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(cartProductsLocator, cartSize));

            recommendationSlider.findElement(recommendedProductsNextButtonLocator).click();
            TimeUnit.SECONDS.sleep(2);
        }
        return addedProducts;
    }

    public boolean incrementProductQtyByPrice(Double priceValue, Integer qtyToIncrease){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(3));
        String price = priceValue.toString();
        price = "€" + price;
        List<WebElement> matchingProducts = webDriver.findElements(By.xpath("//div[@data-zta='articleQuantitySubtotal']//*[contains(text(), '" + price + "')]"));

        if (matchingProducts.isEmpty()) {
            return false;
        }

        for (WebElement matchingProduct : matchingProducts) {
            WebElement lineItem = matchingProduct.findElement(By.xpath("ancestor::article[@data-zta='standard_article']"));

            try {
                WebElement qtyDropdown = lineItem.findElement(productQtyDropdownLocator);
                Select dropdown = new Select(qtyDropdown);
                Integer currentQty = Integer.parseInt(dropdown.getFirstSelectedOption().getText());
                Integer newQty = currentQty + qtyToIncrease;
                dropdown.selectByVisibleText(newQty.toString());
                wait.until(ExpectedConditions.numberOfElementsToBe(productChangeAnimatorLocator,0));
            } catch (Exception e1) {
                // qtyDropdown is not present, finding qtyPlusButton
                try {
                    WebElement qtyTextField = lineItem.findElement(productQtyInputLocator);
                    Integer currentQty = Integer.parseInt(qtyTextField.getAttribute("value"));
                    Integer newQty = currentQty + qtyToIncrease;
                    qtyTextField.sendKeys(Keys.CONTROL + "a"); //not factoring in MacOS
                    qtyTextField.sendKeys(newQty.toString());
                    wait.until(ExpectedConditions.numberOfElementsToBe(productChangeAnimatorLocator,0));

                } catch (Exception e2) {
                    System.out.println("Incrementing the Product Qty from cart is not possible");
                    throw (e2);
                }

            }
        }
        return true;
    }

    public String changeShippingCountry(String countryToBeSelected, String zipCode){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        WebElement shippingCountry = visiblityOf(shippingCountryLocator);
        shippingCountry.click();
        WebElement shippingDropDown = visiblityOf(shippingDropDownLocator);
        shippingDropDown.click();
        visiblityOf(shippingDropDownMenuLocator);
        WebElement matchingCountry = webDriver.findElement(By.xpath("//*[@id='dd-menu-BreakpointAll']//*[contains(text(), '" + countryToBeSelected + "')]"));
        matchingCountry.click();
        WebElement zipCodeField = visiblityOf(shippingCostZipcodeLocator);
        zipCodeField.sendKeys(zipCode);
        WebElement updateButton = visiblityOf(shippingUpdateButtonLocator);
        updateButton.click();
        wait.until(ExpectedConditions.textToBePresentInElement(shippingCountry,countryToBeSelected + " (" + zipCode + ")"));

        return shippingCountry.getText();
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
            return null;
        }
    }

}
