package base;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import pages.CalendarPage;
import pages.ContactPage;
import pages.HomePage;
import pages.LoginPage;
import pages.OrganizationPage;
import utilities.ConfigReader;

public class BaseClass {

    // static: one browser is opened for each <test> and shared by all its test classes
    public static WebDriver driver;

    // page objects, created here so the test classes do not need any setup code
    public LoginPage lp;
    public HomePage hp;
    public CalendarPage calp;
    public ContactPage cp;
    public OrganizationPage op;

    @BeforeTest(alwaysRun = true)
    @Parameters("browser")
    public void launchBrowser(@Optional("chrome") String browser) {

        if (ConfigReader.get("password").equals("CHANGE_ME")) {
            throw new RuntimeException("Set your real password in src/main/resources/config.properties");
        }

        if (browser.equalsIgnoreCase("chrome")) {

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-notifications");

            Map<String, Object> prefs = new HashMap<String, Object>();
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            prefs.put("profile.password_manager_leak_detection", false);
            options.setExperimentalOption("prefs", prefs);

            driver = new ChromeDriver(options);

        } else if (browser.equalsIgnoreCase("firefox")) {

            driver = new FirefoxDriver();

        } else if (browser.equalsIgnoreCase("edge")) {

            driver = new EdgeDriver();

        } else {
            throw new RuntimeException("Invalid browser: " + browser);
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.get(ConfigReader.get("url"));

        Reporter.log("Browser opened: " + browser, true);
    }

    // runs once for every test class: creates the page objects and logs in
    @BeforeClass(alwaysRun = true)
    public void login() {
        lp = new LoginPage(driver);
        hp = new HomePage(driver);
        calp = new CalendarPage(driver);
        cp = new ContactPage(driver);
        op = new OrganizationPage(driver);

        loginToApplication();
    }

    // runs before every test method, but only does something for LoginTest:
    // the login tests need a logged out user on the login page
    @BeforeMethod(alwaysRun = true)
    public void prepareLoginPage() {
        if (getClass().getSimpleName().equals("LoginTest")) {
            lp.logout();
            lp.openLoginPage();
        }
    }

    public void loginToApplication() {

        lp.openLoginPage();

        if (lp.isLoginPageDisplayed()) {
            lp.login(ConfigReader.get("username"), ConfigReader.get("password"));
        }

        Assert.assertTrue(lp.isLoggedIn(), "Login failed - check username and password in config.properties");
        Reporter.log("Logged in to Vtiger", true);
    }

    // saves a screenshot in the "screenshots" folder (used by the negative tests)
    public void takeScreenshot(String fileName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.createDirectories(Paths.get("screenshots"));
            Path target = Paths.get("screenshots", fileName + ".png");
            Files.copy(src.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            Reporter.log("Screenshot saved: " + target.toAbsolutePath(), true);
        } catch (Exception e) {
            Reporter.log("Could not take screenshot: " + e.getMessage(), true);
        }
    }

    @AfterTest(alwaysRun = true)
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            Reporter.log("Browser closed", true);
        }
    }
}