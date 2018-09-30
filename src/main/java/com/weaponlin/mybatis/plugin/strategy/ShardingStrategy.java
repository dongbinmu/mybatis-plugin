package com.weaponlin.mybatis.plugin.strategy;

/**
 * the strategy to sharding databases and tables
 */
public interface ShardingStrategy {

    String completeTable(String database, String table, int databaseSize, int tableSize, Long hash);
}
