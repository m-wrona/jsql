package com.mwronski.jsql.parser;

import com.mwronski.jsql.model.SqlToken;

/**
 * Interface indicates that class parse SQL commands
 * 
 * @date 25-02-2013
 * @author Michal Wronski
 * 
 */
public interface SqlParser {

    /**
     * Get root of AST tree
     * 
     * @return
     */
    SqlToken getRoot();
}
