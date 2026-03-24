import { toast } from "../../../../../utils/toast.jsx";
import { ubicacionesApi } from "../../../../../api/ubicacionesApi";
import { tipoActivosApi } from "../../../../../api/tipoActivosApi";
import RegisterTipoActivoModal from "../RegisterTipoActivoModal";
import RegisterLocationModal from "../RegisterLocationModal";
import RegisterCampusModal from "../RegisterCampusModal";
import RegisterBuildingModal from "../RegisterBuildingModal";
import RegisterClassroomModal from "../RegisterClassroomModal";
import ConfirmDeleteModal from "../../../../../components/ConfirmDeleteModal/ConfirmDeleteModal";

export default function CatalogModals({
  // Tipo Activo
  modalTipoActivoOpen,
  onCloseTipoActivo,
  editTipoActivo,
  onGuardarTipoActivo,
  // Location (primera vez)
  modalLocationOpen,
  onCloseLocation,
  onGuardarLocation,
  refreshLocations,
  // Campus
  modalCampusOpen,
  onCloseCampus,
  editLocation,
  onGuardarCampus,
  subTab,
  // Edificio
  modalBuildingOpen,
  onCloseBuilding,
  campusList,
  items,
  onGuardarEdificio,
  // Aula
  modalClassroomOpen,
  onCloseClassroom,
  edificiosList,
  onGuardarAula,
  // Delete Location
  confirmDeleteLocation,
  onCloseConfirmDeleteLocation,
  onConfirmDeleteLocation,
  getDeleteMessage,
  // Delete Tipo Activo
  confirmDeleteTipoActivo,
  onCloseConfirmDeleteTipoActivo,
  onConfirmDeleteTipoActivo,
}) {
  return (
    <>
      <RegisterTipoActivoModal
        open={modalTipoActivoOpen}
        onClose={onCloseTipoActivo}
        initialData={editTipoActivo}
        onGuardar={onGuardarTipoActivo}
      />

      <RegisterLocationModal
        open={modalLocationOpen}
        onClose={onCloseLocation}
        onGuardar={async (data) => {
          try {
            const campusRes = await ubicacionesApi.createCampus({
              nombre: data.campus.trim(),
              descripcion: data.descripcion?.trim() || null,
            });
            const campusId = campusRes?.data?.id;
            if (!campusId) throw new Error("No se obtuvo el campus creado");
            const edificioRes = await ubicacionesApi.createEdificio({
              idCampus: campusId,
              nombre: data.edificio.trim(),
            });
            const edificioId = edificioRes?.data?.id;
            if (!edificioId) throw new Error("No se obtuvo el edificio creado");
            await ubicacionesApi.createEspacio({
              idEdificio: edificioId,
              nombreEspacio: data.aula.trim(),
            });
            toast.success("Ubicación registrada correctamente");
            refreshLocations();
          } catch (err) {
            toast.error(err?.message ?? "Error al guardar");
            throw err;
          }
          onCloseLocation();
        }}
      />

      <RegisterCampusModal
        open={modalCampusOpen}
        onClose={onCloseCampus}
        initialData={subTab === "campus" ? editLocation : undefined}
        onGuardar={async (data) => {
          await onGuardarCampus(data);
          onCloseCampus();
        }}
      />

      <RegisterBuildingModal
        open={modalBuildingOpen}
        onClose={onCloseBuilding}
        campus={campusList}
        initialData={subTab === "edificios" ? (items.find((e) => e.id === editLocation?.id) ?? editLocation) : undefined}
        onGuardar={async (data) => {
          await onGuardarEdificio(data);
          onCloseBuilding();
        }}
      />

      <RegisterClassroomModal
        open={modalClassroomOpen}
        onClose={onCloseClassroom}
        edificios={edificiosList}
        initialData={subTab === "aulas" ? (items.find((s) => s.id === editLocation?.id) ?? editLocation) : undefined}
        onGuardar={async (data) => {
          await onGuardarAula(data);
          onCloseClassroom();
        }}
      />

      <ConfirmDeleteModal
        open={!!confirmDeleteLocation}
        onClose={onCloseConfirmDeleteLocation}
        onConfirm={onConfirmDeleteLocation}
        message={getDeleteMessage?.() ?? ""}
      />

      <ConfirmDeleteModal
        open={!!confirmDeleteTipoActivo}
        onClose={onCloseConfirmDeleteTipoActivo}
        onConfirm={onConfirmDeleteTipoActivo}
        title="¿Confirmar eliminación?"
        message={
          confirmDeleteTipoActivo
            ? `Se eliminará el tipo de activo "${confirmDeleteTipoActivo.nombre ?? "este elemento"}". Esta acción no se puede deshacer.`
            : ""
        }
      />
    </>
  );
}
