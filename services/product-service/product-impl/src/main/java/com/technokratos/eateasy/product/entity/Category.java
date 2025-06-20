package com.technokratos.eateasy.product.entity;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
  private UUID id;
  private String title;
}
