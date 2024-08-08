package com.example.shorturl.Controller;

import com.example.shorturl.DTO.WithLongUrlRequestDTO;
import com.example.shorturl.Service.UrlMappingService;
import com.example.shorturl.Utils.Result;

import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;



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
    @GetMapping ("/{shortUrl}")
    public RedirectView resolveUrl(@PathVariable(name="shortUrl") String shortUrl){
        String longUrl = urlMappingService.resolveUrl(shortUrl);
        // 拼接url
        RedirectView redirectView = new RedirectView(longUrl);
        // 设置302重定向
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

}
