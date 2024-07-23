package com.example.shorturl.Service.impl;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.example.shorturl.DTO.WithLongUrlRequestDTO;
import com.example.shorturl.Service.QRCodeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Override
    public ResponseEntity<byte[]> generateQRCode(WithLongUrlRequestDTO WithLongUrlRequestDTO) {
        try {
            // 获取url
            String url = WithLongUrlRequestDTO.getLongUrl();
            // 生成二维码
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            QrCodeUtil.generate(url, 250, 250, "png", stream);
            byte[] qrCodeImage = stream.toByteArray();
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);
            return ResponseEntity.ok().headers(headers).body(qrCodeImage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
