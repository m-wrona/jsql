package com.mwronski.jsql.model;

import java.util.ArrayList;
import java.util.List;

import com.mwronski.jsql.recording.SqlRecorder;

/**
 * Collection that enables gathering columns
 * 
 * @date 08-04-2013
 * @author Michal Wronski
 * 
 */
public final class Columns extends SqlToken {

    private final SqlRecorder recorder;

    public Columns(final SqlRecorder recorder) {
        this.recorder = recorder;
    }

    /**
     * Add new column
     * 
     * @param o
     * @return the same instance
     */
    public Columns column(final Object o) {
        add(recorder.nextVariable());
        return this;
    }

    public List<Variable> getColumns() {
        List<Variable> vars = new ArrayList<Variable>();
        for (SqlToken token : this) {
            vars.add((Variable) token);
        }
        return vars;
    }

}
