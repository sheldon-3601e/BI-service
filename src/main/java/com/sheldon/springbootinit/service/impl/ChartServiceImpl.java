package com.sheldon.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sheldon.springbootinit.mapper.ChartMapper;
import com.sheldon.springbootinit.model.entity.Chart;
import com.sheldon.springbootinit.service.ChartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 26483
 * @description 针对表【chart(图表)】的数据库操作Service实现
 * @createDate 2024-01-18 01:58:17
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService {

    @Resource
    private ChartMapper chartMapper;

}




