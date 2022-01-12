package poseteam.pose.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

//크롬드라이버 멀티쓰레드로 사용하여 다중 사용자가 접근 가능하게 하는 메소드, 인스타그램을 모바일 버전으로 보여준다.
public class ChromeDriver_MultiThread {
    //WebDriver 설정
    private WebDriver driver, driver2, driver3;
    private WebElement element;
    private String url = "https://www.instagram.com/";
    public void run() {
        //chromeOptions.addArguments("headless"); //안보이게 하는거

        Map<String, Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", 480);
        deviceMetrics.put("height", 520);
        deviceMetrics.put("pixelRatio", 3.0);

        Map<String, Object> mobileEmulation = new HashMap<String, Object>();
        mobileEmulation.put("deviceMetrics", deviceMetrics);

        mobileEmulation.put("userAgent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        chromeOptions.addArguments("--window-size=480,700");

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\pc\\Desktop\\캡스톤\\chromedriver_win32\\chromedriver.exe");
        try{
            driver = new ChromeDriver(chromeOptions);
            driver.get(url);
        }catch (Exception e){
            System.out.println(e);
            driver.close();
        }
    }
}
