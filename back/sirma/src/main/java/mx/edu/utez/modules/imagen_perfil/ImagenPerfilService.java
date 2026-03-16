package mx.edu.utez.modules.imagen_perfil;

import lombok.extern.slf4j.Slf4j;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.imagen.BaseImagenService;
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
@Slf4j
@Service
public class ImagenPerfilService extends BaseImagenService<ImagenPerfil, ImagenPerfilRepository> {

	private static final String CARPETA_CLOUDINARY = "sirma/perfiles";

	private final UserRepository userRepository;

	public ImagenPerfilService(ImagenPerfilRepository repository, UserRepository userRepository, CloudinaryService cloudinaryService) {
		super(repository, cloudinaryService);
		this.userRepository = userRepository;
	}

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
		Optional<ImagenPerfil> imagenPrevia = repository.findByUsuarioId(usuario.getId());
		if (imagenPrevia.isPresent()) {
			ImagenPerfil img = imagenPrevia.get();
			try {
				cloudinaryService.delete(img.getPublicIdCloudinary());
				repository.delete(img);
			} catch (IOException e) {
				// Loggear error pero continuar con la subida si es posible, o retornar error
				log.error("Error al eliminar imagen previa de Cloudinary: {}", e.getMessage());
			}
		}

		try {
			// 2. Subir nueva foto a la carpeta del usuario (usando su número de empleado)
			Map<String, Object> resultado = cloudinaryService.upload(file, carpetaUsuario);

			ImagenPerfil img = new ImagenPerfil();
			img.setUsuario(usuario);
			img.llenarDesdeCloudinary(resultado, file.getOriginalFilename());
			repository.save(img);

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

		User usuario = found.get();

		Optional<ImagenPerfil> imagen = repository.findByUsuarioId(usuario.getId());

		if (imagen.isPresent())
			return new ApiResponse("OK", imagen.get(), HttpStatus.OK);

		else
			return new ApiResponse("El usuario no tiene foto de perfil", null, HttpStatus.NOT_FOUND);

	}
}
