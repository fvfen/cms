package com.qcms.cms.dao.main.impl;

import org.springframework.stereotype.Repository;

import com.qcms.cms.dao.main.CmsConfigDao;
import com.qcms.cms.entity.main.CmsConfig;
import com.qcms.common.hibernate3.HibernateBaseDao;

@Repository
public class CmsConfigDaoImpl extends HibernateBaseDao<CmsConfig, Integer>
		implements CmsConfigDao {
	public CmsConfig findById(Integer id) {
		CmsConfig entity = get(id);
		return entity;
	}

	@Override
	protected Class<CmsConfig> getEntityClass() {
		return CmsConfig.class;
	}
}