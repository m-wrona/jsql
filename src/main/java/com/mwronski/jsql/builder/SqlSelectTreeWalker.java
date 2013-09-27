package com.mwronski.jsql.builder;

import java.util.ArrayList;
import java.util.List;

import com.mwronski.jsql.model.Table;
import com.mwronski.jsql.model.dql.JoinStatement;
import com.mwronski.jsql.model.dql.SelectStatement;

/**
 * Walker visiting tokens in SELECT statement and pass them to given
 * {@link SqlSelectBuilder}
 * 
 * @see SqlSelectBuilder
 * @date 05-07-2013
 * @author Michal Wronski
 * 
 */
public final class SqlSelectTreeWalker {

    public void walk(final SelectStatement select, final SqlSelectBuilder selectBuilder) {
        walkSelectColumns(selectBuilder, select);
        selectBuilder.handleFrom(select.getFrom());
        for (JoinStatement join : select.getJoins()) {
            selectBuilder.handleJoin(join.getTable(), join.getDirection(), join.getType(), join.getOn());
        }
        if (select.getWhere() != null) {
            selectBuilder.handleWhere(select.getWhere());
        }
        selectBuilder.handleOrderBy(select.getOrder());
        selectBuilder.handleGroupBy(select.getGroupedBy());
    }

    /**
     * Check which columns from which tables should be put into SELECT statement
     * 
     * @param selectBuilder
     * @param select
     */
    private void walkSelectColumns(final SqlSelectBuilder selectBuilder, SelectStatement select) {
        List<Table> tables = new ArrayList<Table>();
        tables.addAll(select.getSelectedTables());
        if (select.getSelectedColumns().isEmpty() && select.getSelectedTables().isEmpty()) {
            tables.addAll(select.getFrom());
            for (JoinStatement join : select.getJoins()) {
                tables.add(join.getTable());
            }
        }
        selectBuilder.handleSelect(tables, select.getSelectedColumns(), select.isDistinct(), select.isCount());
    }
}
