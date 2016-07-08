package com.a.eye.bi.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a.eye.bi.web.dao.ApplicationIndexDao;
import com.a.eye.bi.web.view.Application;

/**
 * @author netspider
 */
@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	private ApplicationIndexDao applicationIndexDao;

	@Override
	public List<Application> selectAllApplicationNames() {
		return applicationIndexDao.selectAllApplicationNames();
	}
}
