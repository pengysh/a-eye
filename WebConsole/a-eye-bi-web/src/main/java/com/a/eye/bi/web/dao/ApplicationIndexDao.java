package com.a.eye.bi.web.dao;

import java.util.List;

import com.a.eye.bi.web.view.Application;

/**
 * 
 * @author netspider
 * 
 */
public interface ApplicationIndexDao {
	
	List<Application> selectAllApplicationNames();
	
}
