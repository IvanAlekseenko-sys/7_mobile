package remindly.helpers;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import remindly.fw.BaseHelper;

public class MainScreenHelper extends BaseHelper {

    public MainScreenHelper(AndroidDriver driver) {
        super(driver);
    }

    public boolean isNoReminderTestPresent() {
        return isElementPresent(By.id("no_reminder_text"));
    }

    public void tapOnAddReminderButton() {
        tap(By.id("add_reminder"));
    }
}
