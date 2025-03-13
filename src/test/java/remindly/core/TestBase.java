package remindly.core;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import remindly.fw.ApplicationManager;

public class TestBase {
    protected final ApplicationManager app = new ApplicationManager();

    @BeforeSuite
    public void globalSetup() {
        app.setUp();
    }

    @BeforeMethod
    public void setUp() {
        app.init();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
       // app.stop(result);
    }

    @AfterSuite
    public void globalTearDown() {
       // app.tearDown();
    }
}
