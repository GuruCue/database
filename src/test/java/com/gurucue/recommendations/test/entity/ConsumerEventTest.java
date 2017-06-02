/*
 * This file is part of Guru Cue Search & Recommendation Engine.
 * Copyright (C) 2017 Guru Cue Ltd.
 *
 * Guru Cue Search & Recommendation Engine is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * Guru Cue Search & Recommendation Engine is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Guru Cue Search & Recommendation Engine. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.gurucue.recommendations.test.entity;

import com.gurucue.recommendations.ResponseException;
import com.gurucue.recommendations.entity.ConsumerEvent;
import com.gurucue.recommendations.entity.DataType;
import junit.framework.TestCase;
import org.junit.Test;
import com.gurucue.recommendations.tokenizer.*;

import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Tests the JSON tokenizer.
 */
public class ConsumerEventTest extends TestCase {

    @Test
    public void testEquals(){
        final ConsumerEvent ce1 = new ConsumerEvent();
        final ConsumerEvent ce2 = new ConsumerEvent();

        assertNotNull("Newly initialized obj still null",ce1);
        assertEquals("Two consumerEvents initiated, but are not equal", ce1, ce2);
        assertFalse("Newly initiated ConsumerEvent equals null", ce1.equals(null));

        ce2.setRequestDuration(3000L);
        assertFalse("Changed ConsumerEvent equals old one", ce1.equals(ce2));
        ce1.setRequestDuration(3000L);
        assertEquals("ConsumerEvents not equal after both have been changed",ce1,ce2);

    }

    @Test
    public void testDataToJson(){
        final ConsumerEvent ce = new ConsumerEvent();
        final Map<DataType,String> testMap = new HashMap<>();
        final StringBuilder dataJson = new StringBuilder(100);
        final String testString = "test feedback\nMultipleLines\n and weird chars \" ' ž đ š č ć";
        testMap.put(new DataType(1L,DataType.FEEDBACK),testString);
        ce.setData(testMap);
        ce.getDataAsJsonString(dataJson);

        JSONTokenizer tokenizer = new JSONTokenizer(dataJson.toString());
        try {
            Token token = tokenizer.nextToken();
            assertTrue("The root token is not a JSONObjectToken", token instanceof JSONObjectToken);
            assertNull("The root token's name is not null", token.getName());
            JSONObjectToken rootToken = (JSONObjectToken)token;

            assertTrue("The root token indicates there is no first sub-token", rootToken.hasNext());
            token = rootToken.next();
            assertTrue("The \""+DataType.FEEDBACK+"\" sub-token is not a StringToken", token instanceof StringToken);
            StringToken stringToken = (StringToken)token;
            assertTrue("The \""+DataType.FEEDBACK+"\" string sub-token doesn't contain the string \""+testString+"\", instead it contains \"" + stringToken.asString() + "\"", testString.equals(stringToken.asString()));
            assertStructureEnd(rootToken);
            token = tokenizer.nextToken();
            assertNull("The tokenizer didn't finish after the root token finished, I got a token \"" + (null == token ? "null" : token.getName()) + "\" of class " + (null == token ? "null" : token.getClass().getCanonicalName()), token);
        }catch(ResponseException re){
            re.printStackTrace();
            throw new RuntimeException("A ResponseException was raised: " + re.toString(), re);
        }


    }

    private void assertStructureEnd(final StructuredToken structuredToken) {
        try {
            assertFalse("The \"" + structuredToken.getName() + "\" token doesn't finish correctly, the hasNext() indicates there exists a next element, while it shouldn't", structuredToken.hasNext());
        } catch (ResponseException e) {
            e.printStackTrace();
            fail("ResponseException while checking hasNext() for end of tree-token");
            return;
        }

        Token token;
        try {
            token = structuredToken.next();
        }
        catch (ResponseException e) {
            e.printStackTrace();
            fail("ResponseException while checking next() for end of tree-token");
            return;
        }
        catch (NoSuchElementException e) {
            return; // this is correct when we expect no more elements
        }
        assertNotNull("The \"" + structuredToken.getName() + "\" token doesn't finish correctly, it returns null instead of throwing a NoSuchElementException", token);
        fail("The \"" + structuredToken.getName() + "\" token doesn't finish correctly, I got a token \"" + token.getName() + "\" of class " + token.getClass().getCanonicalName() + " when there should be none more");
    }
}