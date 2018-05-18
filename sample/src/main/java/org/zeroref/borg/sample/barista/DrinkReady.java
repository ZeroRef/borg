package org.zeroref.borg.sample.barista;

import java.util.UUID;

////////// barista
public class DrinkReady
{
    public DrinkReady(UUID correlationId, String drink) {
        this.correlationId = correlationId;
        this.drink = drink;
    }

    public UUID correlationId;
    public String drink;
}
