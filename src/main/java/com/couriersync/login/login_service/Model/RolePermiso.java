package com.couriersync.login.login_service.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rol_permiso")
public class RolePermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;
}
