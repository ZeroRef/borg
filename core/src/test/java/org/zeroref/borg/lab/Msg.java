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
                "\"id\":\"f8ce617c-6445-4141-9cd7-78fa0bf2210c\"," +
                "\"type\":\"" + cl.getClass().getName() + "\"," +
                "\"returnAddress\":\"" + originator + "\"," +
                "\"payload\":" + recordGson.toJson(cl) +
                "}\n";
    }
}
