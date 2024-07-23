package com.example.shorturl.Controller;

import com.example.shorturl.DTO.WithLongUrlRequestDTO;
import com.example.shorturl.Service.QRCodeService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/qrcode")
public class QRCodeController {

    @Resource
    private QRCodeService qrCodeService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateQRCode(@RequestBody WithLongUrlRequestDTO WithLongUrlRequestDTO){
        return qrCodeService.generateQRCode(WithLongUrlRequestDTO);
    }

}
