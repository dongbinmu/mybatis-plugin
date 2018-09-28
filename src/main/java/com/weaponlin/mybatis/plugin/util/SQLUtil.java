package com.weaponlin.mybatis.plugin.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.mapping.SqlCommandType;

@Slf4j
@UtilityClass
public class SQLUtil {

    public Pair<String, String> getDatabaseAndTable(String sql, SqlCommandType sqlCommandType) {
        if (StringUtils.isBlank(sql)) {
            throw new RuntimeException("invalid sql: " + sql);
        }

        return analysisSQL(sql, sqlCommandType);
    }

    /**
     * TODO refactor
     * @param sql
     * @param sqlCommandType
     * @return
     */
    private Pair<String, String> analysisSQL(String sql, SqlCommandType sqlCommandType) {
        String database = "";
        String table = "";
        if (sqlCommandType == SqlCommandType.SELECT) {
            int index = sql.indexOf(" from ");
            String remainSql = sql.substring(index + 6).trim();
            int spaceIndex = remainSql.indexOf(" ");
            String [] split;
            if (spaceIndex != -1) {
                split = remainSql.substring(0, spaceIndex).split("\\.");
            } else {
                split = remainSql.split("\\.");
            }
            if (split.length == 2) {
                database = split[0];
                table = split[1];
            }
        } else if (sqlCommandType == SqlCommandType.UPDATE) {
            // update table set
            String remainSql = sql.substring("update ".length()).trim();
            int spaceIndex = remainSql.indexOf(" ");
            if (spaceIndex != -1) {
                String[] split = remainSql.substring(0, spaceIndex).split("\\.");
                if (split.length == 2) {
                    database = split[0];
                    table = split[1];
                }
            }
        } else if (sqlCommandType == SqlCommandType.INSERT) {
            // insert into user()
            int index = sql.indexOf(" into ");
            String remainSql = sql.substring(index + 6).trim();
            int spaceIndex = remainSql.indexOf("(");
            if (spaceIndex != -1) {
                String[] split = remainSql.substring(0, spaceIndex).split("\\.");
                if (split.length == 2) {
                    database = split[0];
                    table = split[1];
                }
            }
        } else if (sqlCommandType == SqlCommandType.DELETE) {
            int index = sql.indexOf(" from ");
            String remainSql = sql.substring(index + 6).trim();
            int spaceIndex = remainSql.indexOf(" ");
            if (spaceIndex != -1) {
                String[] split = remainSql.substring(0, spaceIndex).split("\\.");
                if (split.length == 2) {
                    database = split[0];
                    table = split[1];
                }
            }
        } else {
            log.warn("fucking sql command type: {}", sqlCommandType);
        }
        if (StringUtils.isBlank(database) || StringUtils.isBlank(table)) {
            return null;
        }
        return Pair.of(database.trim(), table.trim());
    }
}
