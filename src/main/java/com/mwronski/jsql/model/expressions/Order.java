package com.mwronski.jsql.model.expressions;

import java.util.ArrayList;
import java.util.List;

import com.mwronski.jsql.model.SqlToken;
import com.mwronski.jsql.model.Variable;
import com.mwronski.jsql.recording.SqlRecorder;

/**
 * Class representing SQL "ORDER BY" expression
 * 
 * @author Michal Wronski
 * 
 */
public final class Order extends SqlToken {

    private List<VarItem> vars = null;
    private final SqlRecorder recorder;

    public class VarItem {
        private final Variable var;
        private final boolean desc;

        VarItem(final Variable var, final boolean desc) {
            this.var = var;
            this.desc = desc;
        }

        public Variable getVar() {
            return var;
        }

        public boolean isDesc() {
            return desc;
        }
    }

    public Order(final SqlRecorder recorder) {
        this.recorder = recorder;
        vars = new ArrayList<VarItem>();
    }

    public Order asc(final Object column) {
        vars.add(new VarItem(recorder.nextVariable(), false));
        return this;
    }

    public Order desc(final Object column) {
        vars.add(new VarItem(recorder.nextVariable(), true));
        return this;
    }

    public List<VarItem> getVars() {
        return vars;
    }

    public boolean isNull() {
        return vars == null || vars.size() == 0;
    }

}
