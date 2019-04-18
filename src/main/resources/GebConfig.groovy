import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

WebDriverManager.chromedriver().setup()

driver = {
    new ChromeDriver(new ChromeOptions().with {
        addArguments('--headless')
        addArguments('--disable-gpu')
        addArguments('--window-size=1024,768')
        it
    })
}
reportsDir = new File("build/gebreports")