package com.endava.contest.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.endava.contest.domain.BestBuy;
import com.endava.contest.domain.File;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TradingFacade {

    private final ZipReader zipReader;

    private final QRCodeReader qrCodeReader;

    private final StockParser stockParser;

    private final BestBuyCalculator bestBuyCalculator;

    public List<BestBuy> getBestBuysFor(InputStream zipArchive) {
        List<File> files = zipReader.readZipArchive(zipArchive);
        List<BestBuy> bestBuys = new ArrayList<>(1000);
        for (File file : files) {
            processImageFile(bestBuys, file);
        }
        return bestBuys;
    }

    private void processImageFile(final List<BestBuy> bestBuys, final File file) {
        try {
            tryProcessImageFile(bestBuys, file);
        } catch (Exception e) {
            //skipping file
        }
    }

    private void tryProcessImageFile(final List<BestBuy> bestBuys, final File file) {
        String imageContent = qrCodeReader.decodeQRCode(file.getContentStream());
        double[] stocksPrices = stockParser.getStocks(imageContent);
        if (stocksPrices.length != 0) {
            addIfNotNull(bestBuys, bestBuyCalculator.calculateBestBuy(stocksPrices, file.getFileName()));
        }
    }

    private void addIfNotNull(final List<BestBuy> bestBuys, final BestBuy bestBuy) {
        if (bestBuy != null) {
            bestBuys.add(bestBuy);
        }
    }

    public String makeBestBuysResponse(List<BestBuy> bestBuys) {
        StringBuilder jsonResult = new StringBuilder();
        JSONObject jsonObject = new JSONObject();
        for (BestBuy bestBuy : bestBuys) {
            jsonObject.put(bestBuy.getBatchName(), new JSONObject()
                    .put("buyPoint", bestBuy.getBuyPoint().toString())
                    .put("sellPoint", bestBuy.getSellPoint().toString()));
        }
        jsonResult.append(jsonObject);
        return jsonResult.toString();
    }
}
