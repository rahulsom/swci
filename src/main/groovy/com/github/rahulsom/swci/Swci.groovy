package com.github.rahulsom.swci

import geb.Browser
import geb.waiting.WaitTimeoutException
import groovy.transform.ToString

import java.time.Instant

@SuppressWarnings("unused")
class Swci {

    @ToString
    static class Result {
        boolean success
        String position
        String reportPrefix
    }

    static Result checkin(String confirmationNumber, String firstName, String lastName) {
        def timestamp = Instant.now().toString().replace(':', '')
        def retval = new Result(reportPrefix: "${confirmationNumber}-${timestamp}")

        Browser.drive {
            go 'https://www.southwest.com/air/check-in/index.html'
            report("${retval.reportPrefix}-0")

            $('#confirmationNumber') << confirmationNumber
            $('#passengerFirstName') << firstName
            $('#passengerLastName') << lastName

            $('form button').click()
            try {
                waitFor() {
                    title.contains('Review')
                }
            } catch (WaitTimeoutException ignored) {
                retval.success = false
                return retval
            } finally {
                report("${retval.reportPrefix}-1")
            }

            if ($('.message_error').size() > 0) {
                return retval
            }

            $('button.submit-button').click()
            try {
                waitFor() {
                    title.contains('Confirmation')
                }
            } catch (WaitTimeoutException ignored) {
                retval.success = false
                return retval
            } finally {
                report("${retval.reportPrefix}-2")
            }

            def position = $('.air-check-in-passenger-item--information-boarding-position').text()
            if (position) {
                retval.success = true
                retval.position = position
            }
        }.quit()
        return retval
    }

    static void main(String[] args) {
        def confirmationNumber = System.getenv("CONFIRMATION_NUMBER")
        def firstName = System.getenv("FIRST_NAME")
        def lastName = System.getenv("LAST_NAME")

        println("confirmationNumber: $confirmationNumber")
        println("firstName: $firstName")
        println("lastName: $lastName")

        def result = checkin(confirmationNumber, firstName, lastName)
        new File("build/gebreports/swci.properties").text = "POSITION=${result.position}"
        println result
        assert result.success
    }
}
