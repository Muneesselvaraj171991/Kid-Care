package com.child.parent.kidcare.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PasscodeUtil {
    private static Map<Integer, String>  encodeMap = new HashMap<>();
    static {
        encodeMap.put(0, "IN");
        encodeMap.put(1, "EN");
        encodeMap.put(2, "OU");
        encodeMap.put(3, "GF");
        encodeMap.put(4, "AL");
        encodeMap.put(5, "PS");
        encodeMap.put(6, "44");
        encodeMap.put(7, "77");
        encodeMap.put(8, "GO");
        encodeMap.put(9, "NW");
        encodeMap.put(10, "YU");

        encodeMap.put(11, "0P");
        encodeMap.put(12, "O2");
        encodeMap.put(13, "C3");
        encodeMap.put(14, "MI");
        encodeMap.put(15, "WB");
        encodeMap.put(16, "77");
        encodeMap.put(17, "99");
        encodeMap.put(18, "39");
        encodeMap.put(19, "IU");
        encodeMap.put(20, "WE");

        encodeMap.put(21, "A1");
        encodeMap.put(22, "5T");
        encodeMap.put(23, "9B");
        encodeMap.put(24, "08");
        encodeMap.put(25, "29");
        encodeMap.put(26, "27");
        encodeMap.put(27, "41");
        encodeMap.put(28, "51");
        encodeMap.put(29, "31");
        encodeMap.put(30, "3A");

        encodeMap.put(31, "1F");
        encodeMap.put(32, "F1");
        encodeMap.put(33, "88");
        encodeMap.put(34, "99");
        encodeMap.put(35, "HI");
        encodeMap.put(36, "G7");
        encodeMap.put(37, "34");
        encodeMap.put(38, "AE");
        encodeMap.put(39, "12");
        encodeMap.put(40, "HR");

        encodeMap.put(41, "H1");
        encodeMap.put(42, "SE");
        encodeMap.put(43, "00");
        encodeMap.put(44, "D8");
        encodeMap.put(45, "22");
        encodeMap.put(46, "44");
        encodeMap.put(47, "5A");
        encodeMap.put(48, "7E");
        encodeMap.put(49, "9P");
        encodeMap.put(50, "90");

        encodeMap.put(51, "F8");
        encodeMap.put(52, "4E");
        encodeMap.put(53, "33");
        encodeMap.put(54, "11");
        encodeMap.put(55, "R6");
        encodeMap.put(56, "L5");
        encodeMap.put(57, "12");
        encodeMap.put(58, "B0");
        encodeMap.put(59, "1A");
        encodeMap.put(60, "0F");


    }
    public static int getKeysByValue(String searchValue) {
        for ( Map.Entry<Integer, String> entry : encodeMap.entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue();
             if(searchValue.equals(value)) {
                 return key;
             }
            // do something with key and/or tab
        }

        return -1;
    }

    public static String getEncode(int code) {

        return encodeMap.get(code);
    }

}
