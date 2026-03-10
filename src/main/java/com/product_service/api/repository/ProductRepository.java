package com.product_service.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.product_service.api.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Decrement stock only if sufficient stock exists
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity " +
           "WHERE p.id = :productId AND p.stock >= :quantity")
    int decrementStock(@Param("productId") Long productId,
                       @Param("quantity") Integer quantity);

    // Restore stock (compensating transaction)
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :quantity " +
           "WHERE p.id = :productId")
    int incrementStock(@Param("productId") Long productId,
                       @Param("quantity") Integer quantity);
}
