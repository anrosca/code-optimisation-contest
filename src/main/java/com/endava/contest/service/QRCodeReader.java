package com.endava.contest.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QRCodeReader {

    public String decodeQRCode(InputStream qrCodeImage) {
        try {
            return tryDecodeQRCode(qrCodeImage);
        } catch (NotFoundException | IOException e) {
            log.info("There is no QR code in the image or the image is invalid: {}", e);
            return "";
        }
    }

    private static String tryDecodeQRCode(InputStream qrCodeimage) throws IOException, NotFoundException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}