package com.example.springbatch.batch;

import com.example.springbatch.entity.BeforeEntity;
import com.example.springbatch.repository.BeforeRepository;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 테이블 → 엑셀
 */
@Configuration
@RequiredArgsConstructor
public class FifthBatch {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final BeforeRepository beforeRepository;

    @Bean
    public Job fifthJob() {

        System.out.println("fifth job");

        return new JobBuilder("fifthJob", jobRepository)
                .start(fifthStep())
                .build();
    }

    @Bean
    public Step fifthStep() {

        System.out.println("fifth step");

        return new StepBuilder("fifthStep", jobRepository)
                .<BeforeEntity, BeforeEntity> chunk(10, platformTransactionManager)
                .reader(fifthBeforeReader())
                .processor(fifthProcessor())
                .writer(excelWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<BeforeEntity> fifthBeforeReader() {

        RepositoryItemReader<BeforeEntity> reader = new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();

        // 전체 데이터 셋에서 어디까지 수행 했는지의 값을 저장하지 않음
        reader.setSaveState(false);

        return reader;
    }

    @Bean
    public ItemProcessor<BeforeEntity, BeforeEntity> fifthProcessor() {
        return item -> item;
    }

    @Bean
    public ItemStreamWriter<BeforeEntity> excelWriter() {
        try {
            return new ExcelRowWriter("/Users/jaehyun/Documents/IdeaProjects/Inflearn/Yummi/SpringBatch/FifthBatch.xlsx");
            //리눅스나 맥은 /User/형태로
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
