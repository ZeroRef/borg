package org.zeroref.borg.lab;

public class Msg {
    public static class Tick{}
    public static class TurnOff{}
    public static class Ping{}
    public static class Pong{}

    public static String from(Class cl){
        return from(cl, "");
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
