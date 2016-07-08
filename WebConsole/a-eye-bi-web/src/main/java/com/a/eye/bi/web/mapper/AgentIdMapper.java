package com.a.eye.bi.web.mapper;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Component
public class AgentIdMapper implements RowMapper<List<String>> {

	@Override
	public List<String> mapRow(Result result, int rowNum) throws Exception {
		if (result.isEmpty()) {
			return Collections.emptyList();
		}
		final Cell[] rawCells = result.rawCells();
		final List<String> agentIdList = new ArrayList<>(rawCells.length);

		for (Cell cell : rawCells) {
			final String agentId = Bytes.toString(CellUtil.cloneQualifier(cell));
			agentIdList.add(agentId);
		}

		return agentIdList;
	}
}
