package com.endava.contest.service;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StockParserTest {

    StockParser parser = new StockParser();

    @Test
    public void shouldIgnoreNull() {
        double[] stocks = parser.getStocks(null);
        assertEquals(0, stocks.length);
    }

    @Test
    public void shouldIgnoreEmptyString() {
        double[] stocks = parser.getStocks("");
        assertEquals(0, stocks.length);
    }

    @Test
    public void shouldIgnoreNonNumericString() {
        double[] stocks = parser.getStocks("My name is Slim Shady");
        assertEquals(0, stocks.length);
    }

    @Test
    public void shouldIgnoreWhitespaceString() {
        double[] stocks = parser.getStocks("    \t\t\t      \r\r   \n");
        assertEquals(0, stocks.length);
    }

    @Test
    public void shouldIgnoreNegativeNumbers() {
        double[] stocks = parser.getStocks("-6.45");
        assertEquals(0, stocks.length);
    }

    @Test
    public void shouldIgnoreMultipleNegativeNumbers() {
        double[] stocks = parser.getStocks("6.45 11 2.3 -5 5.23");
        assertEquals(0, stocks.length);
    }

    @Test
    public void shouldReturnOneItemIfOneItemIsGiven() {
        double[] stocks = parser.getStocks("10.56");
        assertArrayEquals(new double[] { 10.56 }, stocks, 0.001);
    }

    @Test
    public void shouldReturnOneItemIfOneItemWithTrailingWhitespaceIsGiven() {
        double[] stocks = parser.getStocks("10.56   \t\t");
        assertArrayEquals(new double[] { 10.56 }, stocks, 0.001);
    }

    @Test
    public void shouldReturnTwoItemsIfTwoItemsAreGiven() {
        double[] stocks = parser.getStocks("10.56 9.23");
        assertArrayEquals(new double[] { 10.56, 9.23 }, stocks, 0.001);
    }

    @Test
    public void shouldReturnTwoItemsIfTwoItemsAreGivenIntegers() {
        double[] stocks = parser.getStocks("10 9.23");
        assertArrayEquals(new double[] { 10, 9.23 }, stocks, 0.001);
    }

    @Test
    public void shouldReturnThreeItemsIfThreeItemsAreGiven() {
        double[] stocks = parser.getStocks("10 12.6 9.23");
        assertArrayEquals(new double[] { 10, 12.6, 9.23 }, stocks, 0.001);
    }
}
