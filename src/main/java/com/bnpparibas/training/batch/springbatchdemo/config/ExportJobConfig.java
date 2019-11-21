package com.bnpparibas.training.batch.springbatchdemo.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import com.bnpparibas.training.batch.springbatchdemo.dto.BookDto;

/**
 * Configuration class for export-job: Read Book data from database into flat csv
 * file.
 */
@Configuration
@EnableBatchProcessing
public class ExportJobConfig {

	private static final Logger log = LoggerFactory.getLogger(ExportJobConfig.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	/**
	 * ItemReader is an abstract representation of how data is provided as input to
	 * a Step. When the inputs are exhausted, the ItemReader returns null.
	 */
	@Bean
	public JdbcCursorItemReader<BookDto> exportReader() {
		final JdbcCursorItemReader<BookDto> reader = new JdbcCursorItemReader<BookDto>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT title, author, isbn, publisher, year FROM Book");
		reader.setRowMapper(new BookRowMapper());

		return reader;
	}

	/**
	 * RowMapper used to map resultset to BookDto
	 */
	public class BookRowMapper implements RowMapper<BookDto> {

		@Override
		public BookDto mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final BookDto book = new BookDto();
			book.setTitle(rs.getString("title"));
			book.setAuthor(rs.getString("author"));
			book.setIsbn(rs.getString("isbn"));
			book.setPublisher(rs.getString("publisher"));
			book.setPublishedOn(rs.getInt("year"));
			return book;
		}
	}

	/**
	 * ItemProcessor represents the business processing of an item. The data read by
	 * ItemReader can be passed on to ItemProcessor. In this unit, the data is
	 * transformed and sent for writing. If, while processing the item, it becomes
	 * invalid for further processing, you can return null. The nulls are not
	 * written by ItemWriter.
	 */
	@Bean
	public ItemProcessor<BookDto, BookDto> exportProcessor() {
		return new ItemProcessor<BookDto, BookDto>() {

			@Override
			public BookDto process(final BookDto book) throws Exception {
				log.info("Processing {}", book);
				return book;
			}
		};
	}

	/**
	 * ItemWriter is the output of a Step. The writer writes one batch or chunk of
	 * items at a time to the target system. ItemWriter has no knowledge of the
	 * input it will receive next, only the item that was passed in its current
	 * invocation.
	 */
	@StepScope // Mandatory for using jobParameters
	@Bean
	public FlatFileItemWriter<BookDto> exportWriter(@Value("#{jobParameters['output-file']}") final String outputFile) {
		final FlatFileItemWriter<BookDto> writer = new FlatFileItemWriter<BookDto>();
		writer.setResource(new FileSystemResource(outputFile));
		writer.setLineAggregator(new DelimitedLineAggregator<BookDto>() {
			{
				setDelimiter(",");
				setFieldExtractor(new BeanWrapperFieldExtractor<BookDto>() {
					{
						setNames(new String[] { "title", "author", "isbn", "publisher", "publishedOn" });
					}
				});
			}
		});

		return writer;
	}

	@Bean
	public Step exportStep(final FlatFileItemWriter<BookDto> exportWriter) {
		return stepBuilderFactory.get("export-step").<BookDto, BookDto>chunk(10) //
				.reader(exportReader()) //
				.processor(exportProcessor()) //
				.writer(exportWriter) //
				.build();
	}

	@Bean(name = "exportJob")
	public Job exportBookJob(final Step exportStep) {
		return jobBuilderFactory.get("export-job") //
				.incrementer(new RunIdIncrementer()) //
				.flow(exportStep) //
				.end() //
				.build();
	}

}