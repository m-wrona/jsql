package com.mwronski.jsql.parser.dql;

import java.util.List;

import com.mwronski.jsql.model.*;
import com.mwronski.jsql.model.Noun.Nouns;
import com.mwronski.jsql.model.expressions.Order;
import com.mwronski.jsql.parser.SqlParser;
import com.mwronski.jsql.recording.SqlRecorder;

/**
 * SQL SELECT command parser. Statement in built in the chain based on current
 * instance.
 * 
 * TODO refactoring - add checking grammar rules
 * 
 * @date 25-02-2013
 * @author Michal Wronski
 * 
 */
public final class Select implements SqlParser {

    private final SqlRecorder recorder;
    private final Noun selectNoun;

    /**
     * Create new select statement
     * 
     * @param recorder
     *            that allows recording of select tokens
     * @param tables
     *            from which all columns should be taken
     */
    public Select(final SqlRecorder recorder, Object... tables) {
        this.recorder = recorder;
        selectNoun = new Noun(Nouns.SELECT);
        for (Table table : recorder.tables(tables)) {
            selectNoun.add(table);
        }
        List<Variable> columns = recorder.variables();
        selectNoun.addAll(columns);
    }

    /**
     * FROM clause
     * 
     * @param o
     *            object from which data will be taken
     * @param others
     *            additional objects from which data will be taken
     * @return the same instance
     */
    public Select from(final Object o, final Object... others) {
        List<Table> fromTables = recorder.tables(o, others);
        if (fromTables.isEmpty()) {
            throw new RuntimeException("Didn't found SQL tokens in FROM cluasule");
        }
        Noun fromNoun = new Noun(Nouns.FROM);
        fromNoun.addAll(fromTables);
        selectNoun.add(fromNoun);
        return this;
    }

    /**
     * JOIN clause
     * 
     * @param o
     *            which data will be joined to SELECT
     * @return the same instance
     */
    public Join join(final Object o) {
        return new Join(this, o);
    }

    /**
     * LEFT JOIN clause
     * 
     * @param o
     *            which data will be joined to SELECT
     * @return the same instance
     */
    public Join leftJoin(final Object o) {
        return new Join(this, o, new Noun(Nouns.LEFT));
    }

    /**
     * LEFT OUTER JOIN clause
     * 
     * @param o
     *            which data will be joined to SELECT
     * @return the same instance
     */
    public Join leftOuterJoin(final Object o) {
        return new Join(this, o, new Noun(Nouns.LEFT), new Noun(Nouns.OUTER));
    }

    /**
     * LEFT INNER JOIN clause
     * 
     * @param o
     *            which data will be joined to SELECT
     * @return the same instance
     */
    public Join leftInnerJoin(final Object o) {
        return new Join(this, o, new Noun(Nouns.LEFT), new Noun(Nouns.INNER));
    }

    /**
     * RIGHT JOIN clause
     * 
     * @param o
     *            which data will be joined to SELECT
     * @return the same instance
     */
    public Join rightJoin(final Object o) {
        return new Join(this, o, new Noun(Nouns.RIGHT));
    }

    /**
     * RIGHT OUTER JOIN clause
     * 
     * @param o
     *            which data will be joined to SELECT
     * @return the same instance
     */
    public Join rightOuterJoin(final Object o) {
        return new Join(this, o, new Noun(Nouns.RIGHT), new Noun(Nouns.OUTER));
    }

    /**
     * RIGHT OUTER JOIN clause
     * 
     * @param o
     *            which data will be joined to SELECT
     * @return the same instance
     */
    public Join rightInnerJoin(final Object o) {
        return new Join(this, o, new Noun(Nouns.RIGHT), new Noun(Nouns.INNER));
    }

    /**
     * WHERE clause
     * 
     * @param where
     *            condition for SELECT statement
     * @return the same instance
     */
    public Select where(final Condition where) {
        if (!where.isNull()) {
            Noun whereNoun = new Noun(Nouns.WHERE);
            selectNoun.add(whereNoun);
            whereNoun.add(where.getRoot());
        }
        return this;
    }

    /**
     * Sort select by given criteria
     * 
     * @return the same instance
     */
    public Order orderBy() {
        Noun orderBy = new Noun(Nouns.ORDER_BY);
        selectNoun.add(orderBy);
        Order order = new Order(recorder);
        orderBy.add(order);
        return order;
    }

    /**
     * Group by given columns
     * 
     * @return columns by which grouping can be made
     */
    public Columns groupBy() {
        Noun groupBy = new Noun(Nouns.GROUP_BY);
        selectNoun.add(groupBy);
        Columns groupByColumns = new Columns(recorder);
        groupBy.add(groupByColumns);
        return groupByColumns;
    }

    /**
     * SELECT COUNT clause
     * 
     * @return the same instance
     */
    public Select count() {
        selectNoun.add(new Noun(Nouns.COUNT));
        return this;
    }

    /**
     * Mark that results should be unique
     * 
     * @return the same instance
     */
    public Select distinct() {
        selectNoun.addFirst(new Noun(Nouns.DISTINCT));
        return this;
    }

    @Override
    public SqlToken getRoot() {
        return selectNoun;
    }

    /**
     * Append new token into statement
     * 
     * @param sqlToken
     */
    void append(final SqlToken sqlToken) {
        selectNoun.add(sqlToken);
    }

    protected SqlRecorder getRecorder() {
        return recorder;
    }

}
