package com.serverless.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.ConversionSchemas;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.serverless.model.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.ConsistentReads.CONSISTENT;

public class BookDao {

    private static volatile BookDao instance;

    public static final String TABLE_NAME_ENV_VAR = "TABLE_BOOK";

    private static final DynamoDBMapper MAPPER = DynamoDBManager.mapper();

    private static volatile DynamoDBMapperConfig bookConfig;

    private BookDao() {
        Map<String, String> env = System.getenv();
        String bookTableName = env.get(TABLE_NAME_ENV_VAR);

        DynamoDBMapperConfig.TableNameOverride tableNameOverride =
                DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(bookTableName);

        bookConfig = DynamoDBMapperConfig.builder()
                .withConsistentReads(CONSISTENT)
                .withTableNameOverride(tableNameOverride)
                .withConversionSchema(ConversionSchemas.V2)
                .build();
    }

    public static BookDao instance() {
        if (instance == null) {
            synchronized (BookDao.class) {
                if (instance == null) {
                    instance = new BookDao();
                }
            }
        }
        return instance;
    }

    public Optional<Book> findBookById(String id) {
        return Optional.ofNullable(MAPPER.load(Book.class, id, bookConfig));
    }

    public List<Book> findBooks() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withConsistentRead(false);
        return MAPPER.scan(Book.class, scanExpression, bookConfig);
    }
}
