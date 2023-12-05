package com.lkishalmi.counter.aws;

import com.lkishalmi.counter.CounterRepository;
import com.lkishalmi.counter.CounterService.Counter;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

/**
 *
 * @author lkishalmi
 */
@Requires(condition = CIAwsRegionProviderChainCondition.class)
@Requires(condition = CIAwsCredentialsProviderChainCondition.class)
@Requires(beans = { DynamoConfiguration.class, DynamoDbClient.class })
@Singleton
@Primary
public class DynamoRepository implements CounterRepository {

    protected final DynamoDbClient client;
    protected final DynamoConfiguration config;

    public DynamoRepository(DynamoDbClient client, DynamoConfiguration config) {
        this.client = client;
        this.config = config;
    }
    
    public boolean existsTable() {
        try {
            client.describeTable(DescribeTableRequest.builder()
                    .tableName(config.getTableName())
                    .build());
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
    
    public void createTable() {
        client.createTable((ct) -> ct
                .tableName(config.getTableName())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .attributeDefinitions(
                        (ad) -> ad.attributeName("name").attributeType(ScalarAttributeType.S),
                        (ad) -> ad.attributeName("value").attributeType(ScalarAttributeType.N),
                        (ad) -> ad.attributeName("modified").attributeType(ScalarAttributeType.S)
                )
        );
    }
    
    @Override
    public Optional<Counter> load(String name) {
        var response = client.getItem((ir) -> ir
                .key(Map.of("name", AttributeValue.fromS(name)))
                .tableName(config.getTableName())
        );
        return response.hasItem() ? Optional.of(fromItem(response.item())): Optional.empty();
    }
    
    protected Counter fromItem(Map<String, AttributeValue> item) {
        var name = item.get("name").s();
        var value = Integer.parseInt(item.get("value").n());
        return new Counter(name, value, Instant.now());
    }
    
    @Override
    public void save(Counter c) {
        var item = new HashMap<String, AttributeValue>();
        item.put("name", AttributeValue.fromS(c.name()));
        item.put("value", AttributeValue.fromN(String.valueOf(c.value())));
        item.put("modified", AttributeValue.fromS(c.lastUsed().toString()));
        
        client.putItem((ir) -> ir
                .item(item)
                .tableName(config.getTableName())
                
        );
    }
}
