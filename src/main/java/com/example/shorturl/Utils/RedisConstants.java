package com.example.shorturl.Utils;

public class RedisConstants {
    public static final String CACHE_LONG_URL = "shortURL:cache:longUrl:";

    public static final String CACHE_SHORT_URL = "shortURL:cache:shortUrl:";

    public static final Long CACHE_URL_TTL = 30L;

    public static final String LOGIN_CODE_KEY = "shortURL:user:login:code:";

    public static final Long LOGIN_CODE_TTL = 3L;

    public static final String LOGIN_TOKEN_KEY = "shortURL:user:login:token:";

    public static final Long LOGIN_TOKEN_TTL = 30L;

    public static final String CACHE_CLICK_COUNT_KEY = "shortURL:cache:clickTimes:";

    public static final String USER_SIGN_KEY = "shortURL:user:sign:";

}
