import {
  ShopBag,
  GenericUser,
  GenericSettings,
  NotificationsBell,
  ChartPieChart,
  TimeClock,
  GenericUsers,
  NotificationsAlert,
} from "@heathmont/moon-icons";
import PageHeader from "../../components/dashboard/PageHeader";
import StatCard from "../../components/dashboard/StatCard";
import ChartCard from "../../components/dashboard/ChartCard";
import StatusPieChart from "../../components/dashboard/StatusPieChart";
import AttentionLineChart from "../../components/dashboard/AttentionLineChart";
import TechnicianBarChart from "../../components/dashboard/TechnicianBarChart";
import ReportedAssetsList from "../../components/dashboard/ReportedAssetsList";

const STATS = [
  {
    icon: ShopBag,
    value: "247",
    label: "Total de Activos",
    badge: { text: "+12%", status: "disponible" },
  },
  {
    icon: GenericUser,
    value: "189",
    label: "Resguardados",
    badge: { text: "+8%", status: "resguardado" },
  },
  {
    icon: GenericSettings,
    value: "12",
    label: "En Mantenimiento",
    badge: { text: "12", status: "mantenimiento" },
  },
  {
    icon: NotificationsBell,
    value: "8",
    label: "Reportes Activos",
    badge: { text: "+2", status: "reportado" },
  },
];

const PIE_DATA = [
  { name: "Disponible", value: 46 },
  { name: "Resguardado", value: 189 },
  { name: "Mantenimiento", value: 12 },
];

const LINE_DATA = [
  { month: "Ene", correctivo: 4.5, preventivo: 2.2 },
  { month: "Feb", correctivo: 4.8, preventivo: 2.5 },
  { month: "Mar", correctivo: 4.2, preventivo: 2.8 },
  { month: "Abr", correctivo: 5, preventivo: 2.5 },
  { month: "May", correctivo: 4.6, preventivo: 3 },
  { month: "Jun", correctivo: 4.4, preventivo: 2.6 },
];

const BAR_DATA = [
  { name: "José García", total: 24 },
  { name: "María Sánchez", total: 22 },
  { name: "Carlos Ramírez", total: 19 },
  { name: "Laura Martínez", total: 17 },
];

const REPORTED_ASSETS = [
  { id: 1, name: "Laptop Dell Latitude 5420", type: "Hardware", reports: 12 },
  { id: 2, name: "Impresora HP LaserJet Pro", type: "Hardware", reports: 9 },
  { id: 3, name: "Monitor LG 24\"", type: "Hardware", reports: 6 },
];

export default function Dashboard() {
  return (
    <>
      <PageHeader
        overline="Panel de Control - Administrador"
        title="Bienvenido al Sistema"
        subtitle="Gestiona activos, resguardos y mantenimientos con métricas en tiempo real"
      />

      <section className="dashboard-stats" aria-label="Resumen de métricas">
        <div className="row g-3 mb-4">
          {STATS.map((stat, i) => (
            <div key={i} className="col-12 col-sm-6 col-xl-3">
              <StatCard {...stat} />
            </div>
          ))}
        </div>
      </section>

      <section className="dashboard-charts mb-4">
        <div className="row g-3">
          <div className="col-12 col-xl-6">
            <ChartCard
              icon={ChartPieChart}
              title="Activos por Estatus"
              subtitle="Distribución actual del inventario"
              iconBg="piccolo"
            >
              <StatusPieChart data={PIE_DATA} />
            </ChartCard>
          </div>
          <div className="col-12 col-xl-6">
            <ChartCard
              icon={TimeClock}
              title="Tiempo Promedio de Atención"
              subtitle="Horas por tipo de mantenimiento"
              iconBg="teal"
            >
              <AttentionLineChart data={LINE_DATA} />
            </ChartCard>
          </div>
        </div>
      </section>

      <section className="dashboard-charts">
        <div className="row g-3">
          <div className="col-12 col-xl-6">
            <ChartCard
              icon={GenericUsers}
              title="Mantenimientos por Técnico"
              subtitle="Carga de trabajo y desempeño"
              iconBg="purple"
            >
              <TechnicianBarChart data={BAR_DATA} />
            </ChartCard>
          </div>
          <div className="col-12 col-xl-6">
            <ChartCard
              icon={NotificationsAlert}
              title="Activos Más Reportados"
              subtitle="Equipos con más incidencias"
              iconBg="chichi"
            >
              <ReportedAssetsList items={REPORTED_ASSETS} />
            </ChartCard>
          </div>
        </div>
      </section>
    </>
  );
}
