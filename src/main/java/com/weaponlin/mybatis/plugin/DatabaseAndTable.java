package com.weaponlin.mybatis.plugin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import com.weaponlin.mybatis.plugin.strategy.ShardingStrategy;

import java.util.*;

/**
 * TODO refactor (and we may be change the class name)
 * 1. change the way to receive database and table
 * 2. add custom sharding strategy, user can implement {@link ShardingStrategy} by self.
 * Need Sharding Databases and Tables
 */
@Data
public class DatabaseAndTable {
    private Map<String, List<String>> dbt;

    private DatabaseAndTable() {
        dbt = new HashMap<>();
    }

    public static DatabaseAndTable fromProperties(Properties properties) {
        DatabaseAndTable dt = new DatabaseAndTable();
        if (properties == null) {
            return dt;
        }else {
            ImmutableMap<String, String> map = Maps.fromProperties(properties);
            map.entrySet().stream().forEach(entry -> {
                String database = entry.getKey();
                String tables = entry.getValue();
                if (StringUtils.isNotBlank(tables)) {
                    Arrays.stream(tables.split(",")).forEach(table -> dt.put(database.toLowerCase(), table.toLowerCase()));
                } else {
                    // TODO print warning log
                }
            });
        }

        return dt;
    }

    public void put(String database, String table) {
        List<String> tables = dbt.get(database);
        if (tables == null) {
            dbt.put(database, Lists.newArrayList(table));
        } else {
            tables.add(table);
        }
    }

    public boolean needSharding(String database, String table) {
        List<String> tableList = dbt.get(database);
        if (CollectionUtils.isEmpty(tableList)) {
            return false;
        } else {
            return tableList.stream().anyMatch(e -> e.equalsIgnoreCase(table));
        }
    }
}
