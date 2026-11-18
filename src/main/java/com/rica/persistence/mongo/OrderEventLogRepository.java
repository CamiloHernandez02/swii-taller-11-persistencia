package com.rica.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderEventLogRepository extends MongoRepository<OrderEventLog, String> {
    List<OrderEventLog> findByOrderId(String orderId);
    Optional<OrderEventLog> findByEventId(String eventId);
    List<OrderEventLog> findByEventType(String eventType);
}
