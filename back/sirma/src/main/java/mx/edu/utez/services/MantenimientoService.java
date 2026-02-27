package mx.edu.utez.services;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateMantenimientoDTO;
import mx.edu.utez.model.Mantenimiento;
import mx.edu.utez.repository.MantenimientoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con los mantenimientos.
 * Proporciona métodos para obtener, crear y mapear mantenimientos utilizando el repositorio de mantenimientos.
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;

    /**
     * Metodo para obtener todos los mantenimientos, devuelve un mapa con el resultado
     * @return Lista de mantenimientos obtenidos del repositorio, junto con un indicador de éxito
     */
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();

        response.put("success", 200);
        response.put("data", mantenimientoRepository.findAll());

        return response;
    }

    /**
     * Metodo para obtener un mantenimiento por su ID, devuelve un mapa con el resultado
     * @param id ID del mantenimiento a buscar
     * @return Mapa con el mantenimiento encontrado o un mensaje de error si no se encuentra, junto con un indicador de éxito
     */
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findById(id);

        if (mantenimiento.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Mantenimiento no encontrado");
        } else {
            Mantenimiento mantenimientoActual = mantenimiento.get();

            response.put("success", 200);
            response.put("mantenimiento", mantenimientoActual);
        }

        return response;
    }

    /**
     * Metodo para crear un nuevo mantenimiento a partir de un DTO, devuelve un mapa con el resultado
     * @param createMantenimientoDTO DTO con los datos para crear un nuevo mantenimiento
     * @return Mapa con el mantenimiento creado o un mensaje de error si no se pudo crear, junto con un indicador de éxito.
     */
    public Map<String, Object> create(CreateMantenimientoDTO createMantenimientoDTO) {
        Map<String, Object> response = new HashMap<>();

        Mantenimiento nuevoMantenimiento = mapMantenimiento(createMantenimientoDTO);
        Mantenimiento mantenimientoGuardado = mantenimientoRepository.save(nuevoMantenimiento);

        response.put("success", 201);
        response.put("mantenimiento", mantenimientoGuardado);

        return response;
    }

    /**
     * Metodo para actualizar un mantenimiento existente a partir de un DTO, devuelve un mapa con el resultado
     * @param id ID del mantenimiento a actualizar
     * @param createMantenimientoDTO DTO con los datos actualizados del mantenimiento
     * @return Mapa con el mantenimiento actualizado o un mensaje de error si no se pudo actualizar, junto con un indicador de éxito.
     */
    public Map<String, Object> update(Integer id, CreateMantenimientoDTO createMantenimientoDTO) {
        Map<String, Object> response = new HashMap<>();

        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findById(id);

        if (mantenimiento.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Mantenimiento no encontrado");
        } else {
            Mantenimiento mantenimientoActual = mantenimiento.get();

            Mantenimiento mantenimientoActualizado = mapMantenimiento(createMantenimientoDTO);
            mantenimientoActualizado.setIdMantenimiento(mantenimientoActual.getIdMantenimiento());

            Mantenimiento mantenimientoGuardado = mantenimientoRepository.save(mantenimientoActualizado);

            response.put("success", 200);
            response.put("mantenimiento", mantenimientoGuardado);
        }

        return response;
    }

    /**
     * Metodo para eliminar un mantenimiento por su ID, devuelve un mapa con el resultado
     * @param id ID del mantenimiento a eliminar
     * @return Mapa con un mensaje de éxito o error dependiendo del resultado de la eliminación, junto con un indicador de éxito.
     */
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findById(id);

        if (mantenimiento.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Mantenimiento no encontrado");
        } else {
            mantenimientoRepository.deleteById(id);

            response.put("success", 204);
            response.put("message", "Mantenimiento eliminado correctamente");
        }

        return response;
    }

    /**
     * Mapea un CreateMantenimientoDTO a una entidad Mantenimiento, asignando solo los campos que no son nulos.
     * @param dto DTO con los datos para crear un nuevo mantenimiento
     * @return Entidad Mantenimiento con los datos mapeados desde el DTO
     */
    private Mantenimiento mapMantenimiento(CreateMantenimientoDTO dto) {
        Mantenimiento mantenimiento = new Mantenimiento();

        if (dto.getActivo() != null)
            mantenimiento.setActivo(dto.getActivo());

        if (dto.getUsuario() != null)
            mantenimiento.setUsuario(dto.getUsuario());

        if (dto.getFechaInicio() != null)
            mantenimiento.setFechaInicio(dto.getFechaInicio());

        if (dto.getFechaFin() != null)
            mantenimiento.setFechaFin(dto.getFechaFin());

        if (dto.getDiagnostico() != null)
            mantenimiento.setDiagnostico(dto.getDiagnostico());

        if (dto.getAccionesRealizadas() != null)
            mantenimiento.setAccionesRealizadas(dto.getAccionesRealizadas());

        if (dto.getEstatusFinal() != null)
            mantenimiento.setEstatusFinal(dto.getEstatusFinal());

        return mantenimiento;
    }
}
