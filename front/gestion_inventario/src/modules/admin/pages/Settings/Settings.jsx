import { useState, useEffect } from "react";
import PageHeader from "../../components/dashboard/PageHeader";
import Card from "../../../../components/Card/Card";
import Button from "../../../../components/Button/Button";
import { GenericUpload, FilesSave } from "@heathmont/moon-icons";
import Icon from "../../../../components/Icon/Icon";
import "./Settings.css";
import { getProfileFromToken } from "../../../../api/authApi";

const DEFAULT_PROFILE = {
  nombreCompleto: "Administrador",
  email: "admin@sistema.com",
  rol: "Administrador",
  departamento: "Tecnologías de la Información",
  noEmpleado: "ADM-001",
};

export default function Settings({
  profile: profileProp,
  onSaveProfile,
  onUploadPhoto,
}) {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const data = getProfileFromToken();
    if (data) setProfile(data);
    setLoading(false);
  }, []);

  const [dragOver, setDragOver] = useState(false);

  const handleFileSelect = (e) => {
    const file = e.target.files?.[0];
    if (file) onUploadPhoto?.(file);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    const file = e.dataTransfer.files?.[0];
    if (file?.type.startsWith("image/")) onUploadPhoto?.(file);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setDragOver(true);
  };

  const handleDragLeave = () => setDragOver(false);

  if (loading) return <p>Cargando...</p>;
  if (!profile) return <p>No se pudo cargar el perfil</p>;

  return (
    <>
      <PageHeader
        overline="CONFIGURACIÓN"
        title="Ajustes de Cuenta"
        subtitle="Administra tu perfil y preferencias de la cuenta"
      />

      <section className="settings-view">
        <div className="settings-view__grid">
          <Card className="settings-card settings-card--photo" padding="medium">
            <div className="settings-card__header">
              <h2 className="settings-card__title">Foto de Perfil</h2>
              <p className="settings-card__subtitle">
                Se muestra en la barra lateral y tu perfil
              </p>
            </div>
            <div
              className={`settings-upload ${dragOver ? "settings-upload--dragover" : ""}`}
              onDrop={handleDrop}
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onClick={() => document.getElementById("photo-upload")?.click()}
            >
              <input
                id="photo-upload"
                type="file"
                accept="image/*"
                onChange={handleFileSelect}
                className="settings-upload__input"
                aria-label="Subir foto de perfil"
              />
              <Icon
                icon={GenericUpload}
                size={40}
                className="settings-upload__icon"
              />
              <p className="settings-upload__text">
                Arrastra una imagen o haz clic para subir
              </p>
              <p className="settings-upload__hint">PNG, JPG hasta 5MB</p>
            </div>
          </Card>

          <Card className="settings-card settings-card--info" padding="medium">
            <div className="settings-card__header">
              <h2 className="settings-card__title">Información de la Cuenta</h2>
              <p className="settings-card__subtitle">
                Datos asociados a tu cuenta de {profile.rol.toLowerCase()}
              </p>
            </div>
            <div className="settings-info">
              <div className="settings-info__row">
                <div className="settings-info__field">
                  <span className="settings-info__label">Nombre Completo</span>
                  <span className="settings-info__value">
                    {profile.nombreCompleto}
                  </span>
                </div>
                <div className="settings-info__field">
                  <span className="settings-info__label">
                    Correo Electrónico
                  </span>
                  <span className="settings-info__value">{profile.correo}</span>
                </div>
              </div>
              <div className="settings-info__divider" />
              <div className="settings-info__row">
                <div className="settings-info__field">
                  <span className="settings-info__label">Rol del Sistema</span>
                  <span className="settings-info__value">{profile.rol}</span>
                </div>
                <div className="settings-info__field">
                  <span className="settings-info__label">Area</span>
                  <span className="settings-info__value">{profile.area}</span>
                </div>
              </div>
              <div className="settings-info__divider" />
              <div className="settings-info__row">
                <div className="settings-info__field">
                  <span className="settings-info__label">No. Empleado</span>
                  <span className="settings-info__value">
                    {profile.numeroEmpleado}
                  </span>
                </div>
              </div>
            </div>
            {onSaveProfile && (
              <div className="settings-card__footer">
                <Button
                  variant="primary"
                  size="small"
                  iconLeft={FilesSave}
                  iconSize={30}
                  onClick={() => onSaveProfile?.(profile)}
                >
                  Guardar Cambios
                </Button>
              </div>
            )}
          </Card>
        </div>
      </section>
    </>
  );
}
