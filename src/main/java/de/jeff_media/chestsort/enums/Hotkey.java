package de.jeff_media.chestsort.enums;

import java.util.Locale;

public enum Hotkey {

    SHIFT_CLICK,
    MIDDLE_CLICK, DOUBLE_CLICK, SHIFT_RIGHT_CLICK,
    OUTSIDE, LEFT_CLICK, RIGHT_CLICK;

    public static String getPermission(Hotkey hotkey) {
        String permission = "chestsort.hotkey." + hotkey.name().toLowerCase(Locale.ROOT).replace("_", "");
        //System.out.println("Permission for " + hotkey.name()+ ": " + permission);
        return permission;
    }
}
