package mx.edu.utez.repository;

import mx.edu.utez.model.Resguardo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResguardoRepository extends JpaRepository<Resguardo, Integer> {
}
