package com.weaponlin.mybatis.plugin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Data
public class ShardingProperties {

    private static final String DOT = "\\.";
    private static final String COMMA = ",";

    private static final String TABLES = "tables";
    private static final String DATABASE_SIZE = "databaseSize";
    private static final String TABLE_SIZE = "tableSize";
    private static final String SHARDING_STRATEGY = "shardingStrategy";


    private Map<String, List<String>> databaseAndTable = new HashMap<>();

    private int databaseSize = 100;

    private int tableSize = 10;

    /**
     * full class name
     */
    private String shardingStrategy = "com.weaponlin.mybatis.plugin.strategy.RemainderShardingStrategy";

    public static ShardingProperties fromProperties(Properties properties) {
        ShardingProperties shardingProperties = new ShardingProperties();
        if (properties == null) {
            return shardingProperties;
        } else {
            ImmutableMap<String, String> map = Maps.fromProperties(properties);

            Optional.ofNullable(map.get(DATABASE_SIZE)).ifPresent(e -> shardingProperties.setDatabaseSize(Integer.valueOf(e)));
            Optional.ofNullable(map.get(TABLE_SIZE)).ifPresent(e -> shardingProperties.setTableSize(Integer.valueOf(e)));
            Optional.ofNullable(map.get(SHARDING_STRATEGY)).ifPresent(shardingProperties::setShardingStrategy);

            String tableString = Optional.ofNullable(map.get(TABLES)).orElse(null);
            if (StringUtils.isBlank(tableString)) {
                log.warn("no sharding database and table");
            }
            Map<String, List<String>> databaseAndTable = shardingProperties.getDatabaseAndTable();
            Arrays.stream(tableString.split(COMMA)).filter(StringUtils::isNotBlank).forEach(e -> {
                String[] split = e.split(DOT);
                if (split.length == 2) {
                    String db = split[0].toLowerCase();
                    String table = split[1].toLowerCase();
                    List<String> tables = databaseAndTable.get(db);
                    if (CollectionUtils.isEmpty(tables)) {
                        databaseAndTable.put(db, Lists.newArrayList(table));
                    } else {
                        tables.add(table);
                    }
                }
            });
        }

        return shardingProperties;
    }

    public boolean isValidDatabaseAndTable(Pair<String, String> table) {
        if (table == null) {
            return false;
        }
        List<String> tables = databaseAndTable.get(table.getLeft().toLowerCase());
        if (CollectionUtils.isEmpty(tables)) {
            return false;
        }
        return tables.stream().anyMatch(e -> e.equalsIgnoreCase(table.getRight()));
    }
}
