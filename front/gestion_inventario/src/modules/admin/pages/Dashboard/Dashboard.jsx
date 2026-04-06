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
import EmptyState from "../../../../components/EmptyState/EmptyState";
import { activosApi } from "../../../../api/activosApi";
import { solicitudesApi } from "../../../../api/solicitudesApi";
import { useQuery } from "@tanstack/react-query";

// Helper para formatear los badges
const fmtBadge = (val, suffix = "") =>
  val == null ? "0" : `${val > 0 ? "+" : ""}${val}${suffix}`;

const MESES = {
  January: "Enero",
  February: "Febrero",
  March: "Marzo",
  April: "Abril",
  May: "Mayo",
  June: "Junio",
  July: "Julio",
  August: "Agosto",
  September: "Septiembre",
  October: "Octubre",
  November: "Noviembre",
  December: "Diciembre",
};

export default function Dashboard() {
  const { data: activosStats } = useQuery({
    queryKey: ["activosStats"],
    queryFn: () => activosApi.getStats(),
  });

  const { data: mantenimientosStats } = useQuery({
    queryKey: ["mantenimientosStats"],
    queryFn: () => solicitudesApi.mantenimientos.getStats(),
  });

  const { data: reportesStats } = useQuery({
    queryKey: ["reportesStats"],
    queryFn: () => solicitudesApi.reportes.getStats(),
  });


  const { promedioAtencion = [], tecnicoMantenimiento = [] } =
    mantenimientosStats ?? {};

  const STATS = [
    {
      icon: ShopBag,
      value: activosStats?.total ?? "...",
      label: "Total de Activos",
      badge: {
        text: fmtBadge(activosStats?.pctTotal, "%"),
        status: "disponible",
      },
    },
    {
      icon: GenericUser,
      value: activosStats?.resguardados ?? "...",
      label: "Resguardados",
      badge: {
        text: fmtBadge(activosStats?.pctResguardados, "%"),
        status: "resguardado",
      },
    },
    {
      icon: GenericSettings,
      value: activosStats?.enMantenimiento ?? "...",
      label: "En Mantenimiento",
      badge: {
        text: fmtBadge(activosStats?.pctMantenimiento, "%"),
        status: "mantenimiento",
      },
    },
    {
      icon: NotificationsBell,
      value: activosStats?.reportados ?? "...",
      label: "Reportes Activos",
      badge: {
        text: fmtBadge(activosStats?.pctReportados, "%"),
        status: "reportado",
      },
    },
  ];

  const PIE_DATA = [
    { name: "Disponible", value: activosStats?.disponibles },
    { name: "Resguardado", value: activosStats?.resguardados },
    { name: "Mantenimiento", value: activosStats?.enMantenimiento },
  ];

  const currentMonth = new Date().getMonth() + 1;
  const isFirstSemester = currentMonth <= 6;
  const semesterMonths = isFirstSemester
    ? ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio"]
    : ["Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];

  const LINE_DATA = semesterMonths.map((m) => ({
    month: m,
    correctivo: 0,
    preventivo: 0,
  }));

  promedioAtencion.forEach((curr) => {
    const target = LINE_DATA.find((item) => item.month === MESES[curr.mes]);
    if (target) {
      if (curr.tipoMantenimiento === "CORRECTIVO") {
        target.correctivo = curr.promedioHoras;
      } else {
        target.preventivo = curr.promedioHoras;
      }
    }
  });

  const BAR_DATA = tecnicoMantenimiento.map((t) => ({
    name: t.tecnico,
    total: t.numMantenimientos,
  }));

  const REPORTED_ASSETS = reportesStats?.map((r, idx) => ({
    id: idx + 1,
    name: r.nombreActivo,
    type: r.tipoActivo,
    reports: r.numReportes,
  }));

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
        <div className="row g-3 align-items-stretch">
          <div className="col-12 col-xl-6 d-flex">
            <ChartCard
              className="w-100 h-100"
              icon={GenericUsers}
              title="Mantenimientos por Técnico"
              subtitle="Carga de trabajo y desempeño"
              iconBg="purple"
            >
              {BAR_DATA.length > 0 ? (
                <TechnicianBarChart data={BAR_DATA} />
              ) : (
                <EmptyState
                  message="No hay mantenimientos de técnicos registrados."
                  hasSearch={false}
                />
              )}
            </ChartCard>
          </div>
          <div className="col-12 col-xl-6 d-flex">
            <ChartCard
              className="w-100 h-100"
              icon={NotificationsAlert}
              title="Activos Más Reportados"
              subtitle="Equipos con más incidencias"
              iconBg="chichi"
            >
              {(REPORTED_ASSETS?.length ?? 0) > 0 ? (
                <ReportedAssetsList items={REPORTED_ASSETS} />
              ) : (
                <EmptyState
                  message="No hay activos con reportes registrados."
                  hasSearch={false}
                />
              )}
            </ChartCard>
          </div>
        </div>
      </section>
    </>
  );
}
