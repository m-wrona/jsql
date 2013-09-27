package com.mwronski.jsql.model.expressions;

import java.util.Arrays;
import java.util.List;

import com.mwronski.jsql.model.Variable;
import com.mwronski.jsql.recording.SqlRecorder;

/**
 * Class representing collection of values, used in SQL expressions "IN" and
 * "NOT IN".
 * 
 * @author Michal Wronski
 * 
 */
public final class Collection extends Expression {

    public enum CollectionType {
        IN, NOT_IN
    }

    private final Variable var;
    private final List<Object> values;
    private final CollectionType type;
    private final boolean omittable;

    public Collection(final SqlRecorder recorder, final Object param, final CollectionType type,
            final boolean omittable, final Object[] values) {
        var = recorder.nextVariable();
        this.values = values != null && values.length > 0 ? Arrays.asList(values) : null;
        this.omittable = omittable;
        this.type = type;
    }

    public Variable getVar() {
        return var;
    }

    public CollectionType getType() {
        return type;
    }

    public List<Object> getValues() {
        return values;
    }

    @Override
    public boolean isNullOmittable() {
        return omittable;
    }

    public boolean shouldBeOmitted() {
        return isNullOmittable() && isNull();
    }

    private boolean isNull() {
        return values == null || values.size() == 0;
    }

}
