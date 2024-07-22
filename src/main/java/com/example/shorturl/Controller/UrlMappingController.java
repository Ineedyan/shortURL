package com.example.shorturl.Controller;

import com.example.shorturl.DTO.ShortenRequestDTO;
import com.example.shorturl.DTO.ShortenResponseDTO;
import com.example.shorturl.Service.UrlMappingService;
import com.example.shorturl.Utils.Result;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;


@RestController
@RequestMapping("/urlMapping")
public class UrlMappingController {

    @Resource
    private UrlMappingService urlMappingService;

    /**
     * 缩短链接
     * @param shortenRequestDTO 源URL链接
     * @return 短链接
     */
    @PostMapping("shorten")
    public Result shortenUrl(@RequestBody ShortenRequestDTO shortenRequestDTO){
        return urlMappingService.shortenUrl(shortenRequestDTO);
    }

    /**
     * 解析短链接
     * @param shortUrl 短链接
     * @return 原链接信息
     */
    @GetMapping ("/{shortUrl}")
    public RedirectView resolveUrl(@PathVariable(name="shortUrl") String shortUrl){
        String longUrl = urlMappingService.resolveUrl(shortUrl);
        RedirectView redirectView = new RedirectView("https://" + longUrl);
        // 设置302重定向
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

}
