/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;

/**
 * Escapes and unescapes {@link String}s for Java, Java Script, HTML and XML.
 *
 * <p>
 * #ThreadSafe#</p>
 *
 * @since 2.0
 * @deprecated As of 3.6, use Apache Commons Text
 * <a href="https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringEscapeUtils.html">
 * StringEscapeUtils</a> instead
 */
@Deprecated
public class StringEscapeUtils {

    /* UNESCAPE TRANSLATORS */
    /**
     * Translator object for unescaping escaped HTML 4.0.
     *
     * While {@link #unescapeHtml4(String)} is the expected method of use, this
     * object allows the HTML unescaping functionality to be used as the
     * foundation for a custom translator.
     *
     * @since 3.0
     */
    public static final CharSequenceTranslator UNESCAPE_HTML4
            = new AggregateTranslator(
                    new LookupTranslator(EntityArrays.BASIC_UNESCAPE()),
                    new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()),
                    new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE()),
                    new NumericEntityUnescaper()
            );

    /**
     * Unescapes a string containing entity escapes to a string containing the
     * actual Unicode characters corresponding to the escapes. Supports HTML 4.0
     * entities.
     *
     * <p>
     * For example, the string {@code "&lt;Fran&ccedil;ais&gt;"} will become
     * {@code "<Français>"}</p>
     *
     * <p>
     * If an entity is unrecognized, it is left alone, and inserted verbatim
     * into the result string. e.g. {@code "&gt;&zzzz;x"} will become
     * {@code ">&zzzz;x"}.</p>
     *
     * @param input the {@link String} to unescape, may be null
     * @return a new unescaped {@link String}, {@code null} if null string input
     *
     * @since 3.0
     */
    public static final String unescapeHtml4(final String input) {
        return UNESCAPE_HTML4.translate(input);
    }

    /**
     * {@link StringEscapeUtils} instances should NOT be constructed in standard
     * programming.
     *
     * <p>
     * Instead, the class should be used as:</p>
     * <pre>StringEscapeUtils.escapeJava("foo");</pre>
     *
     * <p>
     * This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     *
     * @deprecated TODO Make private in 4.0.
     */
    @Deprecated
    public StringEscapeUtils() {
        // empty
    }

}
