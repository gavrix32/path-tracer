package net.gavrix32.engine.graphics;

import net.gavrix32.engine.io.Key;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {
    private static final Properties props, defaultProps;
    private static final String propsPath = System.getProperty("user.dir") + "/pathtracer.properties";
    private static final InputStream defaultPropsInputStream;

    static {
        defaultPropsInputStream = Config.class.getClassLoader().getResourceAsStream("default.properties");
        try {
            File propsFile = new File(propsPath);
            defaultProps = new Properties();
            defaultProps.load(defaultPropsInputStream);
            props = new Properties(defaultProps);
            if (!propsFile.exists()) defaultProps.store(new FileOutputStream(propsPath), null);
            props.load(new FileInputStream(propsPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void reset() {
        try {
            defaultProps.store(new FileOutputStream(propsPath), null);
            props.load(new FileInputStream(propsPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save() {
        try {
            props.store(new FileOutputStream(propsPath), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getString(String key) {
        return props.getProperty(key);
    }

    public static void setString(String key, String value) {
        props.setProperty(key, value);
    }

    public static int getInt(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    public static void setInt(String key, int value) {
        props.setProperty(key, String.valueOf(value));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(props.getProperty(key));
    }

    public static void setBoolean(String key, boolean value) {
        props.setProperty(key, String.valueOf(value));
    }

    public static float getFloat(String key) {
        return Float.parseFloat(props.getProperty(key));
    }

    public static void setFloat(String key, float value) {
        props.setProperty(key, String.valueOf(value));
    }

    public static Key getKey(String key) {
        Map<String, Key> hashMap = new HashMap<>();
        hashMap.put("unknown", Key.UNKNOWN);
        hashMap.put("space", Key.SPACE);
        hashMap.put("apostrophe", Key.APOSTROPHE);
        hashMap.put("comma", Key.COMMA);
        hashMap.put("minus", Key.MINUS);
        hashMap.put("period", Key.PERIOD);
        hashMap.put("slash", Key.SLASH);
        hashMap.put("num0", Key.NUM0);
        hashMap.put("num1", Key.NUM1);
        hashMap.put("num2", Key.NUM2);
        hashMap.put("num3", Key.NUM3);
        hashMap.put("num4", Key.NUM4);
        hashMap.put("num5", Key.NUM5);
        hashMap.put("num6", Key.NUM6);
        hashMap.put("num7", Key.NUM7);
        hashMap.put("num8", Key.NUM8);
        hashMap.put("num9", Key.NUM9);
        hashMap.put("semicolon", Key.SEMICOLON);
        hashMap.put("equal", Key.EQUAL);
        hashMap.put("a", Key.A);
        hashMap.put("b", Key.B);
        hashMap.put("c", Key.C);
        hashMap.put("d", Key.D);
        hashMap.put("e", Key.E);
        hashMap.put("f", Key.F);
        hashMap.put("g", Key.G);
        hashMap.put("h", Key.H);
        hashMap.put("i", Key.I);
        hashMap.put("j", Key.J);
        hashMap.put("k", Key.K);
        hashMap.put("l", Key.L);
        hashMap.put("m", Key.M);
        hashMap.put("n", Key.N);
        hashMap.put("o", Key.O);
        hashMap.put("p", Key.P);
        hashMap.put("q", Key.Q);
        hashMap.put("r", Key.R);
        hashMap.put("s", Key.S);
        hashMap.put("t", Key.T);
        hashMap.put("u", Key.U);
        hashMap.put("v", Key.V);
        hashMap.put("w", Key.W);
        hashMap.put("x", Key.X);
        hashMap.put("y", Key.Y);
        hashMap.put("z", Key.Z);
        hashMap.put("left_bracket", Key.LEFT_BRACKET);
        hashMap.put("backslash", Key.BACKSLASH);
        hashMap.put("right_bracket", Key.RIGHT_BRACKET);
        hashMap.put("grave_accent", Key.GRAVE_ACCENT);
        hashMap.put("world1", Key.WORLD1);
        hashMap.put("world2", Key.WORLD2);
        hashMap.put("escape", Key.ESCAPE);
        hashMap.put("enter", Key.ENTER);
        hashMap.put("tab", Key.TAB);
        hashMap.put("backspace", Key.BACKSPACE);
        hashMap.put("insert", Key.INSERT);
        hashMap.put("delete", Key.DELETE);
        hashMap.put("right", Key.RIGHT);
        hashMap.put("left", Key.LEFT);
        hashMap.put("down", Key.DOWN);
        hashMap.put("up", Key.UP);
        hashMap.put("page_up", Key.PAGE_UP);
        hashMap.put("page_down", Key.PAGE_DOWN);
        hashMap.put("home", Key.HOME);
        hashMap.put("end", Key.END);
        hashMap.put("caps_lock", Key.CAPS_LOCK);
        hashMap.put("scroll_lock", Key.SCROLL_LOCK);
        hashMap.put("num_lock", Key.NUM_LOCK);
        hashMap.put("print_screen", Key.PRINT_SCREEN);
        hashMap.put("pause", Key.PAUSE);
        hashMap.put("f1", Key.F1);
        hashMap.put("f2", Key.F2);
        hashMap.put("f3", Key.F3);
        hashMap.put("f4", Key.F4);
        hashMap.put("f5", Key.F5);
        hashMap.put("f6", Key.F6);
        hashMap.put("f7", Key.F7);
        hashMap.put("f8", Key.F8);
        hashMap.put("f9", Key.F9);
        hashMap.put("f10", Key.F10);
        hashMap.put("f11", Key.F11);
        hashMap.put("f12", Key.F12);
        hashMap.put("f13", Key.F13);
        hashMap.put("f14", Key.F14);
        hashMap.put("f15", Key.F15);
        hashMap.put("f16", Key.F16);
        hashMap.put("f17", Key.F17);
        hashMap.put("f18", Key.F18);
        hashMap.put("f19", Key.F19);
        hashMap.put("f20", Key.F20);
        hashMap.put("f21", Key.F21);
        hashMap.put("f22", Key.F22);
        hashMap.put("f23", Key.F23);
        hashMap.put("f24", Key.F24);
        hashMap.put("f25", Key.F25);
        hashMap.put("kp0", Key.KP0);
        hashMap.put("kp1", Key.KP1);
        hashMap.put("kp2", Key.KP2);
        hashMap.put("kp3", Key.KP3);
        hashMap.put("kp4", Key.KP4);
        hashMap.put("kp5", Key.KP5);
        hashMap.put("kp6", Key.KP6);
        hashMap.put("kp7", Key.KP7);
        hashMap.put("kp8", Key.KP8);
        hashMap.put("kp9", Key.KP9);
        hashMap.put("kp_decimal", Key.KP_DECIMAL);
        hashMap.put("kp_divide", Key.KP_DIVIDE);
        hashMap.put("kp_multiply", Key.KP_MULTIPLY);
        hashMap.put("kp_subtract", Key.KP_SUBTRACT);
        hashMap.put("kp_add", Key.KP_ADD);
        hashMap.put("kp_enter", Key.KP_ENTER);
        hashMap.put("kp_equal", Key.KP_EQUAL);
        hashMap.put("left_shift", Key.LEFT_SHIFT);
        hashMap.put("left_control", Key.LEFT_CONTROL);
        hashMap.put("left_alt", Key.LEFT_ALT);
        hashMap.put("left_super", Key.LEFT_SUPER);
        hashMap.put("right_shift", Key.RIGHT_SHIFT);
        hashMap.put("right_control", Key.RIGHT_CONTROL);
        hashMap.put("right_alt", Key.RIGHT_ALT);
        hashMap.put("right_super", Key.RIGHT_SUPER);
        hashMap.put("menu", Key.MENU);
        hashMap.put("last", Key.LAST);
        return hashMap.get(props.getProperty(key));
    }
}