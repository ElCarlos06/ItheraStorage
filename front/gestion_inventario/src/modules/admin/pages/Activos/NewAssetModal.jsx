import { useState, useEffect } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import Select from "../../../../components/Select/Select";
import { FilesSave } from "@heathmont/moon-icons";
import { tipoActivosApi } from "../../../../api/tipoActivosApi";
import { ubicacionesApi } from "../../../../api/ubicacionesApi";
import { toast } from "../../../../utils/toast.jsx";
import "./NewAssetModal.css";

function toOptions(items, idKey = "id", nameKey = "nombre") {
  const list = Array.isArray(items) ? items : [];
  return list
    .map((item) => {
      const id = item[idKey] ?? item.id ?? item.idMarca ?? item.idModelo;
      const label = item[nameKey] ?? item.name ?? item.nombreEspacio ?? "—";
      const value = id != null && id !== "" ? String(id) : "";
      return { value, label };
    })
    .filter((o) => o.value !== "");
}

export default function NewAssetModal({ open, onClose, onGuardar, initialData }) {
  const isEdit = !!initialData;
  const [form, setForm] = useState({
    etiqueta: "",
    numeroSerie: "",
    idTipoActivo: "",
    costo: "",
    idCampus: "",
    idEdificio: "",
    idEspacio: "",
    descripcion: "",
  });

  const [tipoActivosOptions, setTipoActivosOptions] = useState([]);
  const [campusOptions, setCampusOptions] = useState([]);
  const [edificiosOptions, setEdificiosOptions] = useState([]);
  const [espaciosOptions, setEspaciosOptions] = useState([]);
  const [loadingOptions, setLoadingOptions] = useState(false);

  const extractList = (res) => {
    if (!res) return [];
    const d = res.data;
    if (Array.isArray(d?.content)) return d.content;
    if (Array.isArray(d)) return d;
    if (Array.isArray(res?.content)) return res.content;
    return [];
  };

  // Cargar tipos de activo, campus y (si hay initialData) edificios y espacios
  useEffect(() => {
    if (!open) return;
    setLoadingOptions(true);
    const espacio = initialData?.espacio ?? {};
    const edificio = espacio?.edificio ?? {};
    const idCampus = initialData?.idCampus ?? edificio?.campus?.id;
    const idEdificio = initialData?.idEdificio ?? edificio?.id;

    Promise.all([
      tipoActivosApi.getTipoActivos(0, 200),
      ubicacionesApi.getCampus(0, 200),
      idCampus ? ubicacionesApi.getEdificiosByCampus(idCampus) : Promise.resolve(null),
      idEdificio ? ubicacionesApi.getEspaciosByEdificio(idEdificio) : Promise.resolve(null),
    ])
      .then(([tiposRes, campusRes, edificiosRes, espaciosRes]) => {
        setTipoActivosOptions(toOptions(extractList(tiposRes)));
        setCampusOptions(toOptions(extractList(campusRes)));
        if (edificiosRes) {
          const list = Array.isArray(edificiosRes?.data) ? edificiosRes.data : extractList(edificiosRes);
          setEdificiosOptions(toOptions(list));
        } else {
          setEdificiosOptions([]);
        }
        if (espaciosRes) {
          const list = Array.isArray(espaciosRes?.data) ? espaciosRes.data : extractList(espaciosRes);
          setEspaciosOptions(toOptions(list, "id", "nombreEspacio"));
        } else {
          setEspaciosOptions([]);
        }
      })
      .catch(() => {
        setTipoActivosOptions([]);
        setCampusOptions([]);
      })
      .finally(() => setLoadingOptions(false));
  }, [open, initialData]);

  // Cargar edificios cuando cambia el campus
  useEffect(() => {
    if (!form.idCampus) {
      setEdificiosOptions([]);
      return;
    }
    ubicacionesApi
      .getEdificiosByCampus(form.idCampus)
      .then((res) => {
        const list = Array.isArray(res?.data) ? res.data : [];
        setEdificiosOptions(toOptions(list));
      })
      .catch(() => setEdificiosOptions([]));
  }, [form.idCampus]);

  // Cargar espacios (aulas) cuando cambia el edificio
  useEffect(() => {
    if (!form.idEdificio) {
      setEspaciosOptions([]);
      return;
    }
    ubicacionesApi
      .getEspaciosByEdificio(form.idEdificio)
      .then((res) => {
        const list = Array.isArray(res?.data) ? res.data : [];
        setEspaciosOptions(toOptions(list, "id", "nombreEspacio"));
      })
      .catch(() => setEspaciosOptions([]));
  }, [form.idEdificio]);

  useEffect(() => {
    if (open && initialData) {
      const espacio = initialData.espacio ?? initialData.aula;
      const edificio = espacio?.edificio ?? initialData.edificio;
      const campus = edificio?.campus ?? initialData.campus;
      const tipoActivo = initialData.tipoActivo;

      setForm({
        etiqueta: initialData.etiqueta ?? initialData.codigo ?? "",
        numeroSerie: initialData.numeroSerie ?? "",
        idTipoActivo: String(initialData.idTipoActivo ?? tipoActivo?.id ?? ""),
        costo: initialData.costo ?? "",
        idCampus: String(initialData.idCampus ?? campus?.id ?? ""),
        idEdificio: String(initialData.idEdificio ?? edificio?.id ?? ""),
        idEspacio: String(initialData.idEspacio ?? espacio?.id ?? ""),
        descripcion: initialData.descripcion ?? initialData.descripcionCorta ?? "",
      });
    } else if (open) {
      setForm({
        etiqueta: "",
        numeroSerie: "",
        idTipoActivo: "",
        costo: "",
        idCampus: "",
        idEdificio: "",
        idEspacio: "",
        descripcion: "",
      });
    }
  }, [open, initialData]);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSelect = (field) => (value) => {
    if (field === "idCampus") {
      setForm((prev) => ({ ...prev, idCampus: value, idEdificio: "", idEspacio: "" }));
    } else if (field === "idEdificio") {
      setForm((prev) => ({ ...prev, idEdificio: value, idEspacio: "" }));
    } else {
      setForm((prev) => ({ ...prev, [field]: value }));
    }
  };

  const validarCampos = () => {
    const faltantes = [];
    if (!form.etiqueta?.trim()) faltantes.push("Etiqueta");
    if (!form.numeroSerie?.trim()) faltantes.push("Número de serie");
    if (!form.idTipoActivo) faltantes.push("Tipo de activo");
    if (!form.idCampus) faltantes.push("Campus");
    if (!form.idEdificio) faltantes.push("Edificio");
    if (!form.idEspacio) faltantes.push("Aula");
    if (faltantes.length > 0) {
      toast.error(`Complete los campos obligatorios: ${faltantes.join(", ")}`);
      return false;
    }
    return true;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validarCampos()) return;
    const payload = {
      etiqueta: form.etiqueta.trim(),
      numeroSerie: form.numeroSerie.trim(),
      idTipoActivo: form.idTipoActivo ? Number(form.idTipoActivo) : null,
      idEspacio: form.idEspacio ? Number(form.idEspacio) : null,
      costo: form.costo ? Number(form.costo) : null,
      descripcion: form.descripcion?.trim() || null,
    };
    onGuardar?.(payload);
    setForm({
      etiqueta: "",
      numeroSerie: "",
      idTipoActivo: "",
      costo: "",
      idCampus: "",
      idEdificio: "",
      idEspacio: "",
      descripcion: "",
    });
    onClose?.();
  };

  const handleClose = () => {
    setForm({
      etiqueta: "",
      numeroSerie: "",
      idTipoActivo: "",
      costo: "",
      idCampus: "",
      idEdificio: "",
      idEspacio: "",
      descripcion: "",
    });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="nuevo-activo-modal"
      title={isEdit ? "Editar Activo" : "Registrar Nuevo Activo"}
      subtitle="Completa la información técnica del dispositivo"
      submitLabel={isEdit ? "Guardar cambios" : "Guardar Activo"}
      submitIcon={FilesSave}
      submitIconSize={30}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__row">
        <div className="form-modal__field form-modal__field--flex">
          <Input
            label="Etiqueta*"
            labelClassName="form-modal__label"
            placeholder="ACT-001"
            value={form.etiqueta}
            onChange={handleChange("etiqueta")}
            className="form-modal__input"
            required
          />
        </div>
        <div className="form-modal__field form-modal__field--flex">
          <Input
            label="Número de Serie*"
            labelClassName="form-modal__label"
            placeholder="SN-XXXX-XXXX-XXXX"
            value={form.numeroSerie}
            onChange={handleChange("numeroSerie")}
            className="form-modal__input"
            required
          />
        </div>
      </div>

      <div className="form-modal__row">
        <div className="form-modal__field form-modal__field--flex">
          <Select
            label="Tipo de Activo*"
            labelClassName="form-modal__label"
            value={form.idTipoActivo}
            onChange={handleSelect("idTipoActivo")}
            options={tipoActivosOptions}
            placeholder={loadingOptions ? "Cargando…" : "Seleccionar..."}
            required
            variant="ghost"
          />
        </div>
        <div className="form-modal__field form-modal__field--flex nuevo-activo-modal__field--costo">
          <label className="form-modal__label">Costo</label>
          <div className="nuevo-activo-modal__input-costo">
            <span className="nuevo-activo-modal__costo-prefix">$</span>
            <Input
              type="number"
              step="0.01"
              min="0"
              placeholder="0.00"
              value={form.costo}
              onChange={handleChange("costo")}
              className="form-modal__input nuevo-activo-modal__input--costo"
            />
          </div>
        </div>
      </div>

      <div className="nuevo-activo-modal__section">
        <h3 className="nuevo-activo-modal__section-title">Ubicación</h3>
        <div className="form-modal__row nuevo-activo-modal__row--3">
          <div className="form-modal__field form-modal__field--flex">
            <Select
              label="Campus*"
              labelClassName="form-modal__label"
              value={form.idCampus}
              onChange={handleSelect("idCampus")}
              options={campusOptions}
              placeholder={loadingOptions ? "Cargando…" : "Seleccionar..."}
              required
              variant="ghost"
            />
          </div>
          <div className="form-modal__field form-modal__field--flex">
            <Select
              label="Edificio*"
              labelClassName="form-modal__label"
              value={form.idEdificio}
              onChange={handleSelect("idEdificio")}
              options={edificiosOptions}
              placeholder={form.idCampus ? "Seleccionar..." : "Selecciona campus primero"}
              required
              variant="ghost"
            />
          </div>
          <div className="form-modal__field form-modal__field--flex">
            <Select
              label="Aula*"
              labelClassName="form-modal__label"
              value={form.idEspacio}
              onChange={handleSelect("idEspacio")}
              options={espaciosOptions}
              placeholder={form.idEdificio ? "Seleccionar..." : "Selecciona edificio primero"}
              required
              variant="ghost"
            />
          </div>
        </div>
      </div>

      <div className="form-modal__field">
        <label className="form-modal__label">Descripción Detallada</label>
        <textarea
          placeholder="Especificaciones técnicas, marca, modelo, etc."
          value={form.descripcion}
          onChange={handleChange("descripcion")}
          className="form-modal__textarea"
          rows={4}
        />
      </div>
    </FormModal>
  );
}
