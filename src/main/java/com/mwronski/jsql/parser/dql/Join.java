package com.mwronski.jsql.parser.dql;

import java.util.List;

import com.mwronski.jsql.model.Noun;
import com.mwronski.jsql.model.Noun.Nouns;
import com.mwronski.jsql.model.SqlToken;
import com.mwronski.jsql.model.Table;
import com.mwronski.jsql.parser.SqlParser;

/**
 * SQL JOIN command parser
 * 
 * @date 25-02-2013
 * @author Michal Wronski
 * 
 */
public final class Join implements SqlParser {

    private final Select select;
    private final Noun joinNoun;

    /**
     * Create instance
     * 
     * @param select
     *            statement where join will be made
     * @param o
     *            data that will be joined to select
     * @param nouns
     *            additional nouns for specifying whether join is left, outer
     *            etc.
     */
    Join(final Select select, final Object o, final Noun... nouns) {
        this.select = select;
        List<Table> fromTables = select.getRecorder().tables(o);
        if (fromTables.isEmpty()) {
            throw new RuntimeException("Didn't found SQL tokens in FROM cluasule");
        }
        joinNoun = new Noun(Nouns.JOIN);
        select.append(joinNoun);
        for (Noun noun : nouns) {
            joinNoun.add(noun);
        }
        joinNoun.addAll(fromTables);
    }

    /**
     * Add join condition
     * 
     * @param on
     * @return SELECT where this instance is JOINed
     */
    public Select on(final Condition on) {
        joinNoun.add(new Noun(Nouns.ON));
        joinNoun.add(on.getRoot());
        return select;
    }

    @Override
    public SqlToken getRoot() {
        return joinNoun;
    }

}
