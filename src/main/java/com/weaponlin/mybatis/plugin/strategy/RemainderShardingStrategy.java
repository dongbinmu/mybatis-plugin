package com.weaponlin.mybatis.plugin.strategy;

public class RemainderShardingStrategy implements ShardingStrategy {

    @Override
    public String completeTable(String database, String table, Long hash) {
        StringBuilder sb = new StringBuilder();
        String index = String.valueOf(hash);
        String dbIndex = index.substring(index.length() - 2);
        String tableIndex = index.substring(index.length() - 3);
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
