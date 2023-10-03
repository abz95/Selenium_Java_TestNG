import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Collections;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CartPage {
    WebDriver webDriver;

    public CartPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    //make private maybe later
    public List<WebElement> cartProducts(){
        return webDriver.findElements(By.xpath("//*[@id='checkout-frontend']//article[@data-zta='standard_article']"));
    }

    public boolean isCartEmpty(){
        try
        {
            WebElement cartEmptyText = webDriver.findElement(By.xpath("//*[@id='checkout-frontend']//h1[@data-zta='H1UIC']"));
            return cartEmptyText.getText().equals("Your shopping basket is empty");
        }
        catch (Exception e)
        {
            System.out.println("Some errors occured while checking if cart is empty");
            return false;
        }
    }
    public List<List<String>> getAllCartProducts(){
        List<WebElement> cartProducts = cartProducts();
        List<List<String>> addedProducts = new ArrayList<>();
        for (int i = 0; i < cartProducts.size() ; i++){
            List<String> productInfo = new ArrayList<>();
            productInfo.add(cartProducts.get(i).findElement(By.xpath(".//*[contains(@data-zta, 'productName')]")).getText());
            productInfo.add(cartProducts.get(i).findElement(By.xpath(".//*[contains(@data-zta, 'productVariant')]")).getText().substring(2));
            productInfo.add(cartProducts.get(i).findElement(By.xpath(".//*[contains(@data-zta, 'productStandardPriceAmount')]")).getText().substring(1));
            addedProducts.add(productInfo);
        }
        return addedProducts;
    }

    public List<List<String>> getAllCartProductPriceDesc(){
        List<List<String>> allCartProducts = getAllCartProducts();
        allCartProducts.sort(Comparator.comparing(list -> Double.parseDouble(list.get(2)), Comparator.reverseOrder()));
        return allCartProducts;
    }

    public double getCartProductsTotalPrice(){
        BigDecimal productTotal = new BigDecimal(0);
        List<List<String>> products = getAllCartProductPriceDesc();

        for (int i = 0 ; i < products.size() ; i++) {
            productTotal = productTotal.add(new BigDecimal(products.get(i).get(2)));
        }
        productTotal = productTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return productTotal.doubleValue();
    }


    public double getCartSubTotal(){
        String subTotal = webDriver.findElement(By.xpath("//*[@id=\"cartSummary\"]//p[@data-zta='overviewSubTotalValue']")).getText();
        return Double.parseDouble(subTotal.substring(1));
    }

    public double getShippingFees(){
        String subTotal = webDriver.findElement(By.xpath("//*[@id=\"cartSummary\"]//p[@data-zta='shippingCostValueOverview']")).getText();
        return subTotal.equals("Free") ? 0 : Double.parseDouble(subTotal.substring(1));
    }

    public double getCartAmountTotal(){
        String subTotal = webDriver.findElement(By.xpath("//*[@id=\"cartSummary\"]//h3[@data-zta='total__price__value']")).getText();
        return Double.parseDouble(subTotal.substring(1));
    }
    public void deleteProductFromCartByName(String productName) {

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(1));
        WebElement lineItem = webDriver.findElement(By.xpath("//*[contains(text(), '" + productName + "')]"));
        lineItem = lineItem.findElement(By.xpath("ancestor::article[@data-zta='standard_article']"));
        //WebElement qtyDropdown = lineItem.findElement(By.xpath(".//*[contains(@data-zta, 'quantityPickerSelect')]"));


        //WebElement qtyMinusButton = lineItem.findElement(By.xpath(".//*[contains(@data-zta, 'quantityStepperDecrementButton')]"));

        try {
            WebElement qtyDropdown = lineItem.findElement(By.xpath(".//*[contains(@data-zta, 'quantityPickerSelect')]"));
            Select dropdown = new Select(qtyDropdown);
            dropdown.selectByVisibleText("0");
        } catch (Exception e1) {
            // qtyDropdown is not present, finding qtyMinusButton
            try {
                WebElement qtyMinusButton = lineItem.findElement(By.xpath(".//*[contains(@data-zta, 'quantityStepperDecrementButton')]"));
                qtyMinusButton.click();
            } catch (Exception e2) {
                System.out.println("Deleting the Product from cart is not possible");
            }

        }
    }

    public boolean deleteProductFromCartByPrice(String price){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(1));
        price = "€" + price;


            List<WebElement> matchingProducts = webDriver.findElements(By.xpath("//div[@data-zta='articleQuantitySubtotal']//*[contains(text(), '" + price + "')]"));
            if (matchingProducts.isEmpty()) {
                return false;
            }

            for (WebElement matchingProduct : matchingProducts) {
                WebElement lineItem = matchingProduct.findElement(By.xpath("ancestor::article[@data-zta='standard_article']"));

                try {
                    WebElement qtyDropdown = lineItem.findElement(By.xpath(".//*[contains(@data-zta, 'quantityPickerSelect')]"));
                    Select dropdown = new Select(qtyDropdown);
                    dropdown.selectByVisibleText("0");
                } catch (Exception e1) {
                    // qtyDropdown is not present, finding qtyMinusButton
                    try {
                        WebElement qtyMinusButton = lineItem.findElement(By.xpath(".//*[contains(@data-zta, 'quantityStepperDecrementButton')]"));
                        qtyMinusButton.click();
                    } catch (Exception e2) {
                        System.out.println("Deleting the Product from cart is not possible");
                        return false;
                    }

                }
            }
        return true;
    }

    public boolean deleteAlertDisplayed(){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        try {
            WebElement deleteAlert = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"checkout-frontend\"]//div[@data-zta='reAddArticleAlert']")));
            return true;
        } catch (TimeoutException e) {
            System.out.println("Delete alert not displayed");
            return false;
        }
    }

    public List<List<String>> addProductsFromRecommendations(int numberOfProductsToAdd) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='checkout-frontend']//div[contains(@class, 'recommendations-slider-module_wrapper__gSjnL')]")));
        List<WebElement> sliders = webDriver.findElements(By.xpath("//*[@id='checkout-frontend']//div[contains(@class, 'recommendations-slider-module_wrapper__gSjnL')]"));
        WebElement recommendationSlider = sliders.get(1);
        List<List<String>> addedProducts = new ArrayList<>();
        for (int i = 0; i < numberOfProductsToAdd ; i++){
            List<String> productInfo = new ArrayList<>();
            WebElement recommendedProduct = recommendationSlider.findElement(By.xpath(".//*[contains(@class, 'splide__slide') and not(contains(@aria-hidden, 'true'))]"));
            String productName = recommendedProduct.findElement(By.xpath(".//*[contains(@data-zta, 'P1UIC')]")).getText();
            //compensating for the tag New that some products might have
            productInfo.add(productName.endsWith("new") ? productName.substring(0, productName.length() - 3).trim() : productName);
            //Variant is not displayed, adding "Unknown as the variant"
            //productInfo.add(allProducts.get(i).findElement(By.xpath(".//*[contains(@class, 'ProductListItemVariant-module_variantDescription__36Mpm')]")).getText());
            productInfo.add("Unknown");
            String productPrice = recommendedProduct.findElement(By.xpath(".//*[contains(@class, 'z-price__amount')]")).getText();
            productPrice = productPrice.startsWith("Now ") ? productPrice.substring(5) : productPrice.substring(1);
            productInfo.add(productPrice);
            addedProducts.add(productInfo);

            recommendedProduct.findElement(By.xpath(".//*[contains(@class, 'z-btn')]")).click();
            //checking if the product is added

            Integer cartSize = cartProducts().size();
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id='checkout-frontend']//article[@data-zta='standard_article']"), cartSize));

            recommendationSlider.findElement(By.xpath(".//*[contains(@class, 'splide__arrow--next')]")).click();
            TimeUnit.SECONDS.sleep(2);
        }
        return addedProducts;
    }

    public boolean incrementProductQtyByPrice(String price, Integer qtyToIncrease){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(1));
        price = "€" + price;


            List<WebElement> matchingProducts = webDriver.findElements(By.xpath("//div[@data-zta='articleQuantitySubtotal']//*[contains(text(), '" + price + "')]"));
            if (matchingProducts.isEmpty()) {
                return false;
            }

            for (WebElement matchingProduct : matchingProducts) {
                WebElement lineItem = matchingProduct.findElement(By.xpath("ancestor::article[@data-zta='standard_article']"));

                try {
                    WebElement qtyDropdown = lineItem.findElement(By.xpath(".//*[contains(@data-zta, 'quantityPickerSelect')]"));
                    Select dropdown = new Select(qtyDropdown);
                    Integer currentQty = Integer.parseInt(dropdown.getFirstSelectedOption().getText());
                    Integer newQty = currentQty + qtyToIncrease;
                    dropdown.selectByVisibleText(newQty.toString());
                    wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//*[@id='checkout-frontend']//div[@class='beMKS6MTj3RKepGT1hvS']"),0));
                } catch (Exception e1) {
                    // qtyDropdown is not present, finding qtyPlusButton
                    try {
                        for (int i = 0; i < qtyToIncrease ; i++){
                            WebElement qtyPlusButton = lineItem.findElement(By.xpath(".//*[contains(@data-zta, 'quantityStepperIncrementButton')]"));
                            qtyPlusButton.click();
                            wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//*[@id='checkout-frontend']//div[@class='beMKS6MTj3RKepGT1hvS']"),0));
                        }

                    } catch (Exception e2) {
                        System.out.println("Incrementing the Product Qty from cart is not possible");
                    }

                }
            }
        return true;
    }

    public String changeShippingCountry(String countryToBeSelected, String zipCode){
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        WebElement shippingCountry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='cartSummary']//a[@data-zta='shippingCountryName']")));
        shippingCountry.click();
        WebElement shippingDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-zta='dropdownMenuProxySelect']")));
        shippingDropDown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-zta='dropdownMenuMenu']")));
        WebElement matchingCountry = webDriver.findElement(By.xpath("//*[@id='dd-menu-BreakpointAll']//*[contains(text(), '" + countryToBeSelected + "')]"));
        matchingCountry.click();
        WebElement zipCodeField = webDriver.findElement(By.xpath("//*[@data-zta='shippingCostZipcode']/input"));
        zipCodeField.sendKeys(zipCode);
        WebElement updateButton = webDriver.findElement(By.xpath("//*[@data-zta='shippingCostPopoverAction']"));
        updateButton.click();
        wait.until(ExpectedConditions.textToBePresentInElement(shippingCountry,countryToBeSelected + " (" + zipCode + ")"));

        return shippingCountry.getText();
    }

}
