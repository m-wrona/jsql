package com.mwronski.jsql.grammar.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mwronski.jsql.builder.SqlSelectBuilder;
import com.mwronski.jsql.grammar.GrammarUtil;
import com.mwronski.jsql.model.Table;
import com.mwronski.jsql.model.Variable;
import com.mwronski.jsql.model.dql.JoinStatement;
import com.mwronski.jsql.model.dql.SelectStatement;
import com.mwronski.jsql.model.expressions.InExpression;
import com.mwronski.jsql.model.expressions.Expression;
import com.mwronski.jsql.model.expressions.ExpressionChain;
import com.mwronski.jsql.model.expressions.Relation;
import com.mwronski.jsql.model.expressions.Relation.RelationType;

/**
 * Basic SELECT statement builder common for all grammars
 * 
 * @date 05-07-2013
 * @author Michal Wronski
 * 
 */
public abstract class SQLSelectBuilder implements SqlSelectBuilder {

    protected final StringBuilder sqlColumns = new StringBuilder();
    protected final StringBuilder sqlTables = new StringBuilder();
    protected final StringBuilder sqlWhere = new StringBuilder();
    protected final StringBuilder sqlOrderBy = new StringBuilder();
    protected final StringBuilder sqlGroupBy = new StringBuilder();
    protected final StringBuilder sqlJoins = new StringBuilder();
    /**
     * Set SQL parameters in SELECT statement
     */
    private final Map<Integer, Object> params = new HashMap<Integer, Object>();
    /**
     * Index of current SQL parameter
     */
    private int paramIndex = 1;

    @Override
    public String asSQL() {
        StringBuilder select = new StringBuilder();
        select.append(Nouns.SELECT).append(GrammarUtil.SPACE).append(sqlColumns);
        select.append(GrammarUtil.SPACE);
        select.append(Nouns.FROM).append(GrammarUtil.SPACE).append(sqlTables);
        select.append(sqlJoins);
        if (sqlWhere.length() > 0) {
            select.append(GrammarUtil.SPACE);
            select.append(Nouns.WHERE).append(GrammarUtil.SPACE).append(sqlWhere);
        }
        if (sqlOrderBy.length() > 0) {
            select.append(GrammarUtil.SPACE);
            select.append(GrammarUtil.ORDER_BY).append(GrammarUtil.SPACE).append(sqlOrderBy);
        }
        if (sqlGroupBy.length() > 0) {
            select.append(GrammarUtil.SPACE);
            select.append(GrammarUtil.GROUP_BY).append(GrammarUtil.SPACE).append(sqlGroupBy);
        }
        return select.toString();
    }

    @Override
    public final Map<Integer, Object> getSQLParams() {
        return params;
    }

    @Override
    public final void handleSelect(List<Table> tables, final List<Variable> selectColumns, final boolean distinct,
            final boolean count) {
        // append all columns from tables
        if (selectColumns.isEmpty() && !count) {
            // select all without count
            for (Table table : tables) {
                appendSelectAllFromTable(sqlColumns, table);
            }
        }
        // append columns
        for (Variable column : selectColumns) {
            appendElementBreak(sqlColumns);
            sqlColumns.append(getVariableName(column));
        }
        // append distinct and count
        if (distinct && sqlColumns.length() > 0) {
            sqlColumns.insert(0, Nouns.DISTINCT + GrammarUtil.SPACE);
        }
        if (count) {
            appendElementBreak(sqlColumns);
            sqlColumns.append(GrammarUtil.COUNT_ALL);
        }
    }

    /**
     * Get name of the variable
     * 
     * @param var
     * @return
     */
    protected String getVariableName(Variable var) {
        return GrammarUtil.getVariableName(var, false, false);
    }

    @Override
    public final void handleFrom(final List<Table> tables) {
        for (Table table : tables) {
            // append table name
            appendElementBreak(sqlTables);
            sqlTables.append(getTableDefinitionName(table));
        }
    }

    /**
     * Append select all from table SQL clause
     * 
     * @param sql
     * @param table
     *            from which all columns will be taken
     */
    protected void appendSelectAllFromTable(final StringBuilder sql, final Table table) {
        appendElementBreak(sql);
        sql.append(getTableName(table)).append(GrammarUtil.DOT).append(GrammarUtil.ALL);
    }

    /**
     * Get name of table definition that can be used in FROM or JOIN clause
     * 
     * @param table
     * @return
     */
    protected String getTableDefinitionName(Table table) {
        return GrammarUtil.getTableDefinitionName(table);
    }

    /**
     * Get name of the table
     * 
     * @param table
     * @return
     */
    protected String getTableName(Table table) {
        if (table.getAlias() != null) {
            return table.getAlias();
        }
        return GrammarUtil.getTableName(table);
    }

    /**
     * Append break between elements
     * 
     * @param string
     */
    protected final void appendElementBreak(final StringBuilder string) {
        if (string.length() > 0) {
            string.append(GrammarUtil.COMMA).append(GrammarUtil.SPACE);
        }
    }

    @Override
    public void handleJoin(final Table joinedTable, JoinStatement.Direction direction, JoinStatement.Type type,
            ExpressionChain onCondition) {
        if (direction != JoinStatement.Direction.NONE) {
            sqlJoins.append(GrammarUtil.SPACE).append(direction);
        }
        if (type != JoinStatement.Type.NONE) {
            sqlJoins.append(GrammarUtil.SPACE).append(type);
        }
        sqlJoins.append(GrammarUtil.SPACE).append(Nouns.JOIN);
        sqlJoins.append(GrammarUtil.SPACE).append(getTableDefinitionName(joinedTable));
        sqlJoins.append(GrammarUtil.SPACE).append(Nouns.ON);
        appendConditions(sqlJoins, onCondition);
    }

    @Override
    public final void handleWhere(final ExpressionChain where) {
        appendConditions(sqlWhere, where);
    }

    /**
     * Append conditions into given SQL condition
     * 
     * @param sqlCondition
     * @param condition
     */
    protected final void appendConditions(final StringBuilder sqlCondition, final ExpressionChain condition) {
        boolean first = true;
        for (Map.Entry<Expression, ExpressionChain.Type> entry : condition.getConditions().entrySet()) {
            Expression token = entry.getKey();
            if (token.isNull() && token.isNullOmittable()) {
                continue;
            }
            if (sqlCondition.length() > 0) {
                sqlCondition.append(GrammarUtil.SPACE);
            }
            if (!first) {
                sqlCondition.append(entry.getValue()).append(GrammarUtil.SPACE);
            }
            if (token instanceof ExpressionChain) {
                StringBuilder subCondition = new StringBuilder();
                appendConditions(subCondition, (ExpressionChain) token);
                sqlCondition.append(GrammarUtil.LEFT_BRACKET).append(subCondition).append(GrammarUtil.RIGHT_BRACKET);
            } else if (token instanceof Relation) {
                appendRelation(sqlCondition, (Relation) token);
            } else if (token instanceof InExpression) {
                appendInExpression(sqlCondition, (InExpression) token);
            } else {
                throw new UnsupportedOperationException("Unknown condition type: " + token.getClass());
            }
            first = false;
        }

    }

    /**
     * Append condition with collection into given SQL condition
     * 
     * @param sqlCondition
     * @param inExpression
     */
    private void appendInExpression(final StringBuilder sqlCondition, final InExpression inExpression) {
        sqlCondition.append(getVariableName(inExpression.getVar()));
        sqlCondition.append(GrammarUtil.SPACE);
        if (inExpression.getValues() != null) {
            sqlCondition.append(inExpression.getType());
            sqlCondition.append(GrammarUtil.SPACE);
            sqlCondition.append(GrammarUtil.LEFT_BRACKET);
            int paramIndex = getUniqueParamIndex();
            params.put(paramIndex, inExpression.getValues());
            sqlCondition.append(GrammarUtil.PARAM + paramIndex);
            sqlCondition.append(GrammarUtil.RIGHT_BRACKET);
        } else {
            switch (inExpression.getType()) {
            case IN:
                sqlCondition.append(GrammarUtil.IS_NULL);
                break;
            case NOT_IN:
                sqlCondition.append(GrammarUtil.IS_NOT_NULL);
            }
        }
    }

    /**
     * Append relation into given SQL condition
     * 
     * @param sqlCondition
     * @param relation
     */
    private void appendRelation(final StringBuilder sqlCondition, final Relation relation) {
        if (relation.shouldBeOmitted()) {
            // skip token
            return;
        }
        appendVariable(sqlCondition, relation.getVar(), relation.isCaseInsensitive());
        // relation mark
        appendRelationMark(sqlCondition, relation.getType(), relation.getValue() != null
                || relation.getVarValue() != null);
        // right side
        if (relation.hasVarValue()) {
            appendVariable(sqlCondition, relation.getVarValue(), relation.isCaseInsensitive());
        } else if (relation.getValue() != null) {
            appendValue(sqlCondition, relation.getValue(), relation.isCaseInsensitive());
        }
    }

    /**
     * Append value into given SQL statement
     * 
     * @param sql
     * @param value
     * @param caseInsensitive
     */
    private void appendValue(final StringBuilder sql, final Object value, final boolean caseInsensitive) {
        int paramIndex = getUniqueParamIndex();
        if (caseInsensitive) {
            params.put(paramIndex, value.toString().toLowerCase());
        } else {
            params.put(paramIndex, value);
        }
        sql.append(GrammarUtil.PARAM + paramIndex);
    }

    /**
     * Append variable into given SQL statement
     * 
     * @param sql
     * @param var
     * @param caseInsensitive
     */
    private void appendVariable(final StringBuilder sql, final Variable var, final boolean caseInsensitive) {
        if (caseInsensitive) {
            sql.append(toLowerString(getVariableName(var)));
        } else {
            sql.append(getVariableName(var));
        }
    }

    /**
     * Wrap string with function that will change string to lower case string
     * 
     * @param string
     * @return
     */
    protected abstract String toLowerString(final String string);

    private void appendRelationMark(final StringBuilder sql, final RelationType type, final boolean valueNotNull) {
        switch (type) {
        case EQ:
            if (valueNotNull) {
                sql.append(GrammarUtil.EQUALS);
            } else {
                sql.append(GrammarUtil.SPACE).append(GrammarUtil.IS_NULL);
            }
            break;
        case NEQ:
            if (valueNotNull) {
                sql.append(GrammarUtil.NOT_EQUALS);
            } else {
                sql.append(GrammarUtil.SPACE).append(GrammarUtil.IS_NOT_NULL);
            }
            break;
        case LT:
            sql.append(GrammarUtil.LESS);
            break;
        case EL:
            sql.append(GrammarUtil.LESS_EQAULS);
            break;
        case GT:
            sql.append(GrammarUtil.GREATER);
            break;
        case EG:
            sql.append(GrammarUtil.GREATER_EQUALS);
            break;
        case REGEX:
            sql.append(getRegExpWildcardMark());
            break;
        default:
            throw new UnsupportedOperationException("Unknwon type of relation: " + type);
        }
    }

    /**
     * Generates index for query parameter. Incremented number indexes are used.
     * 
     * @return
     */
    private int getUniqueParamIndex() {
        return paramIndex++;
    }

    /**
     * Get wild-card for regular expressions
     * 
     * @return
     */
    protected abstract String getRegExpWildcardMark();

    @Override
    public final void handleOrderBy(Map<Variable, Boolean> variables) {
        for (Map.Entry<Variable, Boolean> var : variables.entrySet()) {
            appendElementBreak(sqlOrderBy);
            sqlOrderBy.append(getVariableName(var.getKey()));
            if (var.getValue().equals(SelectStatement.DESC)) {
                sqlOrderBy.append(GrammarUtil.SPACE).append(GrammarUtil.DESC);
            }
        }
    }

    @Override
    public final void handleGroupBy(final List<Variable> variables) {
        for (Variable var : variables) {
            appendElementBreak(sqlGroupBy);
            sqlGroupBy.append(getVariableName(var));
        }
    }

}
