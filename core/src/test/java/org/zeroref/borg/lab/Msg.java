package org.zeroref.borg.lab;

import com.google.gson.Gson;

public class Msg {

    private static Gson recordGson = new Gson();

    public static class Tick{}
    public static class TurnOff{}
    public static class Ping{}
    public static class Pong{}

    public static String fromInstance(Object cl){
        return fromInstance(cl, "");
    }

    public static String fromInstance(Object cl, String originator){
        return "{" +
                "\"uuid\":\"9fb046d0-4318-4f2e-8ec3-0152449ebe7d\"," +
                "\"headers\":{}," +
                "\"content\":{" +
                "\"returnAddress\":\"" +
                originator +
                "\"," +
                "\"type\":\"" +
                cl.getClass().getName() +
                "\"," +
                "\"payload\":" + recordGson.toJson(cl) +
                "}" +
                "}\n";
    }
}
