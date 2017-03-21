package com.github.rahulsom.swci

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.pubsub.spi.v1.SubscriberClient
import com.google.pubsub.v1.SubscriptionName
import groovy.transform.CompileStatic
import org.jooq.DSLContext
import org.jooq.impl.DSL

import java.sql.Timestamp

import static com.github.rahulsom.swci.public_.tables.Requests.REQUESTS

@CompileStatic
class GoogleSubscriber {

    @SuppressWarnings("GroovyInfiniteLoopStatement")
    static void runReadLoop(SubscriberClient subscriberClient, SubscriptionName subscriptionName) {
        while (true) {
            println "Pulling..."
            try {
                def response = subscriberClient.pull(subscriptionName, false, 10)
                response.receivedMessagesList.each {
                    ObjectMapper mapper = new ObjectMapper()
                    mapper.findAndRegisterModules()

                    def request = mapper.readValue(it.message.data.toStringUtf8(), CheckinRequest)
                    println request
                    def dsl = DSL.using(ConnectionManager.sql.connection)
                    insertOrUpdate(dsl, request)
                    printRequests(dsl)
                    dsl.close()
                    subscriberClient.acknowledge(subscriptionName, [it.ackId])
                }
            } catch (Exception e) {
                println "Exception occurred"
                e.printStackTrace()
                sleep 10000
            }
        }
    }
    private static void printRequests(DSLContext dsl) {
        def records = dsl.selectFrom(REQUESTS).fetch()
        records.listIterator().each { println it.valuesRow() }
    }
    private static void insertOrUpdate(DSLContext dsl, CheckinRequest request) {
        int matches = getCount(dsl, request)
        if (matches) {
            println "Updating..."
            updateRequest(dsl, request)
        } else {
            println "Inserting..."
            insertRequest(dsl, request)
        }
    }
    private static int getCount(DSLContext dsl, CheckinRequest request) {
        def matches = dsl.selectCount().
                from(REQUESTS).
                where(REQUESTS.CONFIRMATIONNUMBER.equal(request.confirmationNumber)).
                fetchOne().value1()
        matches
    }
    private static int insertRequest(DSLContext dsl, CheckinRequest request) {
        dsl.insertInto(REQUESTS).
                set(REQUESTS.ID, UUID.randomUUID().toString()).
                set(REQUESTS.CONFIRMATIONNUMBER, request.confirmationNumber).
                set(REQUESTS.FIRSTNAME, request.firstName).
                set(REQUESTS.LASTNAME, request.lastName).
                set(REQUESTS.CHECKINTIME, Timestamp.from(request.checkinTime)).
                execute()
    }
    private static int updateRequest(DSLContext dsl, CheckinRequest request) {
        dsl.update(REQUESTS).
                set(REQUESTS.FIRSTNAME, request.firstName).
                set(REQUESTS.LASTNAME, request.lastName).
                set(REQUESTS.CHECKINTIME, Timestamp.from(request.checkinTime)).
                where(REQUESTS.CONFIRMATIONNUMBER.equal(request.confirmationNumber)).
                execute()
    }
    static SubscriptionName getSubscriptionName(String projectId, String subscriptionId) {
        SubscriptionName.
                newBuilder().
                setProject(projectId).
                setSubscription(subscriptionId).
                build()
    }

}
