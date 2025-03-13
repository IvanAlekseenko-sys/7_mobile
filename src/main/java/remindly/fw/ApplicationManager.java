package remindly.fw;

import io.appium.java_client.android.AndroidDriver;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import remindly.helpers.MainScreenHelper;
import remindly.helpers.ReminderHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class ApplicationManager extends EmulatorHelper {

    DesiredCapabilities capabilities;
    @Getter
    MainScreenHelper mainScreenHelper;
    @Getter
    ReminderHelper reminderHelper;

    @BeforeSuite
    public void setUp() {
        start_appium("127.0.0.1", 4723, "info");
        console_idea("console_idea.log");
        console_appium("console_appium.log");
        console_both("console_both.log");
        //console_app();
    }

    @BeforeMethod
    public void init() {
        capabilities = new DesiredCapabilities();

        capabilities.setPlatform(Platform.ANDROID);
        capabilities.setVersion("10");
        capabilities.setCapability("appium:deviceName", "mob");

        capabilities.setCapability("appium:appPackage", "com.blanyal.remindly");
        capabilities.setCapability("appium:appActivity", "com.blanyal.remindme.MainActivity");
        capabilities.setCapability("appium:automationName", "UiAutomator2");

        // Необходимо для того, чтобы не сбрасывать данные приложения
        capabilities.setCapability("appium:noReset", false); // true - сбрасывать данные приложения, false - не сбрасывать
        // Отключение анимаций
        capabilities.setCapability("appium:disableWindowAnimation", true); // true - отключить анимации, false - включить

        //! только при уровне `DEBUG` в логирование APPIUM исходный код страницы будет сохранён в лог
        capabilities.setCapability("appium:printPageSourceOnFindFailure", true);// true - печатать Page Source при ошибке поиска элемента, false - не печатать

        driver = new AndroidDriver(service.getUrl(), capabilities);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.startRecordingScreen();
        clear_logcat_buffer();

        System.out.println("SessionID is: " + driver.getSessionId());

        mainScreenHelper = new MainScreenHelper(driver);
        reminderHelper = new ReminderHelper(driver);
        driver.findElement(By.id("android:id/button1")).click();
    }

    @AfterMethod
    public void stop(ITestResult result) {
        if (driver != null) {
            console_app("console_app.log", "V");
            takeScreenshot(result);
            takeVideo(result);
            save_page_source_file(result);
            driver.quit();
        }
    }

    @AfterSuite
    public void tearDown() {
        stop_appium();
    }
}
