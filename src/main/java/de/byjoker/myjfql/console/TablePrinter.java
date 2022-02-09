package de.byjoker.myjfql.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * https://github.com/2xsaiko/crogamp/blob/master/src/com/github/mrebhan/crogamp/cli/TableList.java
 */

public class TablePrinter {

    private static final String[] BLINE = {"-", "\u2501"};
    private static final String[] CROSSING = {"-+-", "\u2548"};
    private static final String[] VERTICAL_TSEP = {"|", "\u2502"};
    private static final String[] VERTICAL_BSEP = {"|", "\u2503"};
    private static final String TLINE = "\u2500";
    private static final String CORNER_TL = "\u250c";
    private static final String CORNER_TR = "\u2510";
    private static final String CORNER_BL = "\u2517";
    private static final String CORNER_BR = "\u251b";
    private static final String CROSSING_L = "\u2522";
    private static final String CROSSING_R = "\u252a";
    private static final String CROSSING_T = "\u252c";
    private static final String CROSSING_B = "\u253b";

    private final String[] descriptions;
    private final ArrayList<String[]> table;
    private final int[] tableSizes;
    private final int rows;
    private final Alignment[] aligns;
    private int index;
    private String filter;
    private boolean ucode;
    private Comparator<String[]> comparator;
    private int spacing;

    public TablePrinter(String... descriptions) {
        this(descriptions.length, descriptions);
    }

    public TablePrinter(int columns, String... descriptions) {
        if (descriptions.length != columns) {
            throw new IllegalArgumentException();
        }

        this.filter = null;
        this.rows = columns;
        this.descriptions = descriptions;
        this.table = new ArrayList<>();
        this.tableSizes = new int[columns];
        this.updateSizes(descriptions);
        this.ucode = System.getProperty("os.name").equalsIgnoreCase("Linux");
        this.spacing = 1;
        this.aligns = new Alignment[columns];
        this.comparator = null;
        Arrays.fill(aligns, Alignment.LEFT);
    }

    private void updateSizes(String[] elements) {
        for (int i = 0; i < tableSizes.length; i++) {
            if (elements[i] != null) {
                int j = tableSizes[i];
                j = Math.max(j, elements[i].length());
                tableSizes[i] = j;
            }
        }
    }

    public TablePrinter compareWith(Comparator<String[]> c) {
        this.comparator = c;
        return this;
    }

    public TablePrinter sortBy(int column) {
        return this.compareWith(Comparator.comparing(o -> o[column]));
    }

    public TablePrinter align(int column, Alignment align) {
        aligns[column] = align;
        return this;
    }

    public TablePrinter withSpacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    /**
     * Adds a row to the table with the specified elements.
     */

    public TablePrinter addRow(String... elements) {
        if (elements.length != rows) {
            throw new IllegalArgumentException();
        }

        table.add(elements);
        updateSizes(elements);
        return this;
    }

    public TablePrinter filterBy(int par0, String pattern) {
        this.index = par0;
        this.filter = pattern;
        return this;
    }

    public TablePrinter withUnicode(boolean ucodeEnabled) {
        this.ucode = ucodeEnabled;
        return this;
    }

    public void print() {
        StringBuilder line = null;

        if (ucode) {
            for (int i = 0; i < rows; i++) {
                if (line != null) {
                    line.append(CROSSING_T);
                } else {
                    line = new StringBuilder();
                    line.append(CORNER_TL);
                }
                for (int j = 0; j < tableSizes[i] + 2 * spacing; j++) {
                    line.append(TLINE);
                }
            }
            assert line != null;
            line.append(CORNER_TR);
            System.out.println(line);

            line = null;
        }

        // print header
        for (int i = 0; i < rows; i++) {
            if (line != null) {
                line.append(gc(VERTICAL_TSEP));
            } else {
                line = new StringBuilder();
                if (ucode) {
                    line.append(gc(VERTICAL_TSEP));
                }
            }
            StringBuilder part = new StringBuilder(descriptions[i]);
            while (part.length() < tableSizes[i] + spacing) {
                part.append(" ");
            }
            for (int j = 0; j < spacing; j++) {
                line.append(" ");
            }
            line.append(part);
        }
        if (ucode) {
            assert line != null;
            line.append(gc(VERTICAL_TSEP));
        }
        assert line != null;
        System.out.println(line);

        // print vertical seperator
        line = null;
        for (int i = 0; i < rows; i++) {
            if (line != null) {
                line.append(gc(CROSSING));
            } else {
                line = new StringBuilder();
                if (ucode) {
                    line.append(CROSSING_L);
                }
            }
            for (int j = 0; j < tableSizes[i] + 2 * spacing; j++) {
                line.append(gc(BLINE));
            }
        }
        if (ucode) {
            assert line != null;
            line.append(CROSSING_R);
        }
        assert line != null;
        System.out.println(line);

        line = null;
        ArrayList<String[]> localTable = table;

        if (filter != null) {
            Pattern p = Pattern.compile(filter);
            localTable.removeIf(arr -> {
                String s = arr[index];
                return !p.matcher(s).matches();
            });
        }

        if (localTable.isEmpty()) {
            String[] sa = new String[rows];
            localTable.add(sa);
        }

        localTable.forEach(arr -> {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == null) {
                    arr[i] = "";
                }
            }
        });

        if (comparator != null) {
            localTable.sort(comparator);
        }

        for (String[] strings : localTable) {
            for (int i = 0; i < rows; i++) {
                if (line != null) {
                    line.append(gc(VERTICAL_BSEP));
                } else {
                    line = new StringBuilder();
                    if (ucode) {
                        line.append(gc(VERTICAL_BSEP));
                    }
                }
                StringBuilder part = new StringBuilder();
                for (int j = 0; j < spacing; j++) {
                    part.append(" ");
                }
                if (strings[i] != null) {
                    switch (aligns[i]) {
                        case LEFT:
                            part.append(strings[i]);
                            break;
                        case RIGHT:
                            for (int j = 0; j < tableSizes[i] - strings[i].length(); j++) {
                                part.append(" ");
                            }
                            part.append(strings[i]);
                            break;
                        case CENTER:
                            for (int j = 0; j < (tableSizes[i] - strings[i].length()) / 2; j++) {
                                part.append(" ");
                            }
                            part.append(strings[i]);
                            break;
                    }
                }
                while (part.length() < tableSizes[i] + spacing) {
                    part.append(" ");
                }
                for (int j = 0; j < spacing; j++) {
                    part.append(" ");
                }
                line.append(part);
            }
            if (ucode) {
                line.append(gc(VERTICAL_BSEP));
            }
            System.out.println(line.toString());

            line = null;
        }

        if (ucode) {
            for (int i = 0; i < rows; i++) {
                if (line != null) {
                    line.append(CROSSING_B);
                } else {
                    line = new StringBuilder();
                    line.append(CORNER_BL);
                }
                for (int j = 0; j < tableSizes[i] + 2 * spacing; j++) {
                    line.append(gc(BLINE));
                }
            }
            line.append(CORNER_BR);
            System.out.println(line);
        }

    }

    private String gc(String[] src) {
        return src[ucode ? 1 : 0];
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

}
