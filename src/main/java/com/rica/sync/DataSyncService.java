package com.rica.sync;

import com.rica.persistence.Order;
import com.rica.persistence.OrderRepository;
import com.rica.persistence.mongo.OrderEventLog;
import com.rica.persistence.mongo.OrderEventLogRepository;
import com.rica.persistence.redis.CacheRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class DataSyncService {
    private static final Logger log = LoggerFactory.getLogger(DataSyncService.class);

    private final OrderRepository orderRepository;
    private final OrderEventLogRepository eventLogRepository;
    private final CacheRepository cacheRepository;

    public DataSyncService(OrderRepository orderRepository,
                          OrderEventLogRepository eventLogRepository,
                          CacheRepository cacheRepository) {
        this.orderRepository = orderRepository;
        this.eventLogRepository = eventLogRepository;
        this.cacheRepository = cacheRepository;
    }

    public void syncOrderToAllSources(Order order) {
        try {
            // Write to PostgreSQL
            Order savedOrder = orderRepository.save(order);
            log.info("Order synced to PostgreSQL: {}", order.getOrderId());

            // Write to Redis cache
            String orderJson = convertToJson(order);
            cacheRepository.setOrderCache(order.getOrderId(), orderJson, 3600);
            log.info("Order cached in Redis: {}", order.getOrderId());

            // Log to MongoDB for audit trail
            OrderEventLog eventLog = new OrderEventLog(
                    order.getOrderId(),
                    "sync-" + System.currentTimeMillis(),
                    "ORDER_SYNCED",
                    orderJson
            );
            eventLogRepository.save(eventLog);
            log.info("Order event logged to MongoDB: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Error syncing order: {}", order.getOrderId(), e);
        }
    }

    public Order getOrderFromCacheOrDb(String orderId) {
        // Try cache first
        String cachedOrder = cacheRepository.getOrderCache(orderId);
        if (cachedOrder != null) {
            log.info("Order retrieved from cache: {}", orderId);
            return convertFromJson(cachedOrder);
        }

        // Fallback to PostgreSQL
        Optional<Order> order = orderRepository.findByOrderId(orderId);
        if (order.isPresent()) {
            log.info("Order retrieved from PostgreSQL: {}", orderId);
            // Update cache
            cacheRepository.setOrderCache(orderId, convertToJson(order.get()), 3600);
            return order.get();
        }

        return null;
    }

    public void logOrderEvent(String orderId, String eventType, String eventData) {
        OrderEventLog eventLog = new OrderEventLog(orderId, 
                "event-" + System.currentTimeMillis(), 
                eventType, 
                eventData);
        eventLogRepository.save(eventLog);
        log.info("Event logged for order {}: {}", orderId, eventType);
    }

    private String convertToJson(Order order) {
        return String.format(
                "{\"orderId\":\"%s\",\"customerId\":\"%s\",\"totalAmount\":%s,\"status\":\"%s\"}",
                order.getOrderId(), order.getCustomerId(), order.getTotalAmount(), order.getStatus()
        );
    }

    private Order convertFromJson(String json) {
        // Simple JSON parsing (consider using Jackson in production)
        Order order = new Order();
        // Parse JSON and populate order object
        return order;
    }
}
