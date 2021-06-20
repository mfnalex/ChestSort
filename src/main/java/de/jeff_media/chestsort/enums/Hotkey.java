package de.jeff_media.chestsort.enums;

import java.util.Locale;

public enum Hotkey {

    SHIFT_CLICK,
    MIDDLE_CLICK, DOUBLE_CLICK, SHIFT_RIGHT_CLICK,
    LEFT_CLICK_OUTSIDE, RIGHT_CLICK_OUTSIDE;

    public static String getPermission(Hotkey hotkey) {
        return hotkey.name().toLowerCase(Locale.ROOT).replace("_", "");
    }
}
