package com.qcms.cms.statistic.workload;

import java.util.Date;
import java.util.List;

import com.qcms.cms.statistic.workload.CmsWorkLoadStatistic.CmsWorkLoadStatisticDateKind;
import com.qcms.cms.statistic.workload.CmsWorkLoadStatistic.CmsWorkLoadStatisticGroup;

public interface CmsWorkLoadStatisticSvc {
	public List<CmsWorkLoadStatistic> statistic(Integer channelId,
			Integer reviewerId, Integer authorId, Date beginDate, Date endDate,
			CmsWorkLoadStatisticGroup group,CmsWorkLoadStatisticDateKind kind);
}
