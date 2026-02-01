package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;

public class MoneyUtils {

    public static String transform(long number) {
        if (Main.instance.getConfigGestion().isPointsResume()) {
            String transform = number + "";
            int size = transform.length();
            //1000 - 9999
            if (size == 4) {
                return transform.charAt(0) + "K";
            }
            //10000 - 99999
            if (size == 5) {
                return transform.charAt(0) + "" + transform.charAt(1) + "K";
            }
            //100000 - 999999
            if (size == 6) {
                return transform.charAt(0) + "" + transform.charAt(1) + transform.charAt(2) + "K";
            }
            //1000000 - 9999999
            if (size == 7) {
                return transform.charAt(0) + "M";
            }
            //10000000 - 99999999
            if (size == 8) {
                return transform.charAt(0) + "" + transform.charAt(1) + "M";
            }
            //100000000 - 999999999
            if (size == 9) {
                return transform.charAt(0) + "" + transform.charAt(1) + transform.charAt(2) + "M";
            }
            return transform;
        } else {
            return number + "";
        }
    }
}
