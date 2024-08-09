package com.example.shorturl.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shorturl.Entity.UrlMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UrlMappingMapper extends BaseMapper<UrlMapping> {

    void updateBatchClickCount(@Param("list") List<UrlMapping> list);

}
