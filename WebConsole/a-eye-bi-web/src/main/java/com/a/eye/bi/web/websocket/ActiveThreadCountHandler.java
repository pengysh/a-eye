package com.a.eye.bi.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.a.eye.bi.web.service.AgentService;

/**
 * @Author Taejin Koo
 */
public class ActiveThreadCountHandler extends TextWebSocketHandler implements AEyeWebSocketHandler {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final AgentService agentService;

	private static final long DEFAULT_FLUSH_DELAY = 1000;

	private static final long DEFAULT_HEALTH_CHECk_DELAY = 60 * 1000;

	private static final String DEFAULT_REQUEST_MAPPING = "/agent/activeThread";

	public ActiveThreadCountHandler(AgentService agentService) {
		this(DEFAULT_REQUEST_MAPPING, agentService);
	}

	public ActiveThreadCountHandler(String requestMapping, AgentService agentService) {
		this(requestMapping, agentService, DEFAULT_FLUSH_DELAY);
	}

	public ActiveThreadCountHandler(String requestMapping, AgentService agentService, long flushDelay) {
		this(requestMapping, agentService, flushDelay, DEFAULT_HEALTH_CHECk_DELAY);
	}

	public ActiveThreadCountHandler(String requestMapping, AgentService agentService, long flushDelay,
			long healthCheckDelay) {
		this.agentService = agentService;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getRequestMapping() {
		// TODO Auto-generated method stub
		return null;
	}
}
