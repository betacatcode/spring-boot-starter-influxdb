package io.github.betacatcode.influx.utils;

public class StrUtil {
    public static String captureName(String name) {
        name = name.substring(0, 1).toLowerCase() + name.substring(1);//UpperCase大写
        return name;
    }
}
