package com.rica;

import com.rica.sync.DataSyncService;
import com.rica.persistence.Order;
import com.rica.persistence.OrderRepository;
import com.rica.persistence.mongo.OrderEventLogRepository;
import com.rica.persistence.redis.CacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DataSyncServiceTest {

    private DataSyncService dataSyncService;
    private OrderRepository orderRepository;
    private OrderEventLogRepository eventLogRepository;
    private CacheRepository cacheRepository;

    @BeforeEach
    public void setUp() {
        orderRepository = mock(OrderRepository.class);
        eventLogRepository = mock(OrderEventLogRepository.class);
        cacheRepository = mock(CacheRepository.class);
        dataSyncService = new DataSyncService(orderRepository, eventLogRepository, cacheRepository);
    }

    @Test
    public void testSyncOrderToAllSources() {
        Order order = new Order("ORD-001", "CUST-001", new BigDecimal("99.99"), "CREATED");

        dataSyncService.syncOrderToAllSources(order);

        verify(orderRepository).save(order);
        verify(cacheRepository).setOrderCache(anyString(), anyString(), anyLong());
        verify(eventLogRepository).save(any());
    }

    @Test
    public void testLogOrderEvent() {
        dataSyncService.logOrderEvent("ORD-001", "ORDER_CREATED", "{}");

        verify(eventLogRepository).save(any());
    }
}
