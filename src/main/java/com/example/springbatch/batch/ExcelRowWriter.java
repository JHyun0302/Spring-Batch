package com.example.springbatch.batch;

import com.example.springbatch.entity.BeforeEntity;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamWriter;

public class ExcelRowWriter implements ItemStreamWriter<BeforeEntity> {

    private final String filePath;

    private Workbook workbook;

    private Sheet sheet;

    private int currentRowNumber;

    private boolean isClosed;

    public ExcelRowWriter(String filePath) throws IOException {
        this.filePath = filePath;
        this.isClosed = false;
        this.currentRowNumber = 0;
    }

    @Override
    public void write(Chunk<? extends BeforeEntity> chunk) throws Exception {

    }
}
