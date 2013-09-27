package com.mwronski.jsql.model.expressions;

import com.mwronski.jsql.model.SqlToken;

/**
 * SQL expression
 * 
 * @date 25-02-2013
 * @author Michal Wronski
 * 
 */
public abstract class Expression extends SqlToken {

    /**
     * Flag indicates whether expression should be omitted if it's value
     * evaluates to null.
     * 
     * @return
     */
    public abstract boolean isNullOmittable();
}
