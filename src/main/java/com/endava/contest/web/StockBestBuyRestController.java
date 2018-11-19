package com.endava.contest.web;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.endava.contest.service.TradingFacade;

@RestController
@RequestMapping("/stockExchange")
public class StockBestBuyRestController {

    @PostMapping
    public ResponseEntity handle(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        if (multipartFile == null) {
            return ResponseEntity.badRequest()
                    .build();
        }
        InputStream inputStream = multipartFile.getInputStream();
        return ResponseEntity.ok()
                .body(getTradingFacade().getBestBuysFor(inputStream));
    }

    private TradingFacade getTradingFacade() {
        return new TradingFacade();
    }
}