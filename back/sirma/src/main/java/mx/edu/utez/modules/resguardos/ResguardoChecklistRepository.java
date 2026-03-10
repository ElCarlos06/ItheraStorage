package mx.edu.utez.modules.resguardos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResguardoChecklistRepository extends JpaRepository<ResguardoChecklist, Long> {
    List<ResguardoChecklist> findByResguardoId(Long resguardoId);
}

