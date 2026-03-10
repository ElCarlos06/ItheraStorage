/**
 * Mocks guardados para Activos.
 * hay que usarlos pa probar despues xD
 */

export const MOCK_STATS = [
  { value: "247", label: "Total de Activos", badge: { text: "Total", status: "neutral" } },
  { value: "46", label: "Disponibles", badge: { text: "19%", status: "disponible" } },
  { value: "189", label: "Resguardados", badge: { text: "76%", status: "resguardado" } },
  { value: "12", label: "En Mantenimiento", badge: { text: "5%", status: "mantenimiento" } },
];

export const MOCK_ACTIVOS = [
  { id: "1", codigo: "ACT-001", descripcionCorta: "Laptop • Dell", nombre: "Laptop Dell Latitude 5420", asignadoA: "-", tipoActivo: "Laptop", status: "disponible", campus: "Universidad Tecnológica Emiliano Zapata", edificio: "D1", aula: "A1" },
  { id: "2", codigo: "ACT-002", descripcionCorta: "Impresora • HP", nombre: "Impresora HP LaserJet Pro", asignadoA: "Juan Pérez", tipoActivo: "Periférico", status: "resguardado", campus: "Campus Norte", edificio: "A2", aula: "B3" },
  { id: "3", codigo: "ACT-003", descripcionCorta: "Monitor • LG", nombre: "Monitor LG 24\"", asignadoA: "-", tipoActivo: "Equipo de cómputo", status: "mantenimiento", campus: "Universidad Tecnológica Emiliano Zapata", edificio: "D1", aula: "A1" },
  { id: "4", codigo: "ACT-004", descripcionCorta: "Proyector • Epson", nombre: "Proyector Epson", asignadoA: "-", tipoActivo: "Audiovisual", status: "en proceso", campus: "Campus Centro", edificio: "C1", aula: "Sala 2" },
  { id: "5", codigo: "ACT-005", descripcionCorta: "Escáner • Canon", nombre: "Escáner Canon", asignadoA: "-", tipoActivo: "Periférico", status: "reportado", campus: "Universidad Tecnológica Emiliano Zapata", edificio: "D1", aula: "A1" },
];
