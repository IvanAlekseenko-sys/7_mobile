package remindly.fw;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.openqa.selenium.OutputType;
import org.testng.ITestResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class EmulatorHelper {
    protected AndroidDriver driver;
    protected AppiumDriverLocalService service;
    public static final String REPORT_PATH = "src/test_reports";

    protected void start_appium(String ip, int port, String logging) {
        service = new AppiumServiceBuilder()
                .withIPAddress(ip)
                .usingPort(port)
                .withArgument(GeneralServerFlag.LOG_LEVEL, logging)
                .build();
        service.start();
    }

    protected void console_idea(String fileName) {
        try {
            File log_dir = new File(REPORT_PATH);
            if (!log_dir.exists()) {
                log_dir.mkdirs();
            }
            File logFile = new File(log_dir, fileName);
            PrintStream logStream = new PrintStream(new FileOutputStream(logFile));
            TeeOutputStream teeOut = new TeeOutputStream(System.out, logStream); // для отображения в консоли IDEA
            TeeOutputStream teeErr = new TeeOutputStream(System.err, logStream);
            System.setOut(new PrintStream(teeOut));
            System.setErr(new PrintStream(teeErr));
            System.out.println("IDEA logging to: " + fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void console_appium(String fileName) {
        try {
            File log_dir = new File(REPORT_PATH);
            if (!log_dir.exists()) {
                log_dir.mkdirs();
            }
            File logFile = new File(log_dir, fileName);
            OutputStream outputStream = new FileOutputStream(logFile);
            service.addOutPutStream(outputStream);
            service.sendOutputTo(outputStream);
            System.out.println("APPIUM logging to: " + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void console_both(String combinedFileName) {
        try {
            File log_dir = new File(REPORT_PATH);
            if (!log_dir.exists()) {
                log_dir.mkdirs();
            }
            File logFile = new File(log_dir, combinedFileName);
            PrintStream combinedLogStream = new PrintStream(new FileOutputStream(logFile));
            TeeOutputStream combinedOut = new TeeOutputStream(System.out, combinedLogStream);
            TeeOutputStream combinedErr = new TeeOutputStream(System.err, combinedLogStream);
            System.setOut(new PrintStream(combinedOut));
            System.setErr(new PrintStream(combinedErr));
            service.addOutPutStream(combinedLogStream);
            service.sendOutputTo(combinedLogStream);
            System.out.println("COMBINED logging to: " + combinedFileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Log levels used in {@code adb logcat} for filtering log messages.
     *
     * <p>Each log level includes itself and all higher priority levels. The log levels are ordered
     * by priority, with {@code V} being the lowest and {@code S} being the highest.</p>
     *
     * <ul>
     *   <li><b>V (Verbose)</b>: V, D, I, W, E, F, S.
     *   </li>
     *   <li><b>D (Debug)</b>: D, I, W, E, F, S.
     *   </li>
     *   <li><b>I (Info)</b>: I, W, E, F, S.
     *   </li>
     *   <li><b>W (Warning)</b>: W, E, F, S.
     *   </li>
     *   <li><b>E (Error)</b>: E, F, S.
     *   </li>
     *   <li><b>F (Fatal)</b>: F, S.
     *   </li>
     *   <li><b>S (Silent)</b>: Typically used to suppress all logs, but can be used to log only the most critical situations.
     *   </li>
     * </ul>
     *
     * <p>Example usage in {@code adb logcat}:</p>
     * <pre>
     * {@code adb logcat *:W}
     * </pre>
     */
    public void console_app(String fileName, String level) {
        try {
            File log_dir = new File(REPORT_PATH);
            if (!log_dir.exists()) {
                log_dir.mkdirs();
            }
            File logFile = new File(log_dir, fileName);
            Process process = Runtime.getRuntime().exec("adb logcat *:" + level + " -d"); //*  -d означает «dump», то есть "вывести накопленные логи и завершить работу", а не оставаться в режиме постоянного вывода.
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            PrintWriter writer = new PrintWriter(new FileOutputStream(logFile, false)); //* true - дописать в файл лога, false - перезаписать файл
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
            writer.close();
            reader.close();
            System.out.println("APP logs are saved: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeScreenshot(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE && driver != null) {
            // Получаем скриншот как файл
            File scrFile = driver.getScreenshotAs(OutputType.FILE);
            // Формируем имя файла для скриншота
            String screenshotName = result.getName() + ".png";
            File screenshotDir = new File("src/test_reports/");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            try {
                // Создаем файл в указанной папке с заданным именем
                File screenshotFile = new File(screenshotDir, screenshotName);
                // Сохраняем скриншот в файл
                FileUtils.copyFile(scrFile, screenshotFile);
                System.out.println("Screenshot saved: " + screenshotFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void takeVideo(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE && driver != null) {
            // Останавливаем запись и получаем видео в виде Base64 строки
            String base64Video = driver.stopRecordingScreen();
            // Декодируем строку в массив байтов
            byte[] data = Base64.getDecoder().decode(base64Video);
            String videoDir = "src/test_reports/";
            File dir = new File(videoDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String videoPath = videoDir + result.getName() + ".mp4";
            try {
                Files.write(Paths.get(videoPath), data);
                System.out.println("Video saved: " + videoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save_page_source_file(ITestResult result) {  // Если тест упал — сохранить Page Source
        if (result.getStatus() == ITestResult.FAILURE && driver != null) { // Если статус результата теста равен FAILURE (т.е. тест упал) и объект driver не равен null
            try {
                File logDir = new File(REPORT_PATH);
                if (!logDir.exists()) {
                    logDir.mkdirs();
                }
                String pageSource = driver.getPageSource();
                String filename = result.getName() + ".xml";
                // Создаем файл внутри папки logDir
                File pageSourceFile = new File(logDir, filename);
                try (PrintWriter writer = new PrintWriter(new FileOutputStream(pageSourceFile, false))) {
                    writer.println(pageSource);
                }
                System.out.println("PageSource file on error saved in: " + pageSourceFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void stop_appium() {
        service.stop(); // Останавливаем сервис Appium после всех тестов
        System.out.println("APPIUM server on: " + service.getUrl() + " is stopped");
    }

    public void clear_logcat_buffer() {
        try {
            Runtime.getRuntime().exec("adb logcat -c");
            System.out.println("Logcat buffer cleared.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
