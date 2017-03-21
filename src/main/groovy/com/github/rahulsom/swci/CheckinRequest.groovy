package com.github.rahulsom.swci

import groovy.transform.CompileStatic
import groovy.transform.ToString

import java.time.Instant

@ToString(includePackage = false, includeNames = true)
@CompileStatic
class CheckinRequest {
    Instant checkinTime
    String confirmationNumber, firstName, lastName
}
