package remindly.fw;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;

public class BaseHelper {
    protected static AndroidDriver driver;

    public BaseHelper(AndroidDriver driver) {
        this.driver = driver;
    }

    public boolean isElementPresent(By locator) {
        return driver.findElements(locator).size() > 0;
    }

//    public void tap(By locator) {
//        driver.findElement(locator).click(); // Cтандартный клик Selenium
//    }

    public void tap(By locator){
        // Находим элемент по локатору
        WebElement element = driver.findElement(locator);
        // Вычисляем координаты центра элемента
        Rectangle rect = element.getRect();
        int centerX = rect.getX() + rect.getWidth() / 2; // X-координата левого края + (ширина % 2) = центр по оси X
        int centerY = rect.getY() + rect.getHeight() / 2; // Y-координата верхнего края + (ширина % 2) = центр по оси Y

        // Выполняем тап в вычисленном центре элемента с помощью жестов
        tap(centerX, centerY);
    }

    //* Принимает конкретные координаты (x и y)
    public void tap(int x, int y) {
        Point tapPoint = new Point(x,y);
        final PointerInput FINGER = new PointerInput(PointerInput.Kind.TOUCH, "FINGER");
        Sequence tap = new Sequence(FINGER, 1);
        tap.addAction(FINGER.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), tapPoint.x, tapPoint.y));
        // У виртуального указателя нет фиксированной начальной позиции. Поэтому указываем ту же точку, что и конечную
        tap.addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        // Если убрать первый pointerDown может вообще ничего не сделать, так как "палец" находится в неизвестной позиции.
        tap.addAction(FINGER.createPointerMove(Duration.ofMillis(100),
                PointerInput.Origin.viewport(), tapPoint.x, tapPoint.y));
        tap.addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(tap));
    }


    public void type(By locator, String text) {
        if (text != null) {
            tap(locator);
            driver.findElement(locator).clear();
            driver.findElement(locator).sendKeys(text);
        }
    }

    public static void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getTextFromElement(By text) {
        return driver.findElement(text).getText();
    }

}
