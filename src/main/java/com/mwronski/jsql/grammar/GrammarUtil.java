package com.mwronski.jsql.grammar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.mwronski.jsql.model.Table;
import com.mwronski.jsql.model.Variable;

/**
 * Util related with grammar and SQL tokens.
 * 
 * @date 28-06-2013
 * @author Michal Wronski
 * 
 */
public final class GrammarUtil {

    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String ALL = "*";
    public static final String DOT = ".";
    public static final String COUNT_ALL = "COUNT(*)";
    public static final String ORDER_BY = "ORDER BY";
    public static final String DESC = "DESC";
    public static final String GROUP_BY = "GROUP BY";
    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";
    public static final String PARAM = "?";
    public static final String IS_NULL = "IS NULL";
    public static final String IS_NOT_NULL = "IS NOT NULL";
    public static final String EQUALS = "=";
    public static final String NOT_EQUALS = "!=";
    public static final String LESS = "<";
    public static final String LESS_EQAULS = "<=";
    public static final String GREATER = ">";
    public static final String GREATER_EQUALS = ">=";

    private GrammarUtil() {
        // no instances
    }

    /**
     * Get name of given variable
     * 
     * @param variable
     * @param skipAnnotations
     *            flag indicates whether meta-data of variable should be skipped
     *            event if it's available
     * @return name from Column annotation if defined. If Column annotation is
     *         not defined name is taken from getter method name.
     */
    private static String getFieldName(final Variable variable, final boolean skipAnnotations) {
        Method method = variable.getMethod();
        Column columnAnnotation = method.getAnnotation(Column.class);
        if (!skipAnnotations && columnAnnotation != null && !columnAnnotation.name().isEmpty()) {
            return columnAnnotation.name().toLowerCase();
        }
        String fieldName = null;
        if (method.getName().startsWith("get")) {
            fieldName = method.getName().replace("get", "");
        } else if (method.getName().startsWith("is")) {
            fieldName = method.getName().replace("is", "");
        } else {
            fieldName = method.getName();
        }
        return normalizeSpelling(fieldName);
    }

    /**
     * Normalize spelling of string to meet JAVA conventions
     * 
     * @param string
     * @return
     */
    private static String normalizeSpelling(final String string) {
        String normalizedString = string;
        char firstLetterLower = Character.toLowerCase(normalizedString.charAt(0));
        String restString = string.length() > 1 ? normalizedString.substring(1) : "";
        return firstLetterLower + restString;
    }

    /**
     * Get variable name
     * 
     * @param variable
     * @param showTableName
     *            flag indicates whether table name should appear before
     *            variable name
     * @param skipAnnotations
     *            flag indicates whether meta-data of variable should be skipped
     *            event if it's available
     * @return
     */
    public static String getVariableName(final Variable variable, final boolean skipAnnotations,
            final boolean showTableName) {
        StringBuilder varName = new StringBuilder();
        if (variable.getTable().getAlias() != null) {
            varName.append(variable.getTable().getAlias()).append(DOT);
        } else if (showTableName) {
            varName.append(getTableName(variable.getTable())).append(DOT);
        }
        varName.append(getFieldName(variable, skipAnnotations));

        return varName.toString();
    }

    /**
     * Get table name.
     * 
     * 
     * @return name Entity annotation or class name if entity annotation is not
     *         defined
     */
    public static String getTableName(final Table table) {
        Entity entityAnno = table.getTableClass().getAnnotation(Entity.class);
        if (entityAnno != null && !entityAnno.name().isEmpty()) {
            return entityAnno.name();
        }
        return table.getTableClass().getSimpleName();
    }

    /**
     * Get name of table definition
     * 
     * @param table
     * @return table name with its alias if set
     */
    public static String getTableDefinitionName(final Table table) {
        StringBuilder tableName = new StringBuilder(getTableName(table));
        if (table.getAlias() != null) {
            tableName.append(SPACE).append(table.getAlias());
        }
        return tableName.toString();
    }

    /**
     * Normalize values in given collection. If element is an ENUM, convert it
     * to String
     * 
     * @param values
     * @return
     */
    public static List<Object> normalizeValues(final Collection<Object> values) {
        List<Object> normalizedValues = new ArrayList<Object>();
        for (Object value : values) {
            if (value instanceof Enum) {
                normalizedValues.add(value.toString());
            } else {
                normalizedValues.add(value);
            }
        }
        return normalizedValues;
    }

}
