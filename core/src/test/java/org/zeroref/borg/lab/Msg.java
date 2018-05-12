package org.zeroref.borg.lab;

import com.google.gson.Gson;

public class Msg {

    private static Gson recordGson = new Gson();

    public static class Tick{}
    public static class TurnOff{}
    public static class Ping{}
    public static class Pong{}

    public static String from(Class cl){
        return from(cl, "");
    }

    public static String fromInstance(Object cl){
        return "{" +
                "\"uuid\":\"9fb046d0-4318-4f2e-8ec3-0152449ebe7d\"," +
                "\"headers\":{}," +
                "\"content\":{" +
                "\"returnAddress\":\"" +
                "\"," +
                "\"type\":\"" +
                cl.getClass().getName() +
                "\"," +
                "\"payload\":" + recordGson.toJson(cl) + 
                "}" +
                "}\n";
    }

    public static String from(Class cl, String origin){
        return "{" +
                "\"uuid\":\"9fb046d0-4318-4f2e-8ec3-0152449ebe7d\"," +
                "\"headers\":{}," +
                "\"content\":{" +
                "\"returnAddress\":\"" +
                origin +
                "\"," +
                "\"type\":\"" +
                cl.getTypeName() +
                "\"," +
                "\"payload\":\"{}\"" +
                "}" +
                "}\n";
    }
}
