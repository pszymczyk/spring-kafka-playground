package com.pszymczyk.repositiories;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private Set<ConsumedOffsetEntity> consumedOffsets;

    public OrderEntity addItem(String item, int partition, long offset) {
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

        updateOffsets(partition, offset);
        return this;
    }

    public void removeItem(String item, int partition, long offset) {
        if (getOrderItems() == null || getOrderItems().isEmpty()) {
            setOrderItems(new HashSet<>());
        }

        getOrderItems()
            .stream()
            .filter(orderItemEntity -> orderItemEntity.getName().equals(item))
            .findAny()
            .ifPresent(orderItemEntity -> orderItemEntity.setCount(Math.max(0L, orderItemEntity.getCount()-1)));

        updateOffsets(partition, offset);
    }

    private void updateOffsets(int partition, long offset) {
        ConsumedOffsetEntity consumedOffsetEntity = new ConsumedOffsetEntity();
        consumedOffsetEntity.setKafkaOffset(offset);
        consumedOffsetEntity.setPartition(partition);

        Set<ConsumedOffsetEntity> collect = consumedOffsets
            .stream()
            .filter(consumedOffsetEntity1 -> !consumedOffsetEntity1.getPartition().equals(consumedOffsetEntity.getPartition()))
            .collect(Collectors.toSet());

        collect.add(consumedOffsetEntity);
        consumedOffsets.clear();
        consumedOffsets.addAll(collect);
    }


    public OrderEntity addItem(String orderId, String item, int partition, long offset) {
        setOrderId(orderId);
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setName(item);
        orderItemEntity.setCount(1L);
        setOrderItems(Set.of(orderItemEntity));

        Set<ConsumedOffsetEntity> consumedOffsetEntitySet = new HashSet<>();
        ConsumedOffsetEntity consumedOffsetEntity = new ConsumedOffsetEntity();
        consumedOffsetEntity.setKafkaOffset(offset);
        consumedOffsetEntity.setPartition(partition);
        consumedOffsetEntitySet.add(consumedOffsetEntity);
        setConsumedOffsets(consumedOffsetEntitySet);
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

    public Set<ConsumedOffsetEntity> getConsumedOffsets() {
        return consumedOffsets;
    }

    public void setConsumedOffsets(Set<ConsumedOffsetEntity> consumedOffsets) {
        this.consumedOffsets = consumedOffsets;
    }


}
