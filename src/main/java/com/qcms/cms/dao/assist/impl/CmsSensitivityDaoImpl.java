package com.qcms.cms.dao.assist.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.qcms.cms.dao.assist.CmsSensitivityDao;
import com.qcms.cms.entity.assist.CmsSensitivity;
import com.qcms.common.hibernate3.HibernateBaseDao;

@Repository
public class CmsSensitivityDaoImpl extends
		HibernateBaseDao<CmsSensitivity, Integer> implements CmsSensitivityDao {
	@SuppressWarnings("unchecked")
	public List<CmsSensitivity> getList(boolean cacheable) {
		String hql = "from CmsSensitivity bean order by bean.id desc";
		return getSession().createQuery(hql).setCacheable(cacheable).list();
	}

	public CmsSensitivity findById(Integer id) {
		CmsSensitivity entity = get(id);
		return entity;
	}

	public CmsSensitivity save(CmsSensitivity bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsSensitivity deleteById(Integer id) {
		CmsSensitivity entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	@Override
	protected Class<CmsSensitivity> getEntityClass() {
		return CmsSensitivity.class;
	}
}