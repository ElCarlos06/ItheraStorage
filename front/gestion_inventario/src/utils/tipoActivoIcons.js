/**
 * Mapeo de iconos para tipos de activo según nombre y tipoBien.
 * Prioridad: 1) tipoBien (vehículo), 2) keywords en nombre, 3) default.
 */
import {
  SportCarRacing,
  DevicesMacbook,
  MediaMonitor,
  DevicesSmartphone,
  DevicesTvBox,
  FilesPrint,
  GenericPicture,
  SoftwareCode,
  TravelBed,
  ShopBag,
  TravelHotel,
  DevicesKeyboard,
  DevicesMouse,
  GenericHome,
} from "@heathmont/moon-icons";

/** Icono por defecto cuando no hay coincidencia */
export const DEFAULT_TIPO_ACTIVO_ICON = ShopBag;

/** Mapeos: array de { keywords, icon } — si nombre incluye keyword → ese icono */
const MAPEOS = [
  { keywords: ["auto", "carro", "vehículo", "vehiculo", "coche", "camioneta", "moto", "motocicleta"], icon: SportCarRacing },
  { keywords: ["laptop", "portátil", "portatil", "computadora", "pc ", "notebook", "macbook"], icon: DevicesMacbook },
  { keywords: ["monitor", "pantalla", "display", "proyector"], icon: MediaMonitor },
  { keywords: ["teléfono", "telefono", "celular", "phone", "smartphone"], icon: DevicesSmartphone },
  { keywords: ["impresora", "printer", "multifuncional"], icon: FilesPrint },
  { keywords: ["cámara", "camara", "camera", "videocámara"], icon: GenericPicture },
  { keywords: ["televisión", "television", "tv ", "televisor"], icon: DevicesTvBox },
  { keywords: ["mesa", "escritorio", "silla", "mobiliario", "mueble"], icon: TravelBed },
  { keywords: ["teclado", "keyboard"], icon: DevicesKeyboard },
  { keywords: ["mouse", "ratón", "raton"], icon: DevicesMouse },
  { keywords: ["caja", "almacén", "almacen", "inventario", "estantería"], icon: ShopBag },
  { keywords: ["aire", "acondicionado", "ventilador", "clima"], icon: GenericHome },
];

/**
 * Devuelve el icono para un tipo de activo.
 * @param {{ nombre?: string, tipoBien?: string }} item - tipo de activo
 * @returns {import("react").ComponentType} componente de icono
 */
export function getTipoActivoIcon(item) {
  const n = (item?.nombre ?? "").toLowerCase().trim();
  const tipoBien = (item?.tipoBien ?? "").toLowerCase();

  // 1) Si es vehículo por tipoBien (Inmueble = vehículo en la UI)
  if (tipoBien.includes("inmueble")) return SportCarRacing;

  // 2) Buscar por keywords en el nombre
  for (const { keywords, icon } of MAPEOS) {
    if (keywords.some((k) => n.includes(k))) return icon;
  }

  // 3) Default: icono genérico de activo
  return DEFAULT_TIPO_ACTIVO_ICON;
}
