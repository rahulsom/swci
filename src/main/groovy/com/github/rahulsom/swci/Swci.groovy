package com.github.rahulsom.swci

import geb.Browser
import io.github.bonigarcia.wdm.ChromeDriverManager
import org.openqa.selenium.chrome.ChromeDriver

import java.time.Instant

@SuppressWarnings("unused")
class Swci {

    static class Result {
        boolean success
        String position
        String reportPrefix
    }

    static Result checkin(String confirmationNumber, String firstName, String lastName) {
        ChromeDriverManager.getInstance().setup()
        def timestamp = Instant.now().toString().replace(':', '')
        def retval = new Result(reportPrefix: "${confirmationNumber}-${timestamp}")

        Browser.drive(driver: new ChromeDriver()) {
            go 'https://www.southwest.com/air/check-in/index.html'
            report("${retval.reportPrefix}-0")

            $('#confirmationNumber') << confirmationNumber
            $('#passengerFirstName') << firstName
            $('#passengerLastName') << lastName

            $('form button').click()
            waitFor() {
                title.contains('Review')
            }
            report("${retval.reportPrefix}-1")

            if ($('.message_error').size() > 0) {
                return retval
            }

            $('button.submit-button').click()
            waitFor() {
                title.contains('Confirmation')
            }

            report("${retval.reportPrefix}-2")
            def position = $('.air-check-in-passenger-item--information-boarding-position').text()
            if (position) {
                retval.success = true
                retval.position = position
            }
        }.quit()
        return retval
    }
}
