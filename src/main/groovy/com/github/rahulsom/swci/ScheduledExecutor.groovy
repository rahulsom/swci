package com.github.rahulsom.swci

import groovy.transform.CompileStatic
import org.jooq.impl.DSL

import java.sql.Timestamp
import java.time.Instant

import static com.github.rahulsom.swci.public_.tables.Requests.REQUESTS

@CompileStatic
class ScheduledExecutor {
    @SuppressWarnings("GroovyInfiniteLoopStatement")
    static void run() {
        while (true) {
            def dsl = DSL.using(ConnectionManager.sql.connection)
            def requestsDue = dsl.selectFrom(REQUESTS).
                    where(REQUESTS.CHECKINTIME.le(Timestamp.from(Instant.now()))).
                    fetch()

            requestsDue.listIterator().each { row ->
                println row
                new CheckinSupport().checkin(row.confirmationnumber, row.firstname, row.lastname)
                dsl.deleteFrom(REQUESTS).where(REQUESTS.ID.eq(row.id)).execute()
            }

            if (requestsDue.size()) {
                dsl.selectFrom(REQUESTS).fetch().listIterator().each { println it.valuesRow() }
            }

            dsl.close()
            sleep 60000
        }
    }
}
