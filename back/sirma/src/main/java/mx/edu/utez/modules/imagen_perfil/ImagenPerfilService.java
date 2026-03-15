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
	 * @param correo Correo del usuario actual.
	 * @param file      Archivo de la foto de perfil.
	 * @return ApiResponse con la nueva imagen de perfil.
	 */
	@Transactional
	public ApiResponse subirImagen(String correo, MultipartFile file) {
		Optional<User> found = userRepository.findByCorreoIgnoreCase(correo);
		if (found.isEmpty())
			return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);

		User usuario = found.get();
		String carpetaUsuario = CARPETA_CLOUDINARY + "/" + usuario.getNumeroEmpleado();

		// 1. Eliminar foto anterior si existe (para mantener solo una activa y limpia)
		Optional<ImagenPerfil> imagenPrevia = imagenPerfilRepository.findByUsuarioId(usuario.getId());
		if (imagenPrevia.isPresent()) {
			ImagenPerfil img = imagenPrevia.get();
			try {
				cloudinaryService.delete(img.getPublicIdCloudinary());
				imagenPerfilRepository.delete(img);
			} catch (IOException e) {
				// Loggear error pero continuar con la subida si es posible, o retornar error
				System.err.println("Error al eliminar imagen previa de Cloudinary: " + e.getMessage());
			}
		}

		try {
			// 2. Subir nueva foto a la carpeta del usuario (usando su número de empleado)
			Map<String, Object> resultado = cloudinaryService.upload(file, carpetaUsuario);

			ImagenPerfil img = new ImagenPerfil();
			img.setUsuario(usuario);
			img.setUrlCloudinary((String) resultado.get("secure_url"));
			img.setPublicIdCloudinary((String) resultado.get("public_id"));
			img.setNombreArchivo(file.getOriginalFilename());
			imagenPerfilRepository.save(img);

			return new ApiResponse("Imagen de perfil actualizada correctamente", img, HttpStatus.CREATED);
		} catch (IOException e) {
			return new ApiResponse("Error al subir imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Obtiene la foto de perfil actual del usuario.
	 *
	 * @param correo Correo del usuario actual.
	 * @return ApiResponse con la imagen de perfil encontrada.
	 */
	@Transactional(readOnly = true)
	public ApiResponse obtenerImagen(String correo) {

		Optional<User> found = userRepository.findByCorreoIgnoreCase(correo);

		if (found.isEmpty())
			return new ApiResponse("El usuario no tiene imágenes asociadas", true, HttpStatus.NOT_FOUND);

		User  usuario = found.get();

		Optional<ImagenPerfil> imagen = imagenPerfilRepository.findByUsuarioId(usuario.getId());
		if (imagen.isPresent()) {
			return new ApiResponse("OK", imagen.get(), HttpStatus.OK);
		} else {
			return new ApiResponse("El usuario no tiene foto de perfil", null, HttpStatus.NOT_FOUND);
		}
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
