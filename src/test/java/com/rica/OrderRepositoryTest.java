package com.rica;

import com.rica.persistence.Order;
import com.rica.persistence.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testSaveAndRetrieveOrder() {
        Order order = new Order("ORD-001", "CUST-001", new BigDecimal("99.99"), "CREATED");
        Order savedOrder = orderRepository.save(order);

        Optional<Order> retrievedOrder = orderRepository.findByOrderId("ORD-001");
        assertTrue(retrievedOrder.isPresent());
        assertEquals("CUST-001", retrievedOrder.get().getCustomerId());
    }

    @Test
    public void testFindByCustomerId() {
        Order order1 = new Order("ORD-001", "CUST-001", new BigDecimal("99.99"), "CREATED");
        Order order2 = new Order("ORD-002", "CUST-001", new BigDecimal("149.99"), "CREATED");

        orderRepository.save(order1);
        orderRepository.save(order2);

        var orders = orderRepository.findByCustomerId("CUST-001");
        assertEquals(2, orders.size());
    }

    @Test
    public void testFindByStatus() {
        Order order1 = new Order("ORD-001", "CUST-001", new BigDecimal("99.99"), "COMPLETED");
        Order order2 = new Order("ORD-002", "CUST-001", new BigDecimal("149.99"), "PENDING");

        orderRepository.save(order1);
        orderRepository.save(order2);

        var completedOrders = orderRepository.findByStatus("COMPLETED");
        assertEquals(1, completedOrders.size());
    }
}
