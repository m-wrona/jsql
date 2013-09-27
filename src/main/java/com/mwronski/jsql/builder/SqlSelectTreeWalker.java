package com.mwronski.jsql.builder;

import static com.mwronski.jsql.builder.SqlTreeUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mwronski.jsql.model.*;
import com.mwronski.jsql.model.Noun.Nouns;
import com.mwronski.jsql.model.expressions.Order;

/**
 * Walker visiting tokens in SELECT statement and pass them to given
 * {@link SqlSelectBuilder}
 * 
 * @see SqlSelectBuilder
 * @date 05-07-2013
 * @author Michal Wronski
 * 
 */
public final class SqlSelectTreeWalker {

    public void walk(final SqlToken selectToken, final SqlSelectBuilder selectBuilder) {
        Map<Nouns, List<Noun>> nouns = groupByNouns(selectToken, Nouns.SELECT, Nouns.FROM, Nouns.JOIN, Nouns.WHERE,
                Nouns.ORDER_BY, Nouns.GROUP_BY);
        walkSelect(selectBuilder, getFirst(nouns.get(Nouns.SELECT)));
        walkFrom(selectBuilder, getFirst(nouns.get(Nouns.FROM)));
        walkJoin(selectBuilder, nouns.get(Nouns.JOIN));
        walkWhere(selectBuilder, getFirst(nouns.get(Nouns.WHERE)));
        walkOrderBy(selectBuilder, getFirst(nouns.get(Nouns.ORDER_BY)));
        walkGroupBy(selectBuilder, getFirst(nouns.get(Nouns.GROUP_BY)));
    }

    private void walkSelect(final SqlSelectBuilder selectBuilder, final Noun select) {
        int fromIndex = indexOf(select.getChildren(), Nouns.FROM);
        List<Variable> columns = new ArrayList<Variable>();
        List<Table> tables = new ArrayList<Table>();
        if (fromIndex > 0) {
            for (SqlToken token : select.getChildren().subList(0, fromIndex)) {
                if (token instanceof Variable) {
                    columns.add((Variable) token);
                } else if (token instanceof Table) {
                    tables.add((Table) token);
                }
            }
        }
        selectBuilder.handleSelect(tables, columns, indexOf(select.getChildren(), Nouns.DISTINCT) != -1,
                indexOf(select.getChildren(), Nouns.COUNT) != -1);
    }

    private void walkFrom(final SqlSelectBuilder selectBuilder, final Noun from) {
        List<Table> tables = new ArrayList<Table>();
        for (SqlToken table : from) {
            tables.add((Table) table);
        }
        selectBuilder.handleFrom(tables);
    }

    private void walkJoin(final SqlSelectBuilder selectBuilder, final List<Noun> joins) {
        if (joins == null) {
            return;
        }
        for (Noun join : joins) {
            Table table = find(join.getChildren(), Table.class).get(0);
            boolean isLeft = indexOf(join.getChildren(), Nouns.LEFT) != -1;
            boolean isRight = indexOf(join.getChildren(), Nouns.RIGHT) != -1;
            Boolean leftJoin = (!isLeft && !isRight) ? null : isLeft;
            boolean isOuter = indexOf(join.getChildren(), Nouns.OUTER) != -1;
            boolean isInner = indexOf(join.getChildren(), Nouns.INNER) != -1;
            Boolean innerJoin = (!isInner && !isOuter) ? null : isInner;
            int onIndex = indexOf(join.getChildren(), Nouns.ON);
            SqlToken onCondition = join.getChildren().subList(onIndex + 1, join.getChildren().size()).get(0);
            selectBuilder.handleJoin(table, leftJoin, innerJoin, onCondition);
        }
    }

    private void walkWhere(final SqlSelectBuilder selectBuilder, final Noun where) {
        if (where == null) {
            return;
        }
        selectBuilder.handleWhere(where.getChildren().get(0));
    }

    private void walkOrderBy(final SqlSelectBuilder selectBuilder, final Noun orderBy) {
        if (orderBy == null) {
            return;
        }
        Order order = (Order) orderBy.getChildren().get(0);
        selectBuilder.handleOrderBy(order.getVars());
    }

    private void walkGroupBy(final SqlSelectBuilder selectBuilder, final Noun groupBy) {
        if (groupBy == null) {
            return;
        }
        Columns columns = (Columns) groupBy.getChildren().get(0);
        selectBuilder.handleGroupBy(columns.getColumns());
    }

}
