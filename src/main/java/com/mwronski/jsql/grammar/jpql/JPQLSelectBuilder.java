package com.mwronski.jsql.grammar.jpql;

import java.util.ArrayList;
import java.util.List;

import com.mwronski.jsql.grammar.common.GrammarUtil;
import com.mwronski.jsql.grammar.common.Nouns;
import com.mwronski.jsql.grammar.common.SQLSelectBuilder;
import com.mwronski.jsql.model.Table;
import com.mwronski.jsql.model.Variable;
import com.mwronski.jsql.model.dql.JoinStatement;
import com.mwronski.jsql.model.expressions.ExpressionChain;

/**
 * Select statement for JP-QL grammar
 * 
 * @date 05-07-2013
 * @author Michal Wronski
 * 
 */
final class JPQLSelectBuilder extends SQLSelectBuilder {

    private final StringBuilder sqlJoinConditions = new StringBuilder();

    @Override
    protected String toLowerString(String string) {
        return "lower(" + string + ")";
    }

    @Override
    protected String getRegExpWildcardMark() {
        return " LIKE ";
    }

    @Override
    protected String getVariableName(Variable var) {
        return GrammarUtil.getVariableName(var, true, true);
    }

    @Override
    protected String getTableDefinitionName(Table table) {
        if (table.getAlias() != null) {
            return super.getTableDefinitionName(table);
        }
        return GrammarUtil.getTableName(table) + Nouns.SPACE + GrammarUtil.getTableName(table);
    }

    @Override
    protected String getTableName(Table table) {
        return table.getAlias() != null ? super.getTableName(table) : GrammarUtil.getTableName(table);
    }

    protected void appendSelectAllFromTable(final StringBuilder sql, final Table table) {
        appendElementBreak(sql);
        sql.append(getTableName(table));
    }

    @Override
    public String asSQL() {
        StringBuilder select = new StringBuilder();
        select.append(Nouns.SELECT).append(Nouns.SPACE).append(sqlColumns);
        select.append(Nouns.SPACE);
        select.append(Nouns.FROM).append(Nouns.SPACE).append(sqlTables);
        select.append(sqlJoins);
        boolean hasJoins = sqlJoinConditions.length() > 0;
        boolean hasConditions = sqlWhere.length() > 0;
        if (hasConditions || hasJoins) {
            select.append(Nouns.SPACE).append(Nouns.WHERE);
            if (hasJoins) {
                select.append(Nouns.SPACE).append(Nouns.LEFT_BRACKET).append(sqlJoinConditions)
                        .append(Nouns.RIGHT_BRACKET);
                if (hasConditions) {
                    select.append(Nouns.SPACE).append(Nouns.AND);
                }
            }
            if (hasConditions) {
                select.append(Nouns.SPACE).append(sqlWhere);
            }
        }
        if (sqlOrderBy.length() > 0) {
            select.append(Nouns.SPACE);
            select.append(Nouns.ORDER_BY).append(Nouns.SPACE).append(sqlOrderBy);
        }
        if (sqlGroupBy.length() > 0) {
            select.append(Nouns.SPACE);
            select.append(Nouns.GROUP_BY).append(Nouns.SPACE).append(sqlGroupBy);
        }
        return select.toString();
    }

    @Override
    public void handleJoin(final Table joinedTable, JoinStatement.Direction direction, JoinStatement.Type type,
            ExpressionChain onCondition) {
        // emulate join statement
        // TODO change to real join when model and parser is changed
        List<Table> joinedTables = new ArrayList<Table>();
        joinedTables.add(joinedTable);
        handleFrom(joinedTables);
        appendConditions(sqlJoinConditions, onCondition);
    }

}
