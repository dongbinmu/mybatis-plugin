package com.weaponlin.mybatis.plugin.strategy;

public class RemainderShardingStrategy implements ShardingStrategy {

    @Override
    public String completeTable(String database, String table, int databaseSize, int tableSize, Long hash) {
        StringBuilder sb = new StringBuilder();
        String dbIndex = String.format("%02d", hash % databaseSize);
        String tableIndex = String.format("%03d", hash % tableSize);
        return sb.append(database)
                .append("_")
                .append(dbIndex)
                .append(".")
                .append(table)
                .append("_")
                .append(tableIndex)
                .toString();
    }
}
