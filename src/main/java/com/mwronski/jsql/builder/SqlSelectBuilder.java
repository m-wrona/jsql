package com.mwronski.jsql.builder;

import java.util.List;

import com.mwronski.jsql.model.SqlToken;
import com.mwronski.jsql.model.Table;
import com.mwronski.jsql.model.Variable;
import com.mwronski.jsql.model.expressions.Order.VarItem;

/**
 * Builder for SELECT statement
 * 
 * @date 28-06-2013
 * @author Michal Wronski
 * 
 */
public interface SqlSelectBuilder extends SqlCommandBuilder {

    /**
     * Handle SELECT part of the statement
     * 
     * @param tables
     *            from which all columns should be taken
     * @param selectColumns
     *            columns to be selected
     * @param distinct
     *            flag indicates whether select is distinct
     * @param count
     *            flag indicates whether generic counting is made
     */
    void handleSelect(List<Table> tables, List<Variable> selectColumns, boolean distinct, boolean count);

    /**
     * Handle FROM part of the statement
     * 
     * @param tables
     *            tables from which data is taken
     */
    void handleFrom(List<Table> tables);

    /**
     * Handle JOIN part of the statement
     * 
     * @param joinedTable
     * @param left
     *            flag indicates whether join is left(true) or right(false). If
     *            null simple join is used.
     * @param inner
     *            flag indicates whether join is inner(true) or outer(false). If
     *            null simple join is used.
     * @param onCondition
     *            joining conditions
     */
    void handleJoin(Table joinedTable, Boolean left, Boolean inner, SqlToken onCondition);

    /**
     * Handle WHERE part of the statement
     * 
     * @param where
     *            select conditions
     */
    void handleWhere(SqlToken where);

    /**
     * Handle ORDER BY part of the statement
     * 
     * @param variables
     *            variables with set order
     */
    void handleOrderBy(List<VarItem> variables);

    /**
     * Handle GROUP BY part of the statement
     * 
     * @param variables
     *            variables that results are grouped by
     */
    void handleGroupBy(List<Variable> variables);

}
