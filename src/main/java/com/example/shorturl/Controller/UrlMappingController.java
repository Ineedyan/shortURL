package com.example.shorturl.Controller;

import com.example.shorturl.DTO.WithLongUrlRequestDTO;
import com.example.shorturl.Service.UrlMappingService;
import com.example.shorturl.Utils.Result;

import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;


@RestController
@RequestMapping("/url")
public class UrlMappingController {

    @Resource
    private UrlMappingService urlMappingService;

    /**
     * 缩短链接
     * @param WithLongUrlRequestDTO 请求体
     * @return 短链接
     */
    @PostMapping("shorten")
    public Result shortenUrl(@RequestBody WithLongUrlRequestDTO WithLongUrlRequestDTO){
        return urlMappingService.shortenUrl(WithLongUrlRequestDTO);
    }

    /**
     * 解析短链接
     * @param shortUrl 短链接
     * @return 原链接信息
     */
    @GetMapping ("re/{shortUrl}")
    public ResponseEntity<?> resolveUrl(@PathVariable(name="shortUrl") String shortUrl){
        String longUrl = urlMappingService.resolveUrl(shortUrl);
        if (longUrl == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.fail("短链接无效或已过期"));
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
    }

}
