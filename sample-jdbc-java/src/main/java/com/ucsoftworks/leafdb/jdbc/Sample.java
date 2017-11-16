package com.ucsoftworks.leafdb.jdbc;

import com.ucsoftworks.leafdb.LeafDb;
import com.ucsoftworks.leafdb.dsl.Condition;
import com.ucsoftworks.leafdb.dsl.Field;
import com.ucsoftworks.leafdb.dsl.FieldExtensionsKt;
import kotlin.jvm.functions.Function1;

import java.util.LinkedList;
import java.util.List;

public class Sample {
    private static final String TABLE_TEST = "test";
    private static final Field A = new Field("a"), B = new Field("b");

    public static void main(String[] args) {
        final SampleHelper sampleHelper = new SampleHelper("sample-java.db", 1);
        final LeafDb leafDb = sampleHelper.getLeafDb();

        leafDb.insertOrUpdate(TABLE_TEST, new SampleData(1, 2), A).execute();
        leafDb.insertOrUpdate(TABLE_TEST, new SampleData(3, 4), A).execute();
        leafDb.insertOrUpdate(TABLE_TEST, new SampleData(5, 6), A).execute();

        final Field f = FieldExtensionsKt.minus(5, A);

        leafDb.updateAll(TABLE_TEST, new LinkedList<SampleData>() {{
            add(new SampleData(3, 3));
        }}, new Function1<SampleData, Condition>() {
            @Override
            public Condition invoke(SampleData sampleData) {
                return A.equal(sampleData.a);
            }
        }).execute();

        final List<SampleData> dbData = leafDb.select(TABLE_TEST).where(A.plus(B).greaterThan(5)).findAs(SampleData.class).execute();

        System.out.println(dbData);
    }

    private static class SampleData {
        int a;
        int b;

        SampleData(int a, int b) {
            this.a = a;
            this.b = b;

        }

        @Override
        public String toString() {
            return "SampleData{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
    }

}
