package com.example.springbatch.batch;

import com.example.springbatch.entity.AfterEntity;
import com.example.springbatch.entity.BeforeEntity;
import com.example.springbatch.entity.CustomBeforeRowMapper;
import com.example.springbatch.repository.AfterRepository;
import com.example.springbatch.repository.BeforeRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * JPA 성능 문제와 JDBC
 * Batch 처리 청크 단위 bulk insert 수행이 무너지게 됩니다.
 * JDBC 기반으로 작성하게 된다면 청크로 설정한 값이 모이게 된다면 bulk 쿼리로 단 1번의 insert가 수행되지만 JPA의 IDENTITY 전략 때문에 bulk 쿼리 대신 각각의 수만큼 insert가 수행됩니다.
 */
@Configuration
@RequiredArgsConstructor
public class SixthBatch {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final BeforeRepository beforeRepository;

    private final AfterRepository afterRepository;

    private final DataSource dataSource;

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            private LocalDateTime startTime;

            @Override
            public void beforeJob(JobExecution jobExecution) {
                startTime = LocalDateTime.now();
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                LocalDateTime endTime = LocalDateTime.now();

                long nanos = ChronoUnit.NANOS.between(startTime, endTime);
                double seconds = nanos / 1_000_000_000.0;

                System.out.println("Job 실행 시간: " + seconds + " 초");
            }
        };
    }

    @Bean
    public Job sixthJob() {
        return new JobBuilder("sixthJob", jobRepository)
                .start(sixthStep())
                .listener(jobExecutionListener())
                .build();
    }

    @Bean
    public Step sixthStep() {
        return new StepBuilder("sixthStep", jobRepository)
                .<BeforeEntity, AfterEntity> chunk(10, platformTransactionManager)
                .reader(beforeSixthReader())
                .processor(middleSixthProcessor())
                .writer(afterSixthWriter())
                .build();
    }

//    @Bean
//    public RepositoryItemReader<BeforeEntity> beforeSixthReader() {
//        return new RepositoryItemReaderBuilder<BeforeEntity>()
//                .name("beforeReader")
//                .pageSize(10)
//                .methodName("findAll")
//                .repository(beforeRepository)
//                .sorts(Map.of("id", Sort.Direction.ASC))
//                .build();
//    }

    @Bean
    public JdbcPagingItemReader<BeforeEntity> beforeSixthReader() {
        return new JdbcPagingItemReaderBuilder<BeforeEntity>()
                .name("beforeSixthReader")
                .dataSource(dataSource)
                .selectClause("SELECT id, username")
                .fromClause("FROM BeforeEntity")
                .sortKeys(Map.of("id", Order.ASCENDING))
                .rowMapper(new CustomBeforeRowMapper())
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleSixthProcessor() {
        return new ItemProcessor<BeforeEntity, AfterEntity>() {
            @Override
            public AfterEntity process(BeforeEntity item) throws Exception {
                AfterEntity afterEntity = new AfterEntity();
                afterEntity.setUsername(item.getUsername());
                return afterEntity;
            }
        };
    }

//    @Bean
//    public RepositoryItemWriter<AfterEntity> afterSixthWriter() {
//        return new RepositoryItemWriterBuilder<AfterEntity>()
//                .repository(afterRepository)
//                .methodName("save")
//                .build();
//    }

    @Bean
    public JdbcBatchItemWriter<AfterEntity> afterSixthWriter() {
        String sql = "INSERT INTO AfterEntity (username) VALUES (:username)";
        return new JdbcBatchItemWriterBuilder<AfterEntity>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }


}