package com.javadevjournal.springbootbatch.config;

import com.javadevjournal.springbootbatch.listener.SpringBatchJobCompletionListener;
import com.javadevjournal.springbootbatch.listener.SpringBatchJobExecutionListener;
import com.javadevjournal.springbootbatch.listener.SpringBatchStepListener;
import com.javadevjournal.springbootbatch.model.LogLineRecord;
import com.javadevjournal.springbootbatch.step.LogLineRecordProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * @Author - Kunwar Vikas
 */
@Configuration
public class SpringBatchConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Value("classpath*:/csv/stockinfo*.csv")
	private Resource[] inputFiles;

	@Bean
	public MultiResourceItemReader<LogLineRecord> multiResourceItemReader() {
		MultiResourceItemReader<LogLineRecord> reader = new MultiResourceItemReader<>();

		reader.setDelegate(reader());
		reader.setResources(inputFiles);

		return reader;
	}

	@Bean
	public Job processJob() {
		return jobBuilderFactory.get("stockpricesinfojob")
				.incrementer(new RunIdIncrementer())
				.listener(new SpringBatchJobExecutionListener())
				.flow(LogLineRecordStep())
                .end()
                .build();
	}

    @Bean
    public Step LogLineRecordStep() {
        return stepBuilderFactory.get("step1")
                .listener(new SpringBatchStepListener())
                .<LogLineRecord, String>chunk(10)
                .reader(multiResourceItemReader())
                .processor(stockInfoProcessor())
                .writer(writer())
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean
    public FlatFileItemReader<LogLineRecord> reader() {
        return new FlatFileItemReaderBuilder<LogLineRecord>()
                .name("logLineRecordReader")
                .delimited()
                //.names(new String[] {"stockId", "stockName","stockPrice","yearlyHigh","yearlyLow","address","sector","market"})
				.names(new String[] {"logRecord"})
				.targetType(LogLineRecord.class)
                .build();
    }

    @Bean
    public LogLineRecordProcessor stockInfoProcessor(){
	    return new LogLineRecordProcessor();
    }

    @Bean
    public FlatFileItemWriter<String> writer() {
        return new FlatFileItemWriterBuilder<String>()
                .name("stockInfoItemWriter")
                .resource(new FileSystemResource(
                        "target/output.txt"))
                .lineAggregator(new PassThroughLineAggregator<>())
				.build();
    }

	@Bean
	public JobExecutionListener listener() {
		return new SpringBatchJobCompletionListener();
	}
}
