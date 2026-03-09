package mx.edu.utez.modules.roles;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

@Entity
@Table(name = "ROL")
@Getter
@Setter
public class Role extends BaseEntity {

    private String name;
    private String description;

}
