package com.github.rahulsom.swci

import geb.Browser
import io.github.bonigarcia.wdm.ChromeDriverManager
import org.openqa.selenium.chrome.ChromeDriver

class CheckinSupport {
    void checkin(String confirmationNumber, String firstName, String lastName) {
        ChromeDriverManager.getInstance().setup()
        Browser.drive(driver: new ChromeDriver()) {
            go 'https://www.southwest.com/flight/retrieveCheckinDoc.html'

            $('#confirmationNumber') << confirmationNumber
            $('#firstName') << firstName
            $('#lastName') << lastName

            $('#submitButton').click()

        }.quit()
    }
}
