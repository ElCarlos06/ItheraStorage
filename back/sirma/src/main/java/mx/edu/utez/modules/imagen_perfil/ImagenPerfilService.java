package mx.edu.utez.modules.imagen_perfil;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import mx.edu.utez.util.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para la administración de imágenes de perfil de Usuario.
 * Permite a los usuarios tener una foto de perfil personalizada almacenada en la nube.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class ImagenPerfilService {

	private static final String CARPETA_CLOUDINARY = "sirma/perfiles";

	private final ImagenPerfilRepository imagenPerfilRepository;
	private final UserRepository userRepository;
	private final CloudinaryService cloudinaryService;

	/**
	 * Sube y asocia una foto de perfil al usuario indicado.
	 *
	 * @param usuarioId ID del usuario.
	 * @param file      Archivo de la foto de perfil.
	 * @return ApiResponse con la nueva imagen de perfil.
	 */
	@Transactional
	public ApiResponse subirImagen(Long usuarioId, MultipartFile file) {
		Optional<User> found = userRepository.findById(usuarioId);
		if (found.isEmpty())
			return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);

		try {
			Map<String, Object> resultado = cloudinaryService.upload(file, CARPETA_CLOUDINARY);

			ImagenPerfil img = new ImagenPerfil();
			img.setUsuario(found.get());
			img.setUrlCloudinary((String) resultado.get("secure_url"));
			img.setPublicIdCloudinary((String) resultado.get("public_id"));
			img.setNombreArchivo(file.getOriginalFilename());
			imagenPerfilRepository.save(img);

			return new ApiResponse("Imagen de perfil subida correctamente", img, HttpStatus.CREATED);
		} catch (IOException e) {
			return new ApiResponse("Error al subir imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Lista todas las fotos de perfil del usuario (historial por si se guardan varias).
	 *
	 * @param usuarioId ID del usuario.
	 * @return ApiResponse con la lista de fotos.
	 */
	@Transactional(readOnly = true)
	public ApiResponse listarImagenes(Long usuarioId) {
		List<ImagenPerfil> lista = imagenPerfilRepository.findByUsuarioId(usuarioId);
		return new ApiResponse("OK", lista, HttpStatus.OK);
	}

	/**
	 * Elimina una foto de perfil usando su identificador de imagen.
	 *
	 * @param imagenId ID de la imagen a eliminar.
	 * @return ApiResponse indicando el resultado.
	 */
	@Transactional
	public ApiResponse eliminarImagen(Long imagenId) {
		Optional<ImagenPerfil> found = imagenPerfilRepository.findById(imagenId);
		if (found.isEmpty())
			return new ApiResponse("Imagen no encontrada", true, HttpStatus.NOT_FOUND);

		try {
			cloudinaryService.delete(found.get().getPublicIdCloudinary());
			imagenPerfilRepository.delete(found.get());
			return new ApiResponse("Imagen de perfil eliminada correctamente", HttpStatus.OK);
		} catch (IOException e) {
			return new ApiResponse("Error al eliminar imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
