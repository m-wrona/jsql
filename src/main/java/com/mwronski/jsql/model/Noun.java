package com.mwronski.jsql.model;

/**
 * Single noun that can be used in SQL
 * 
 * @date 25-02-2013
 * @author Michal Wronski
 * 
 */
public final class Noun extends SqlToken {

    public enum Nouns {
        SELECT, FROM, WHERE, AND, OR, JOIN, ON, COUNT, DISTINCT, LEFT, RIGHT, OUTER, INNER, ORDER_BY, GROUP_BY;
    }

    private final Nouns noun;

    public Noun(final Nouns noun) {
        this.noun = noun;
    }

    public Nouns getNoun() {
        return noun;
    }

}
