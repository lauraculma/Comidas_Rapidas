package com.comidarapida.repository;

import com.comidarapida.model.ProductoComidaRapida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<ProductoComidaRapida, Long> {
}
