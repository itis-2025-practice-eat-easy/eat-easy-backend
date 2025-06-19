package com.technokratos.eateasy.product;

import com.technokratos.eateasy.product.util.MinioProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@EnableConfigurationProperties(MinioProperties.class)
@ComponentScan({
        "com.technokratos.eateasy.common.exceptionhandler",
        "com.technokratos.eateasy.product"
})
public class ProductApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductApplication.class, args);
  }
}
