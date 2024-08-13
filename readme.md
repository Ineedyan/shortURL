```
Tips: 在本文档后文以省略服务器地址与标识如http://localhost:8081/，
    实际请求URL格式为：(prefix)/api/xxx
```
# 目录
- [目录](#目录)
- [一. 项目部署环境](#一-项目部署环境)
- [二. 项目接口](#二-项目接口)
  - [1. 长短连接映射接口(UrlMapping)](#1-长短连接映射接口urlmapping)
    - [压缩链接](#压缩链接)
    - [解析短链接](#解析短链接)
    
  - [2. 生成QRCode二维码](#2-生成qrcode二维码)
    - [二维码生成](#二维码生成)
        
  - [3. 用户信息操作接口](#3-用户信息操作接口)
    - [验证码发送](#验证码发送)
          
    - [用户登录](#用户登录)
          
          

# 一. 项目部署环境

```
部署环境：
    项目服务器部署地址：http://8.138.83.207:8081
    MySQL: http://8.138.83.207:3306
        -us: root
        -pw: zgb159632
    Redis: http://8.138.83.207:6379
        -pw: zgb159632
本地测试环境：
    服务器地址：http://localhost:8081
    MySQL: http://localhost:3306
    Redis: 192.168.130.58:6379 (虚拟机)
```

<br>


# 二. 项目接口

## 1. 长短连接映射接口(UrlMapping)


### 压缩链接
###### 接口功能
> 将一个长URL压缩为一个6位的短URL

###### URL
> [prefix/url/shorten/](prefix/url/shorten/)

###### 支持格式
> JSON

###### HTTP请求方式
> POST

###### 请求参数
```
    shortenRequestDTO {
        String prefixType; // 前缀类型，如http  |  https
        String longUrl; // 长链接
        int days;  // 有效期
    }
    Tips：前端请求form表单只需要直接传入对应键值对，无需额外包裹，如:
    {
        "prefixType" : "xxx",
        ...
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
// JSON
{
    "prefixType": "https", 
    "longUrl": "www.bilibili.com"
}
```

###### 接口返回数据示例
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
### 解析短链接
###### 接口功能
> 将短URL解析为长URL并执行302重定向

###### URL
> [prefix/urlMapping/{shortUrl}](prefix/urlMapping/shortUrl)

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

###### 接口返回数据示例
```
{
    // 返回重定向的HTML文件（如哔哩哔哩的页面html）
}
```

<br>

## 2. 生成QRCode二维码
### 二维码生成
###### 接口功能
> 将长URL解析为二维码

###### URL
> [prefix/qrcode/generate](prefix/qrcode/generate)

###### 支持格式
> JSON

###### HTTP请求方式
> POST

###### 请求参数
```
    shortenRequestDTO {
        String prefixType; // 前缀类型，如http  |  https
        String longUrl; // 长链接
    }
```

###### 返回字段
```
    ResponseEntity<byte[]>
    
    // 返回PNG格式的二维码
    QrCodeUtil.generate(url, 250, 250, "png", stream);
    byte[] qrCodeImage = stream.toByteArray();
    // 设置响应头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_PNG);
    headers.setContentLength(qrCodeImage.length);
    return ResponseEntity.ok().headers(headers).body(qrCodeImage);
    
```
###### 接口调用示例
> 地址：[http://localhost:8081/qrcode/generate](http://localhost:8081/qrcode/generate)
``` 
// JSON
{
    "prefixType": "https",
    "longUrl": "www.bilibili.com"
}
```
###### 接口返回数据示例
```
{
    // PNG格式的二维码
    
    // 前端参考处理方式
    <img id="qrCodeImage" src="" alt="QR Code will appear here">
    // 在生成二维码的回调函数中
    document.getElementById('qrCodeImage').src = url;
}

```
<br>


## 3. 用户信息操作接口

### 验证码发送
###### 接口功能
> 发送验证码

###### URL
> [prefix/user/sendCode](prefix/user/sendCode)

###### 支持格式
> String

###### HTTP请求方式
> GET

###### 请求参数
```
    username: 用户名/邮箱
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
> [http://localhost:8081/user/sendCode?username=xxx](http://localhost:8081/user/sendCode)


###### 接口返回数据示例
```
{
    "success": true,
    "errorMsg": null,
    "data": "发送验证码成功，请前往邮箱查收",
    "total": null
}
```

---

### 用户登录
###### 接口功能
> 用户身份验证，并在返回字段中返回token值，请查看返回字段描述与样例。后续用户请求的请求头（request-header）中都需要携带此token，字段名为"auth-token"，值为token

###### URL
> [prefix/user/login](prefix/user/login)

###### 支持格式
> JSON

###### HTTP请求方式
> POST

###### 请求参数
```
    loginFormDTO {
        Byte loginType; // 登录类型，值0为邮箱+验证码，值1为邮箱+密码
        String username; // 用户名 / 邮箱
        String code;  // 验证码
        String password; // 密码
    }
    Tips：前端请求form表单只需要直接传入对应键值对，无需额外包裹，如:
    {
        "loginType" : "0",
        ...
    }
```

###### 返回字段
```
    // data中包含token的值，登录成功后，返回一个token，前端请求需要携带token
    Result {
        Boolean success; // 状态
        String errorMsg; // 错误信息
        Object data; // 返回的具体对象
        Long total; // 总数
    }
```

###### 接口调用示例
> 地址：[http://localhost:8081/user/login](http://localhost:8081/user/login)
``` 
// 请求JSON, 下方的loginType为0或1，为0时，为用户名+验证码登录
{
    "loginType" : 0,
    "username" : "761118187@qq.com",
    "code" : "542156"
}
```

###### 接口返回数据示例
```
// 接口返回的data中存放的为Token，在后续的网站访问请求中，请求头中都需要设置此Token
{
    "success": true,
    "errorMsg": null,
    "data": "0f737d55-0724-4637-b16f-cec2c8d2a32d",
    "total": null
}
```
---

