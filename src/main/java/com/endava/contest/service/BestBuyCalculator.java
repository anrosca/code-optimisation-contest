package com.endava.contest.service;

import org.springframework.stereotype.Service;

import com.endava.contest.domain.BestBuy;

@Service
public class BestBuyCalculator {

    public BestBuy calculateBestBuy(final double[] stocksPrices, final String batchName) {
        DoublePair bestPrice = null;
        DoublePair candidate = new DoublePair();
        for (int i = 0; i < stocksPrices.length - 1; ++i) {
            for (int j = i + 2; j < stocksPrices.length; ++j) {
                if (Double.compare(stocksPrices[i], stocksPrices[j]) < 0) {
                    candidate.setFirst(stocksPrices[i]);
                    candidate.setSecond(stocksPrices[j]);
                    if (bestPrice == null || bestPrice.compareTo(candidate) < 0) {
                        bestPrice = candidate.clone();
                    }
                }
            }
        }
        return bestPrice != null ? new BestBuy(bestPrice.getFirst(), bestPrice.getSecond(), batchName) : null;
    }

    private static class DoublePair implements Cloneable, Comparable<DoublePair> {

        private double first;

        private double second;

        public DoublePair(final double first, final double second) {
            this.first = first;
            this.second = second;
        }

        public DoublePair() {
        }

        public double getFirst() {
            return first;
        }

        public double getSecond() {
            return second;
        }

        public void setFirst(final double first) {
            this.first = first;
        }

        public void setSecond(final double second) {
            this.second = second;
        }

        public DoublePair clone() {
            try {
                return (DoublePair) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int compareTo(final DoublePair other) {
            final double firstDiff = Math.abs(first - second);
            final double secondDiff = Math.abs(other.first - other.second);
            return Double.compare(firstDiff, secondDiff);
        }
    }
}