package com.ucsoftworks.leafdb.jdbc;

import com.ucsoftworks.leafdb.LeafDbSchemaMigration;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Pasenchuk Victor on 29/08/2017
 */

public class SampleHelper extends LeafDbJdbcHelper {

    public SampleHelper(String dbName, int version) {
        super(dbName, version);
    }

    @NotNull
    @Override
    public LeafDbSchemaMigration onCreate() {
        return new LeafDbSchemaMigration().createTable("test");
    }

    @NotNull
    @Override
    public LeafDbSchemaMigration onUpgradeSchema(int oldVersion, int newVersion) {
        return new LeafDbSchemaMigration();
    }
}
