package com.bnpparibas.training.batch.springbatchdemo.config;

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
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.bnpparibas.training.batch.springbatchdemo.dto.BookDto;

/**
 * Configuration class for import-job: Read Book data from flat csv file into
 * database.
 */
@Configuration
@EnableBatchProcessing
public class ImportJobConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImportJobConfig.class);

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private MaClasseMetier maClasseMetier;

	@Bean(name = "importJob")
	public Job importBookJob(final Step importStep, final Step deleteStep,
			final JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("import-Job") //
				.incrementer(new RunIdIncrementer()) //
				.listener(listener) //
				.start(deleteStep) //
				.next(importStep) //
				.build();
	}

	@Bean
	public Step importStep(final FlatFileItemReader<BookDto> importReader, //
			final ItemProcessor<BookDto, BookDto> importProcessor, //
			final ItemWriter<BookDto> importWriter) {

		return stepBuilderFactory.get("import-step") //
				.<BookDto, BookDto>chunk(10) //
				.reader(importReader) //
				.processor(importProcessor) //
				.writer(importWriter) //
				.build();
	}

	/**
	 * Delete Step for deleting all Book records.
	 *
	 * @param dataSource
	 *            dataSource injected by Spring
	 * @return the Step
	 */
	@Bean
	public Step deleteStep(final DataSource dataSource) {
		return stepBuilderFactory.get("delete-step") //
				.tasklet(deleteTasklet(dataSource)) //
				.build();
	}

	/**
	 * Instanciate DeleteTasklet
	 *
	 * @param dataSource
	 *            the dataSource
	 * @return the tasklet
	 */
	private Tasklet deleteTasklet(final DataSource dataSource) {
		final DeleteTasklet deleteTasklet = new DeleteTasklet();
		deleteTasklet.setSql("DELETE FROM Book;");
		deleteTasklet.setDataSource(dataSource);

		return deleteTasklet;
	}

	@StepScope // Mandatory for using jobParameters
	@Bean
	public FlatFileItemReader<BookDto> importReader(@Value("#{jobParameters['input-file']}") final String inputFile) {
		return new FlatFileItemReaderBuilder<BookDto>() //
				.name("bookItemReader") //
				.resource(new FileSystemResource(inputFile)) //
				.delimited() //
				.delimiter(";") //
				.names(new String[] { "title", "author", "isbn", "publisher", "publishedOn" }) //
				.linesToSkip(1) //
				.fieldSetMapper(new BeanWrapperFieldSetMapper<BookDto>() {
					{
						setTargetType(BookDto.class);
					}
				}).build();
	}

	@Bean
	public ItemProcessor<BookDto, BookDto> importProcessor() {
		return new ItemProcessor<BookDto, BookDto>() {
			@Override
			public BookDto process(final BookDto book) throws Exception {
				LOGGER.info("Processing {}", book);

				return maClasseMetier.maMethodeMetier(book);
			}
		};
	}

	@Bean
	public JdbcBatchItemWriter<BookDto> importWriter(final DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<BookDto>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO book (title, author, isbn, publisher, year) "
						+ "VALUES (:title, :author, :isbn, :publisher, :publishedOn )")
				.dataSource(dataSource) //
				.build();
	}

}
