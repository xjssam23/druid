/*
 * Copyright 2011 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.stat;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;

import com.alibaba.druid.util.JMXUtils;

public class JdbcStatementStat implements JdbcStatementStatMBean {
	private final AtomicLong createCount = new AtomicLong(0); // 执行createStatement的计数
	private final AtomicLong prepareCount = new AtomicLong(0); // 执行parepareStatement的计数
	private final AtomicLong prepareCallCount = new AtomicLong(0); // 执行preCall的计数
	private final AtomicLong closeCounter = new AtomicLong(0); // Statement关闭的计数
	
	private final AtomicInteger concurrentCount = new AtomicInteger();
	private final AtomicInteger concurrentMax = new AtomicInteger();

	private final AtomicLong count = new AtomicLong();
	private final AtomicLong errorCount = new AtomicLong();

	private final AtomicLong nanoTotal = new AtomicLong();
	
	private Throwable lastError;
	private long lastErrorTime;

	private long lastSampleTime = 0;
	
	public void reset() {
		concurrentCount.set(0);
		concurrentMax.set(0);
		count.set(0);
		errorCount.set(0);
		nanoTotal.set(0);
		lastError = null;
		lastErrorTime = 0;
		lastSampleTime = 0;
		
		createCount.set(0);
		prepareCount.set(0);
		prepareCallCount.set(0);
		closeCounter.set(0);
	}

	public void beforeExecute() {
		int invoking = concurrentCount.incrementAndGet();

		for (;;) {
			int max = concurrentMax.get();
			if (invoking > max) {
				if (concurrentMax.compareAndSet(max, invoking)) {
					break;
				} else {
					continue;
				}
			} else {
				break;
			}
		}

		count.incrementAndGet();
		lastSampleTime = System.currentTimeMillis();
	}

	public long getErrorCount() {
		return errorCount.get();
	}

	public int getRunningCount() {
		return concurrentCount.get();
	}

	public int getConcurrentMax() {
		return concurrentMax.get();
	}

	public long getExecuteCount() {
		return count.get();
	}
	
	public Date getExecuteLastTime() {
		if (lastSampleTime == 0) {
			return null;
		}

		return new Date(lastSampleTime);
	}

	public long getNanoTotal() {
		return nanoTotal.get();
	}

	public void afterExecute(long nanoSpan) {
		concurrentCount.decrementAndGet();

		nanoTotal.addAndGet(nanoSpan);
	}

	public Throwable getLastException() {
		return lastError;
	}

	public Date getLastErrorTime() {
		if (lastErrorTime <= 0) {
			return null;
		}
		
		return new Date(lastErrorTime);
	}

	public void error(Throwable error) {
		errorCount.incrementAndGet();
		lastError = error;
		lastErrorTime = System.currentTimeMillis();
	}
	
	@Override
	public long getCloseCount() {
		return closeCounter.get();
	}

	@Override
	public long getCreateCount() {
		return createCount.get();
	}

	@Override
	public long getExecuteMillisTotal() {
		return this.getNanoTotal() / (1000 * 1000);
	}

	@Override
	public long getPrepareCallCount() {
		return prepareCallCount.get();
	}

	@Override
	public long getPrepareCount() {
		return prepareCount.get();
	}

	@Override
	public long getExecuteSuccessCount() {
		return this.getExecuteCount() - this.getErrorCount() - this.getRunningCount();
	}
	
	@Override
	public CompositeData getLastError() throws JMException {
		return JMXUtils.getErrorCompositeData(this.getLastException());
	}
	
	public void incrementCreateCounter() {
		createCount.incrementAndGet();
	}

	public void incrementPrepareCallCount() {
		prepareCallCount.incrementAndGet();
	}

	public void incrementPrepareCounter() {
		prepareCount.incrementAndGet();
	}

	public void incrementStatementCloseCounter() {
		closeCounter.incrementAndGet();
	}
	
	public static class Entry {
		private long lastExecuteStartNano;
		private String lastExecuteSql;

		public long getLastExecuteStartNano() {
			return lastExecuteStartNano;
		}

		public void setLastExecuteStartNano(long lastExecuteStartNano) {
			this.lastExecuteStartNano = lastExecuteStartNano;
		}

		public String getLastExecuteSql() {
			return lastExecuteSql;
		}

		public void setLastExecuteSql(String lastExecuteSql) {
			this.lastExecuteSql = lastExecuteSql;
		}
	}
}
