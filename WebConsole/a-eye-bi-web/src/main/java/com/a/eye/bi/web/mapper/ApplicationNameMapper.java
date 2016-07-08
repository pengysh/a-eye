package com.a.eye.bi.web.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;

import com.a.eye.bi.web.service.ApplicationFactory;
import com.a.eye.bi.web.view.Application;

/**
 *
 */
@Component
public class ApplicationNameMapper implements RowMapper<List<Application>> {

	@Autowired
	private ApplicationFactory applicationFactory;

	@Override
	public List<Application> mapRow(Result result, int rowNum) throws Exception {
		if (result.isEmpty()) {
			return Collections.emptyList();
		}
		Set<Short> uniqueTypeCodes = new HashSet<>();
		String applicationName = Bytes.toString(result.getRow());

		Cell[] rawCells = result.rawCells();
		for (Cell cell : rawCells) {
			short serviceTypeCode = Bytes.toShort(CellUtil.cloneValue(cell));
			uniqueTypeCodes.add(serviceTypeCode);
		}
		List<Application> applicationList = new ArrayList<>();
		for (short serviceTypeCode : uniqueTypeCodes) {
			final Application application = applicationFactory.createApplication(applicationName, serviceTypeCode);
			applicationList.add(application);
		}
		return applicationList;
	}
}
