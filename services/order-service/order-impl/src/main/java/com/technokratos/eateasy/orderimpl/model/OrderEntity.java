package com.technokratos.eateasy.orderimpl.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {
    @Id
    private UUID id;
    @Column(name = "cart_id", nullable = false)
    private UUID cartId;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "delivery_address")
    private String deliveryAddress;
}
