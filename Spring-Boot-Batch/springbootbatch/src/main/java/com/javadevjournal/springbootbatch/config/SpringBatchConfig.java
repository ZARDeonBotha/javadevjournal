package com.javadevjournal.springbootbatch.config;

import com.javadevjournal.springbootbatch.listener.SpringBatchJobCompletionListener;
import com.javadevjournal.springbootbatch.listener.SpringBatchJobExecutionListener;
import com.javadevjournal.springbootbatch.listener.SpringBatchStepListener;
import com.javadevjournal.springbootbatch.model.StockInfo;
import com.javadevjournal.springbootbatch.step.StockInfoProcessor;
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
	public MultiResourceItemReader<StockInfo> multiResourceItemReader() {
		MultiResourceItemReader<StockInfo> reader = new MultiResourceItemReader<>();

		reader.setDelegate(reader());
		reader.setResources(inputFiles);

		return reader;
	}

	@Bean
	public Job processJob() {
		return jobBuilderFactory.get("stockpricesinfojob")
				.incrementer(new RunIdIncrementer())
				.listener(new SpringBatchJobExecutionListener())
				.flow(StockPricesInfoStep())
                .end()
                .build();
	}

    @Bean
    public Step StockPricesInfoStep() {
        return stepBuilderFactory.get("step1")
                .listener(new SpringBatchStepListener())
                .<StockInfo, String>chunk(10)
                .reader(multiResourceItemReader())
                .processor(stockInfoProcessor())
                .writer(writer())
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean
    public FlatFileItemReader<StockInfo> reader() {
        return new FlatFileItemReaderBuilder<StockInfo>()
                .name("stockInfoItemReader")
                .delimited()
                .names(new String[] {"stockId", "stockName","stockPrice","yearlyHigh","yearlyLow","address","sector","market"})
                .targetType(StockInfo.class)
                .build();
    }

    @Bean
    public StockInfoProcessor stockInfoProcessor(){
	    return new StockInfoProcessor();
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
