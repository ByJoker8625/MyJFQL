import de.byjoker.myjfql.database.*;
import de.byjoker.myjfql.lang.ColumnComparator;
import de.byjoker.myjfql.lang.ColumnFilter;
import de.byjoker.myjfql.lang.SortingOrder;
import de.byjoker.myjfql.util.JsonColumnParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class Speedtest {

    public static void main(String[] args) {
        Table table = new MapManagedTable("users", Arrays.asList("id", "name", "password"), "id");

        long l1 = System.currentTimeMillis();
        double d0;

        for (int i = 0; i < 10000; i++) {
            SimpleColumn column = new CompiledColumn();
            column.insert("id", i);
            column.insert("name", "__" + i);
            column.insert("password", Objects.hash("pw", i));
            table.addColumn(column);
        }

        d0 = (double) System.currentTimeMillis() - l1;

        Collection<Column> columns;

        double d1;
        double d2;

        {
            long l = System.currentTimeMillis();

            columns = ColumnFilter.filter(table, Arrays.asList("id !== 1"), new ColumnComparator(
                    "id"
            ), SortingOrder.ASC);

            d1 = (double) System.currentTimeMillis() - l;

            System.out.println(columns);
        }

        {
            long l = System.currentTimeMillis();

            String jsonObject = JsonColumnParser.stringifyCompiledColumns(columns, table.getStructure());

            d2 = (double) System.currentTimeMillis() - l;

            System.out.println(jsonObject);
        }

        System.out.println();
        System.out.println(d0 + " ms inserting");
        System.out.println(d1 + " ms selecting & sorting");
        System.out.println(d2 + " ms formatting");
        System.out.println((d1 + d2) + " ms total select time");
        System.out.println();
        System.out.println("total of " + (d1 + d2 + d0) + " ms");

    }
}
