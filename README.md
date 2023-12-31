# Selenium_Java_TestNG
This project is developed in Selenium using Java (Maven) and TestNG framework. It follows a POM (Page Object Model) based approach for organizing code. Please note that there might be some structural issues in the project, as this is my first attempt at creating an automation framework using these technologies, except Selenium. I will be adding comments to the code and pushing them for better readability,  and I write comments while coding that are specifically easy for me, and polishing them later. I have currently removed them and would be pushing proper ones later today.

## Setup and Execution
To run the project, use the following Maven command in the terminal:
```shell
mvn clean test -DsuiteXmlFile="testng.xml"
```

The above command will execute the test suite specified in the `testng.xml` file.

## Reporting

The project utilizes Extent Report for reporting. The report is generated in the root folder of this project when the suite is run. Please note that I've added my report to `.gitignore` as to prevent unnecessary repository clutter.

## Chrome Driver

The Chrome Driver executable is located in `src/main/resources`. It is set up for Chrome version 117.0.5938.132. While I typically use the Windows environment variable to point to my driver's location, for this project, I've included it here to eliminate any additional dependencies and potential problems.

## Issues Faced

The major hurdle I faced was rate limiting on the website. Due to this limitation, I was unable to run the entire suite in one go and was getting locked out. However, the individual test cases should work correctly.

## Project Scope

The goal of this project was to showcase my logical skills and testing processes. I have covered all the "special cases" mentioned in the document. However, there were certain scenarios I couldn't automate due to time constraints at my workplace (we have a deployment this week). These cases include:

1. Deleting a product and restoring it using the undo button.
2. Interacting with the box that appears when adding a product from the list on "https://www.zooplus.com/shop/cats/dry_cat_food".
3. Clicking the checkout button; my assumption is that this functionality is not in scope.
4. Voucher code functionality.
5. Logging in and validating the cart's behavior.
6. Pressing buttons to increment/decrement/delete a product from the cart, as I believed the text field was a more generic way to do it and faster.