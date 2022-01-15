package de.byjoker.myjfql.lang;

import java.util.Comparator;
import java.util.regex.Pattern;

public class StringComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        if (o1.equals("null")) {
            if (o2.equals("null")) {
                return 0;
            }

            return 1;
        } else if (isNumber(o1)) {
            if (o2.equals("null")) {
                return -1;
            }

            if (!isNumber(o2)) {
                return -1;
            }

            return Long.compare(Long.parseLong(o1), Long.parseLong(o2));
        } else {
            if (o2.equals("null")) {
                return -1;
            }

            if (isNumber(o2)) {
                return 1;
            }

            return o1.compareTo(o2);
        }
    }

    private boolean isNumber(String s) {
        return Pattern.compile("\\d*").matcher(s).matches();
    }

}
