package com.mwronski.jsql.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Basic SQL token
 * 
 * @date 25-02-2013
 * @author Michal Wronski
 * 
 */
public class SqlToken implements Iterable<SqlToken> {

    private final List<SqlToken> children = new ArrayList<SqlToken>();

    public final <T extends SqlToken> void add(final T child) {
        children.add(child);
    }

    public final <T extends SqlToken> void addFirst(final T child) {
        children.add(0, child);
    }

    public final <T extends SqlToken> void addAll(final Collection<T> children) {
        this.children.addAll(children);
    }

    @Override
    public final Iterator<SqlToken> iterator() {
        return children.iterator();
    }

    public final boolean hasChildren() {
        return !children.isEmpty();
    }

    public List<SqlToken> getChildren() {
        return children;
    }
}
