package mx.edu.utez.modules.areas;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

@Entity
@Table(name = "AREA")
@Getter @Setter
public class Area extends BaseEntity {
}
