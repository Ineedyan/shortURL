package com.example.shorturl.Service;

import com.example.shorturl.DTO.shortenRequestDTO;
import org.springframework.http.ResponseEntity;

public interface QRCodeService{

    ResponseEntity<byte[]> generateQRCode(shortenRequestDTO shortenRequestDTO);
}
