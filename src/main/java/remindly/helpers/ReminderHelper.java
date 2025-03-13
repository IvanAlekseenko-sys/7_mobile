package remindly.helpers;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import remindly.fw.BaseHelper;

public class ReminderHelper extends BaseHelper {
    public ReminderHelper(AndroidDriver driver) {
        super(driver);
    }

    public void enterReminderTitle(String text) {
        type(By.id("reminder_title"),text);
    }

    public void tapOnSaveButton() {
        tap(By.id("save_reminder"));
    }

    public String isReminderTextPresent() {
        return getTextFromElement(By.id("recycle_title"));
    }
}
