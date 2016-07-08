package com.a.eye.bi.web.dao.hbase;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Scan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Repository;

import com.a.eye.bi.web.dao.ApplicationIndexDao;
import com.a.eye.bi.web.view.Application;
import com.a.eye.common.web.common.hbase.HBaseTables;
import com.a.eye.common.web.common.hbase.HbaseOperations2;

/**
 * @author netspider
 * @author emeroad
 */
@Repository
public class HbaseApplicationIndexDao implements ApplicationIndexDao {

	@Autowired
	private HbaseOperations2 hbaseOperations2;

	@Autowired
	@Qualifier("applicationNameMapper")
	private RowMapper<List<Application>> applicationNameMapper;

	@Autowired
	@Qualifier("agentIdMapper")
	private RowMapper<List<String>> agentIdMapper;

	@Override
	public List<Application> selectAllApplicationNames() {
		Scan scan = new Scan();
		scan.setCaching(30);
		List<List<Application>> results = hbaseOperations2.find(HBaseTables.APPLICATION_INDEX, scan, applicationNameMapper);
		List<Application> applications = new ArrayList<>();
		for (List<Application> result : results) {
			applications.addAll(result);
		}
		return applications;
	}
}
