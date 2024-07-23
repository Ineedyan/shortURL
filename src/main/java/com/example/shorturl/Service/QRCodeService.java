package com.example.shorturl.Service;

import com.example.shorturl.DTO.WithLongUrlRequestDTO;
import org.springframework.http.ResponseEntity;

public interface QRCodeService{

    ResponseEntity<byte[]> generateQRCode(WithLongUrlRequestDTO WithLongUrlRequestDTO);
}
