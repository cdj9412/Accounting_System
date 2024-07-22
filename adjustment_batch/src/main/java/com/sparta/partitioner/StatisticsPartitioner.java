package com.sparta.partitioner;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsPartitioner implements Partitioner {

	private final JdbcTemplate jdbcTemplate;
	private final int gridSize;
	private final LocalDate date;
	private final AtomicInteger partition = new AtomicInteger(0);

	public StatisticsPartitioner(DataSource dataSource, int gridSize, LocalDate date) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.gridSize = gridSize;
		this.date = date;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		long minVideoId = getMinVideoId();
		long maxVideoId = getMaxVideoId();

		if (minVideoId == 0 && maxVideoId == 0) {
			return Collections.emptyMap();
		}

		long range = Math.max(1, (maxVideoId - minVideoId + 1) / this.gridSize);

		Map<String, ExecutionContext> result = new HashMap<>();
		for (int i = 0; i < this.gridSize; i++) {
			long startVideoId = minVideoId + (i * range);
			long endVideoId = (i == this.gridSize - 1) ? maxVideoId : (startVideoId + range - 1);

			if (startVideoId > maxVideoId) {
				break;
			}

			ExecutionContext context = new ExecutionContext();
			context.putLong("startVideoId", startVideoId);
			context.putLong("endVideoId", endVideoId);
			context.put("date", date);
			context.putInt("partitionNumber", partition.getAndIncrement());
			result.put("partition" + i, context);
		}

		return result;
	}

	private long getMinVideoId() {
		Long minId = jdbcTemplate.queryForObject(
			"SELECT COALESCE(MIN(video_id), 0) FROM video_daily_statistic WHERE date = ?",
			Long.class,
			date
		);
		return minId != null ? minId : 0L;
	}

	private long getMaxVideoId() {
		Long maxId = jdbcTemplate.queryForObject(
			"SELECT COALESCE(MAX(video_id), 0) FROM video_daily_statistic WHERE date = ?",
			Long.class,
			date
		);
		return maxId != null ? maxId : 0L;
	}
}