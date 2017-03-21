package com.github.rahulsom.swci

import com.google.cloud.pubsub.spi.v1.SubscriberClient
import com.google.pubsub.v1.SubscriptionName
import groovy.transform.CompileStatic

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@CompileStatic
class SwciApplication {
    private static ExecutorService executors = Executors.newFixedThreadPool(5)
    static void main(String[] args) {
        manageSubscriptions()
        executors.submit { ScheduledExecutor.run() }
    }
    private static void manageSubscriptions() {
        SubscriptionName subscriptionName = GoogleSubscriber.getSubscriptionName('swci-32764', 'checkin-executor')
        def subscriberClient = SubscriberClient.create()
        executors.submit { GoogleSubscriber.runReadLoop(subscriberClient, subscriptionName) }
    }
}
