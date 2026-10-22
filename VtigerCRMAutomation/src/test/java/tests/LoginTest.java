package tests;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import base.BaseClass;
import utilities.ConfigReader;
import utilities.ExcelUtility;

public class LoginTest extends BaseClass {

    ExcelUtility eu = new ExcelUtility();

    @Test(priority = 1, groups = {"Smoke", "Positive"}, description = "TC01 Login with valid credentials")
    public void validLogin_Test() throws Exception {

        lp.login(ConfigReader.get("username"), ConfigReader.get("password"));
        Thread.sleep(3000);

        Assert.assertTrue(lp.isLoggedIn(), "Home page was not displayed");

        Reporter.log("Login successful with valid username and password", true);
    }

    @Test(priority = 2, groups = {"Negative"}, description = "TC02 Login with invalid username and password")
    public void invalidLogin_Test() throws Exception {

        String username = eu.getDataFromExcel("Login", 1, 1);
        String password = eu.getDataFromExcel("Login", 1, 2);

        lp.login(username, password);
        Thread.sleep(3000);
        takeScreenshot("TC02_InvalidLogin");

        Assert.assertTrue(lp.isLoginPageDisplayed(), "User should stay on the login page");

        Reporter.log("Login not allowed for invalid username and password", true);
    }

    @Test(priority = 3, groups = {"Negative"}, description = "TC03 Login with blank username")
    public void blankUsername_Test() throws Exception {

        String username = eu.getDataFromExcel("Login", 2, 1);
        String password = eu.getDataFromExcel("Login", 2, 2);

        lp.login(username, password);
        Thread.sleep(3000);
        takeScreenshot("TC03_BlankUsername");

        Assert.assertTrue(lp.isLoginPageDisplayed(), "User should stay on the login page");

        Reporter.log("Login not allowed when username is blank", true);
    }

    @Test(priority = 4, groups = {"Negative"}, description = "TC04 Login with blank password")
    public void blankPassword_Test() throws Exception {

        String username = eu.getDataFromExcel("Login", 3, 1);
        String password = eu.getDataFromExcel("Login", 3, 2);

        lp.login(username, password);
        Thread.sleep(3000);
        takeScreenshot("TC04_BlankPassword");

        Assert.assertTrue(lp.isLoginPageDisplayed(), "User should stay on the login page");

        Reporter.log("Login not allowed when password is blank", true);
    }

    @Test(priority = 5, groups = {"System", "Positive"}, description = "TC05 Home page is not accessible after logout")
    public void logoutThenOpenHome_Test() throws Exception {

        lp.login(ConfigReader.get("username"), ConfigReader.get("password"));
        Assert.assertTrue(lp.isLoggedIn(), "Login failed");
        Reporter.log("Logged in", true);

        lp.logout();
        driver.get(ConfigReader.get("url") + "index.php?module=Home&action=index");
        Thread.sleep(3000);

        Assert.assertTrue(lp.isLoginPageDisplayed(), "Login page should be shown after logout");

        Reporter.log("Logged out, Home page is not opened without login", true);
    }
}