package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Integer> {
}
