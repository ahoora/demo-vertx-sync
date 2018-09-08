package com.demo;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.sync.SyncVerticle;

import static io.vertx.ext.sync.Sync.*;

public class MainVerticle extends SyncVerticle {
    private static final String ADDRESS = "some-address";


    @Suspendable
    @Override
    public void start() throws Exception {

        EventBus eb = vertx.eventBus();
        eb.consumer(ADDRESS).handler(msg -> {
            System.out.println("Waiting");
            // reply after one second
            vertx.setTimer(1000, tid -> msg.reply("wibble"));
        });

        // If you want to do sync stuff in an async handler it must be transformed to a fiber handler
        vertx.createHttpServer().requestHandler(fiberHandler(req -> {

            // Send a message to address and wait for a reply
            Message<String> reply = awaitResult(h -> eb.send(ADDRESS, "blah", h));

            System.out.println("Got reply: " + reply.body());

            req.response().end("blah");

        })).listen(8080, "localhost");

    }

}
