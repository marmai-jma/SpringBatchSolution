package com.bnpparibas.training.batch.springbatchdemo.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Tasklet used to delete records
 */
public class DeleteTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTasklet.class);

	private DataSource dataSource;

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private String sql;

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(final String sql) {
		this.sql = sql;
	}

	@Override
	public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
		final JdbcTemplate template = new JdbcTemplate(dataSource);
		final int[] recordCount = template.batchUpdate(sql);
		LOGGER.info("Record deleted {}", recordCount);
		return RepeatStatus.FINISHED;

	}
}