package com.endava.contest.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.endava.contest.domain.BestBuy;
import com.endava.contest.domain.File;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TradingFacade implements Consumer<File> {

    private final ZipReader zipReader = new ZipReader();

    private final AtomicLong taskIdGenerator = new AtomicLong();

    private final BlockingQueue<BestBuyCalculateTask> submittedTasksQueue = new LinkedBlockingQueue<>();

    private volatile boolean isDone = false;

    public String getBestBuysFor(InputStream zipArchive) {
        zipReader.registerConsumer(this);
        zipReader.readZipArchive(zipArchive);
        BestBuyCalculateTask[] finishedTasks = spinUntilAllTasksAreDone();
        Arrays.sort(finishedTasks, Comparator.comparingLong(bestBuyCalculateTask -> bestBuyCalculateTask.id));
        return makeBestBuysResponse(finishedTasks);
    }

    private BestBuyCalculateTask[] spinUntilAllTasksAreDone() {
        while (!isDone) {
            Thread.yield();
        }
        int totalTasks = (int) taskIdGenerator.get();
        BestBuyCalculateTask[] finishedTasks = new BestBuyCalculateTask[totalTasks];
        int finishedTasksIndex = 0;
        for (int i = 0; i < totalTasks; ++i) {
            BestBuyCalculateTask queueElement = getQueueElement();
            if (queueElement != null)
                finishedTasks[finishedTasksIndex++] = queueElement;
        }
        return finishedTasks;
    }

    private BestBuyCalculateTask getQueueElement() {
        try {
            return submittedTasksQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    private String makeBestBuysResponse(final BestBuyCalculateTask[] results) {
        StringBuilder jsonResult = new StringBuilder(50_000);
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        for (BestBuyCalculateTask task : results) {
            BestBuy bestBuy = task.getResult();
            if (bestBuy != null) {
                objectNode.set(bestBuy.getBatchName(), JsonNodeFactory.instance.objectNode()
                        .put("buyPoint", bestBuy.getBuyPoint().toString())
                        .put("sellPoint", bestBuy.getSellPoint().toString()));
            }
        }
        jsonResult.append(objectNode.toString());
        return jsonResult.toString();
    }

    @Override
    public void accept(final File file) {
        if (file == null) {
            isDone = true;
        } else {
            ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
            forkJoinPool.submit(new BestBuyCalculateTask(taskIdGenerator.incrementAndGet(), file));
        }
    }

    private class BestBuyCalculateTask implements Runnable {

        private final long id;

        private final File file;

        private volatile BestBuy result;

        public BestBuyCalculateTask(final long id, final File file) {
            this.id = id;
            this.file = file;
        }

        public long getId() {
            return id;
        }

        public boolean isDone() {
            return result != null;
        }

        public BestBuy getResult() {
            return result;
        }

        @Override
        public void run() {
            processImageFile(file);
        }

        private void processImageFile(final File file) {
            try {
                tryProcessImageFile(file);
            } catch (Exception e) {
                //skipping file
            }
        }

        private void tryProcessImageFile(final File file) {
            String imageContent = QRCodeReader.decodeQRCode(file.getContentStream());
            double[] stocksPrices = StockParser.getStocks(imageContent);
            if (stocksPrices.length != 0) {
                result = BestBuyCalculator.calculateBestBuy(stocksPrices, file.getFileName());
                submittedTasksQueue.add(this);
            }
        }
    }
}
