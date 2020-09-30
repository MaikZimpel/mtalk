package com.zimpel.mtalk.actor;

import com.zimpel.mtalk.message.Command;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import lombok.Getter;

@Getter
public class ActorVerticle extends AbstractVerticle {

    public final static String NAME_KEY = "ACTOR_NAME";

    private final String name;

    public ActorVerticle(String name) {
        this.name = name;
    }

    @Override
    public void start(Promise<Void> promise) {
        System.out.println(name + " attached to event bus.");
        send(new Command(900, "RDY", name));
        promise.complete();
    }

    public void send(Command command) {
        var deliveryOptions = new DeliveryOptions();
        deliveryOptions.setHeaders(MultiMap.caseInsensitiveMultiMap().add(NAME_KEY, name));
        System.out.println("[" + name + "]: publishing message: " + command);
        vertx.eventBus().publish("esko-bus", command, deliveryOptions);
    }

    public void receive(Handler<Message<Command>> handler)  {
        vertx.eventBus().consumer("esko-bus", handler);
    }
}
