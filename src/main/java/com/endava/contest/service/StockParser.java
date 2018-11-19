package com.endava.contest.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class StockParser {

    public double[] getStocks(String stockPrices) {
        try {
            if (stockPrices == null || stockPrices.trim().isEmpty()) {
                return new double[0];
            }
            Map<String, Double> doubleCache = new HashMap<>();
            stockPrices = stockPrices.trim();
            String[] items = stockPrices.split(" ");
            double[] stocks = new double[items.length];
            int i = 0;
            for (String price : items) {
                if (doubleCache.containsKey(price)) {
                    stocks[i] = doubleCache.get(price);
                } else {
                    stocks[i] = Double.parseDouble(price);
                    doubleCache.put(price, stocks[i]);
                }
                if (stocks[i] < 0) {
                    return new double[0];
                }
                ++i;
            }
            return stocks;
        } catch (NumberFormatException e) {
            return new double[0];
        }
    }
}
