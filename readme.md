```
Tips: 项目服务器部署地址为：http://8.138.83.207:8081
因此，在本文档后文以prefix代之，即请求URL格式为：prefix/api/xxx
```
# 目录

1\. 长短链接映射接口

---

**1\. 压缩链接**
###### 接口功能
> 将一个长URL压缩为一个8位（暂定）的短URL

###### URL
> [prefix/urlMapping/shorten/](prefix/urlMapping/shorten/)

###### 支持格式
> JSON

###### HTTP请求方式
> POST

###### 请求参数
```
    ShortenRequestDTO {
        String longUrl; // 长链接
    }
```

###### 返回字段
```
    Result {
        Boolean success; // 状态
        String errorMsg; // 错误信息
        Object data; // 返回的具体对象
        Long total; // 总数
    }
```

###### 接口调用示例
> 地址：[http://localhost:8081/urlMapping/shorten](http://localhost:8081/urlMapping/shorten)
``` 
// 接口使用POST + row JSON
{
    "longUrl": "www.bilibili.com"
}
```

##### 接口返回数据示例
```
{
    "success": true,
    "errorMsg": null,
    "data": {
        "shortUrl": "dSHU3PPX"
    },
    "total": null
}
```
---
**2\. 解析短链接**
###### 接口功能
> 将短URL解析为长URL并执行302重定向

###### URL
> [prefix/{shortUrl}](prefix/shortUrl)

###### 支持格式
> String

###### HTTP请求方式
> GET

###### 请求参数
```
    String shortUrl; // 短URL
```

###### 返回字段
```
    RedirectView 重定向

```

###### 接口调用示例
> 地址：[http://localhost:8081/urlMapping/dSHU3PPX](http://localhost:8081/urlMapping/dSHU3PPX)
``` 
// 接口使用GET
将短URL拼接在urlMapping后即可
```

##### 接口返回数据示例
```
{
    // 返回重定向的HTML文件（如哔哩哔哩的页面html）
}