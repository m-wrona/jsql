package com.mwronski.jsql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mwronski.jsql.model.Noun;
import com.mwronski.jsql.model.Noun.Nouns;
import com.mwronski.jsql.model.SqlToken;

/**
 * Generic stuff related with AST tree handling
 * 
 * @date 05-07-2013
 * @author Michal Wronski
 * 
 */
final class SqlTreeUtil {

    private SqlTreeUtil() {
        // no instances
    }

    /**
     * Group nouns in given token and its children
     * 
     * @param root
     * @param nouns
     * @return
     */
    static Map<Nouns, List<Noun>> groupByNouns(final SqlToken root, final Nouns... nouns) {
        Map<Nouns, List<Noun>> groupNouns = new LinkedHashMap<Nouns, List<Noun>>();
        Set<Nouns> wantedNouns = new HashSet<Noun.Nouns>(Arrays.asList(nouns));
        groupNounToken(groupNouns, root, wantedNouns);
        for (SqlToken child : root) {
            groupNounToken(groupNouns, child, wantedNouns);
        }
        return groupNouns;
    }

    /**
     * Group wanted nouns and save in the given map
     * 
     * @param groupNouns
     * @param token
     * @param wantedNouns
     */
    private static void groupNounToken(final Map<Nouns, List<Noun>> groupNouns, final SqlToken token,
            final Set<Nouns> wantedNouns) {
        if (!(token instanceof Noun)) {
            return;
        }
        Noun noun = (Noun) token;
        if (!wantedNouns.contains(noun.getNoun())) {
            return;
        }
        List<Noun> nouns = groupNouns.get(noun.getNoun());
        if (nouns == null) {
            nouns = new ArrayList<Noun>();
            groupNouns.put(noun.getNoun(), nouns);
        }
        nouns.add(noun);
    }

    /**
     * Get first token from the list
     * 
     * @param tokens
     * @return token token or null if tokens are null
     * @throws RuntimeException
     *             when tokens are set but don't have single element inside
     */
    static <T extends SqlToken> T getFirst(final List<T> tokens) {
        if (tokens == null) {
            return null;
        } else if (tokens.size() != 1) {
            throw new RuntimeException("Got more than one element - size: " + tokens.size());
        }
        return tokens.get(0);
    }

    /**
     * Get index of given noun in tokens
     * 
     * @param tokens
     * @param noun
     * @return index of noun or -1 if noun was not found
     */
    static int indexOf(final List<SqlToken> tokens, final Nouns noun) {
        int indexOf = 0;
        for (SqlToken token : tokens) {
            if (token instanceof Noun && ((Noun) token).getNoun() == noun) {
                return indexOf;
            }
            indexOf++;
        }
        return -1;
    }

    /**
     * Find elements of given class
     * 
     * @param elements
     * @param clazz
     * @return not null list of found elements
     */
    @SuppressWarnings("unchecked")
    static <T, E> List<T> find(final List<E> elements, final Class<T> clazz) {
        List<T> foundElements = new ArrayList<T>();
        for (E element : elements) {
            if (clazz.isInstance(element)) {
                foundElements.add((T) element);
            }
        }
        return foundElements;
    }
}
