package com.comidarapida.repository;

import com.comidarapida.model.MateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima, Long> {
    java.util.Optional<MateriaPrima> findByCodigo(int codigo);
}
