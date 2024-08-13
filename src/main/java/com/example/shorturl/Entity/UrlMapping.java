package com.example.shorturl.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_mapping")
public class UrlMapping implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String longUrl;

    private String shortUrl;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime expireTime;

    private int clickCount;
}
