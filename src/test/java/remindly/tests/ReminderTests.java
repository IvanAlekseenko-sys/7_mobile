package remindly.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import remindly.core.TestBase;

public class ReminderTests extends TestBase {
    @Test
    public void addReminderTitlePositiveTest() {
        // добавить com.blanyal.remindly:id/add_reminder
        app.getMainScreenHelper().tapOnAddReminderButton();
        // ввести текст com.blanyal.remindly:id/reminder_title
        app.getReminderHelper().enterReminderTitle("PORTISHEAD");
        // сохранить com.blanyal.remindly:id/save_reminder
        app.getReminderHelper().tapOnSaveButton();
        // проверить текст com.blanyal.remindly:id/recycle_title
        Assert.assertTrue(app.getReminderHelper().isReminderTextPresent().contains("PORTISHEAD"));
    }
}
