package com.github.rahulsom.swci

import groovy.sql.Sql
import groovy.transform.CompileStatic

@CompileStatic
class ConnectionManager {
    static Sql sql = Sql.newInstance('jdbc:h2:./swci', 'sa', 'secret')
}
