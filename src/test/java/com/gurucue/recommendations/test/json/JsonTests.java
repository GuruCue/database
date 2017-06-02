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
package com.gurucue.recommendations.test.json;

import com.gurucue.recommendations.json.Json;
import com.gurucue.recommendations.json.JsonDouble;
import com.gurucue.recommendations.json.JsonLong;
import com.gurucue.recommendations.json.JsonParser;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests the JSON parser implementation.
 */
public class JsonTests {

    @Test
    public void testLongPositive() {
        final JsonLong expected = new JsonLong(42L);
        final Json actual = JsonParser.parse("42");
        Assert.assertEquals("Parsing of a positive Long value is not correct", expected, actual);
    }

    @Test
    public void testLongNegative() {
        final JsonLong expected = new JsonLong(-42L);
        final Json actual = JsonParser.parse("-42");
        Assert.assertEquals("Parsing of a negative Long value is not correct", expected, actual);
    }

    @Test
    public void testDoublePositive() {
        final JsonDouble expected = new JsonDouble(12.34);
        final Json actual = JsonParser.parse("12.34");
        Assert.assertEquals("Parsing of a positive Double value is not correct", expected, actual);
    }

    @Test
    public void testDoubleNegative() {
        final JsonDouble expected = new JsonDouble(-12.34);
        final Json actual = JsonParser.parse("-12.34");
        Assert.assertEquals("Parsing of a negative Double value is not correct", expected, actual);
    }

    @Test
    public void testScientificPositivePositiveExponent() {
        final JsonDouble expected = new JsonDouble(12.34e+1);
        final Json actual = JsonParser.parse("12.34e+1");
        Assert.assertEquals("Parsing of a positive scientific value with a positive exponent is not correct", expected, actual);
    }

    @Test
    public void testScientificPositiveNegativeExponent() {
        final JsonDouble expected = new JsonDouble(12.34e-1);
        final Json actual = JsonParser.parse("12.34e-1");
        Assert.assertEquals("Parsing of a positive scientific value with a negative exponent is not correct", expected, actual);
    }
}
