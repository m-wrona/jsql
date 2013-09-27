package com.mwronski.jsql.parser.dql;

import com.mwronski.jsql.model.dql.SelectStatement;
import com.mwronski.jsql.recording.SqlRecorder;

/**
 * Parser for WHERE statement
 * 
 * @author Michal Wronski
 * @date 27.09.13 12:53
 */
public final class Where extends Condition {

    private final SelectStatement select;

    /**
     * Sort select by given criteria
     * 
     * @return order by parser
     */
    public Order orderBy() {
        Order order = new Order(getRecorder(), select);
        return order;
    }

    /**
     * Group by given columns
     * 
     * @return group by parser
     */
    public GroupBy groupBy() {
        return new GroupBy(getRecorder(), select);
    }

    /**
     * Create instance
     * 
     * @param recorder
     *            that allows recording of select tokens
     * @param select
     *            for which condition will be created
     */
    Where(SqlRecorder recorder, SelectStatement select) {
        super(recorder);
        this.select = select;
        select.setWhere(getChain());
    }

}
