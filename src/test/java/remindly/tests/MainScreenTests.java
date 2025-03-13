package remindly.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import remindly.core.TestBase;

public class MainScreenTests extends TestBase {
    @Test
    public void appLaunchTest(){
        Assert.assertTrue(app.getMainScreenHelper().isNoReminderTestPresent());
    }
}
