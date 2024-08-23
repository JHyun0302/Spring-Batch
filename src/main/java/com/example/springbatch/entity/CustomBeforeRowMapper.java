package com.example.springbatch.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

// Sixth 배치 JdbcPagingItemReader에서 사용하는 매퍼
public class CustomBeforeRowMapper implements RowMapper<BeforeEntity> {

    public static final String ID_COLUMN = "id";

    public static final String USERNAME_COLUMN = "username";

    @Override
    public BeforeEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        BeforeEntity beforeEntity = new BeforeEntity();

        beforeEntity.setId(rs.getLong(ID_COLUMN));
        beforeEntity.setUsername(rs.getString(USERNAME_COLUMN));

        return beforeEntity;
    }
}