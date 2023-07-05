/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.salemate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.ScheduleJobLogMapper;
import com.salemate.model.ScheduleJobLogEntity;
import com.salemate.service.ScheduleJobLogService;
import org.springframework.stereotype.Service;

@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogMapper, ScheduleJobLogEntity> implements ScheduleJobLogService {

//	@Override
//	public PageUtils queryPage(Map<String, Object> params) {
//		String jobId = (String)params.get("jobId");
//
//		IPage<ScheduleJobLogEntity> page = this.page(
//			new Query<ScheduleJobLogEntity>().getPage(params),
//			new QueryWrapper<ScheduleJobLogEntity>().like(StringUtils.isNotBlank(jobId),"job_id", jobId)
//		);
//
//		return new PageUtils(page);
//	}

}
