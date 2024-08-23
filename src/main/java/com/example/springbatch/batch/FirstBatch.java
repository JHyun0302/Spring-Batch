package com.example.springbatch.batch;

import com.example.springbatch.entity.AfterEntity;
import com.example.springbatch.entity.BeforeEntity;
import com.example.springbatch.repository.AfterRepository;
import com.example.springbatch.repository.BeforeRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FirstBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final BeforeRepository beforeRepository;
    private final AfterRepository afterRepository;

    @Bean
    public Job firstJob() {

        System.out.println("first job");

        return new JobBuilder("firstJob", jobRepository)
                .start(firstStep())
//                .next()
                .build();
    }

    /**
     * Step에서 "읽기 → 처리 → 쓰기" 과정을 구상
     *
     * 청크 : chunk
     * 이때 읽기 → 처리 → 쓰기 작업은 청크 단위로 진행되는데, 대량의 데이터를 얼만큼 끊어서 처리할지에 대한 값으로 적당한 값을 선정해야 합니다.
     * (너무 작으면 I/O 처리가 많아지고 오버헤드 발생, 너무 크면 적재 및 자원 사용에 대한 비용과 실패시 부담이 커짐)
     */
    @Bean
    public Step firstStep() {
        return new StepBuilder("firstStep", jobRepository)
                .<BeforeEntity, AfterEntity> chunk(10,platformTransactionManager)
                .reader(beforeReader())
                .processor(middleProcessor())
                .writer(afterWriter())
                .build();
    }

    /**
     * Read : BeforeEntity 테이블에서 읽어오는 Reader
     */
    @Bean
    public RepositoryItemReader<BeforeEntity> beforeReader() {

        return new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    /**
     * Process : 읽어온 데이터를 처리하는 Process (큰 작업을 수행하지 않을 경우 생략 가능, 지금과 같이 단순 이동은 사실 필요 없음)
     */
    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleProcessor() {

        return new ItemProcessor<BeforeEntity, AfterEntity>() {
            @Override
            public AfterEntity process(BeforeEntity item) throws Exception {
                AfterEntity afterEntity = new AfterEntity();
                afterEntity.setUsername(item.getUsername());
                return afterEntity;
            }
        };
    }

    /**
     * Write : AfterEntity에 처리한 결과를 저장하는 Writer
     */
    @Bean
    public RepositoryItemWriter<AfterEntity> afterWriter() {

        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}
