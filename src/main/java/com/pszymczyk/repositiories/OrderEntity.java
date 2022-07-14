package com.pszymczyk.repositiories;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
public class OrderEntity {

    @Id
    @GeneratedValue
    private Long entityId;

    @Column(unique = true)
    private String orderId;

    @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private Set<OrderItemEntity> orderItems;

    @Column
    private Long lastAppliedOffset;

    public OrderEntity() {
    }

    public OrderEntity(String orderId) {
        this.orderId = orderId;
    }

    public OrderEntity addItem(String item) {
        if (getOrderItems() == null) {
            setOrderItems(new HashSet<>());
        }
        Optional<OrderItemEntity> orderItem = getOrderItems()
            .stream()
            .filter(orderItemEntity -> orderItemEntity.getName().equals(item))
            .findAny();

        if (orderItem.isPresent()) {
            orderItem.get().setCount(orderItem.get().getCount() + 1);
        } else {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setName(item);
            orderItemEntity.setCount(1L);
            getOrderItems().add(orderItemEntity);
        }

        return this;
    }

    public void removeItem(String item) {
        if (getOrderItems() == null || getOrderItems().isEmpty()) {
            setOrderItems(new HashSet<>());
        }

        getOrderItems()
            .stream()
            .filter(orderItemEntity -> orderItemEntity.getName().equals(item))
            .findAny()
            .ifPresent(orderItemEntity -> orderItemEntity.setCount(Math.max(0L, orderItemEntity.getCount()-1)));
    }

    public OrderEntity addItem(String orderId, String item) {
        setOrderId(orderId);
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setName(item);
        orderItemEntity.setCount(1L);
        setOrderItems(Set.of(orderItemEntity));

        return this;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String userId) {
        this.orderId = userId;
    }

    public Set<OrderItemEntity> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItemEntity> orderItems) {
        this.orderItems = orderItems;
    }

    public Long getLastAppliedOffset() {
        return lastAppliedOffset;
    }

    public void setLastAppliedOffset(Long lastAppliedOffset) {
        this.lastAppliedOffset = lastAppliedOffset;
    }
}
