package mx.edu.utez.services;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateReporteDTO;
import mx.edu.utez.model.Reporte;
import mx.edu.utez.repository.ReporteRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con los reportes.
 * Proporciona métodos para obtener, crear y mapear reportes utilizando el repositorio de reportes.
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;

    /**
     * Metodo para obtener todos los reportes, devuelve un mapa con el resultado
     * @return Lista de reportes obtenidos del repositorio, junto con un indicador de éxito
     */
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();

        response.put("success", 200);
        response.put("data", reporteRepository.findAll());

        return response;
    }

    /**
     * Metodo para obtener un reporte por su ID, devuelve un mapa con el resultado
     * @param id ID del reporte a buscar
     * @return Mapa con el reporte encontrado o un mensaje de error si no se encuentra, junto con un indicador de éxito
     */
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Reporte> reporte = reporteRepository.findById(id);

        if (reporte.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Reporte no encontrado");
        } else {
            Reporte reporteActual = reporte.get();

            response.put("success", 200);
            response.put("reporte", reporteActual);
        }

        return response;
    }

    /**
     * Metodo para crear un nuevo reporte a partir de un DTO, devuelve un mapa con el resultado
     * @param createReporteDTO DTO con los datos para crear un nuevo reporte
     * @return Mapa con el reporte creado o un mensaje de error si no se pudo crear, junto con un indicador de éxito.
     */
    public Map<String, Object> create(CreateReporteDTO createReporteDTO) {
        Map<String, Object> response = new HashMap<>();

        Reporte nuevoReporte = mapReporte(createReporteDTO);
        Reporte reporteGuardado = reporteRepository.save(nuevoReporte);

        response.put("success", 201);
        response.put("reporte", reporteGuardado);

        return response;
    }

    /**
     * Metodo para actualizar un reporte existente a partir de un DTO, devuelve un mapa con el resultado
     * @param id ID del reporte a actualizar
     * @param createReporteDTO DTO con los datos actualizados del reporte
     * @return Mapa con el reporte actualizado o un mensaje de error si no se pudo actualizar, junto con un indicador de éxito.
     */
    public Map<String, Object> update(Integer id, CreateReporteDTO createReporteDTO) {
        Map<String, Object> response = new HashMap<>();

        Optional<Reporte> reporte = reporteRepository.findById(id);

        if (reporte.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Reporte no encontrado");
        } else {
            Reporte reporteActual = reporte.get();

            Reporte reporteActualizado = mapReporte(createReporteDTO);
            reporteActualizado.setIdReporte(reporteActual.getIdReporte());

            Reporte reporteGuardado = reporteRepository.save(reporteActualizado);

            response.put("success", 200);
            response.put("reporte", reporteGuardado);
        }

        return response;
    }

    /**
     * Metodo para eliminar un reporte por su ID, devuelve un mapa con el resultado
     * @param id ID del reporte a eliminar
     * @return Mapa con un mensaje de éxito o error dependiendo del resultado de la eliminación, junto con un indicador de éxito.
     */
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Reporte> reporte = reporteRepository.findById(id);

        if (reporte.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Reporte no encontrado");
        } else {
            reporteRepository.deleteById(id);

            response.put("success", 204);
            response.put("message", "Reporte eliminado correctamente");
        }

        return response;
    }

    /**
     * Mapea un CreateReporteDTO a una entidad Reporte, asignando solo los campos que no son nulos.
     * @param dto DTO con los datos para crear un nuevo reporte
     * @return Entidad Reporte con los datos mapeados desde el DTO
     */
    private Reporte mapReporte(CreateReporteDTO dto) {
        Reporte reporte = new Reporte();

        if (dto.getActivo() != null)
            reporte.setActivo(dto.getActivo());

        if (dto.getPrioridad() != null)
            reporte.setPrioridad(dto.getPrioridad());

        if (dto.getUsuario() != null)
            reporte.setUsuario(dto.getUsuario());

        if (dto.getFechaReporte() != null)
            reporte.setFechaReporte(dto.getFechaReporte());

        if (dto.getVideo() != null)
            reporte.setVideo(dto.getVideo());

        return reporte;
    }
}
