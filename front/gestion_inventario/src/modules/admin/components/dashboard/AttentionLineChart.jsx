import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;

  // Extraemos la información del mes desde el primer elemento del payload
  const localData = payload[0].payload;

  return (
    <div
      style={{
        background: "var(--moon-gohan)",
        border: "1px solid var(--moon-beerus)",
        borderRadius: 8,
        padding: "10px 14px",
        boxShadow: "0 4px 12px rgba(0,0,0,.12)",
      }}
    >
      <p
        style={{
          margin: 0,
          marginBottom: 8,
          fontWeight: 600,
          color: "var(--moon-bulma)",
        }}
      >
        {label}
      </p>

      {/* Info Mantenimiento Correctivo */}
      <div style={{ marginBottom: 6 }}>
        <p style={{ margin: 0, fontSize: 13, color: "var(--moon-chichi-100)", fontWeight: 600 }}>
          Correctivo
        </p>
        <p style={{ margin: "2px 0 0 0", fontSize: 12, color: "var(--moon-trunks)" }}>
          Promedio: <strong>{localData.correctivo?.toFixed(2) ?? "0.00"} hrs</strong>
        </p>
        <p style={{ margin: "2px 0 0 0", fontSize: 12, color: "var(--moon-trunks)" }}>
          Cantidad: <strong>{localData.numCorrectivo ?? 0} {localData.numCorrectivo === 1 ? 'mantenimiento' : 'mantenimientos'}</strong>
        </p>
      </div>

      {/* Info Mantenimiento Preventivo */}
      <div>
        <p style={{ margin: 0, fontSize: 13, color: "var(--moon-teal-100)", fontWeight: 600 }}>
          Preventivo
        </p>
        <p style={{ margin: "2px 0 0 0", fontSize: 12, color: "var(--moon-trunks)" }}>
          Promedio: <strong>{localData.preventivo?.toFixed(2) ?? "0.00"} hrs</strong>
        </p>
        <p style={{ margin: "2px 0 0 0", fontSize: 12, color: "var(--moon-trunks)" }}>
          Cantidad: <strong>{localData.numPreventivo ?? 0} {localData.numPreventivo === 1 ? 'mantenimiento' : 'mantenimientos'}</strong>
        </p>
      </div>
    </div>
  );
};

export default function AttentionLineChart({ data = [] }) {
  return (
    <ResponsiveContainer width="100%" height={240}>
      <LineChart data={data} margin={{ top: 8, right: 8, left: 8, bottom: 8 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="var(--moon-beerus)" />
        <XAxis
          dataKey="month"
          tick={{ fill: "var(--moon-trunks)", fontSize: 12 }}
          tickLine={false}
        />
        <YAxis
          tick={{ fill: "var(--moon-trunks)", fontSize: 12 }}
          tickLine={false}
          axisLine={false}
          domain={['dataMin', 'dataMax + 1']}
        />
        <Tooltip content={<CustomTooltip />} />
        <Legend
          wrapperStyle={{ fontSize: 14 }}
          formatter={(value) => (
            <span style={{ color: "var(--moon-trunks)" }}>{value}</span>
          )}
        />
        <Line
          type="monotone"
          dataKey="correctivo"
          name="Correctivo"
          stroke="var(--moon-chichi-90)"
          strokeWidth={2}
          dot={{ fill: "var(--moon-chichi-90)", r: 4 }}
          activeDot={{ r: 6 }}
        />
        <Line
          type="monotone"
          dataKey="preventivo"
          name="Preventivo"
          stroke="var(--moon-teal-90)"
          strokeWidth={2}
          dot={{ fill: "var(--moon-teal-90)", r: 4 }}
          activeDot={{ r: 6 }}
        />
      </LineChart>
    </ResponsiveContainer>
  );
}
