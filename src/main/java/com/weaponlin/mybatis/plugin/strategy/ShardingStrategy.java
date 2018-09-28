package com.weaponlin.mybatis.plugin.strategy;

/**
 * the strategy to sharding databases and tables
 */
public interface ShardingStrategy {

    /**
     * TODO add dataBaseSize and tableSize
     * @param database
     * @param table
     * @param hash
     * @return
     */
    String completeTable(String database, String table, Long hash);
}
