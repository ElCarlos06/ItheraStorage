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
          domain={[0, 8]}
        />
        <Tooltip
          contentStyle={{
            borderRadius: "8px",
            border: "1px solid var(--moon-beerus)",
          }}
          labelStyle={{ color: "var(--moon-bulma)" }}
        />
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
        />
        <Line
          type="monotone"
          dataKey="preventivo"
          name="Preventivo"
          stroke="var(--moon-teal-90)"
          strokeWidth={2}
          dot={{ fill: "var(--moon-teal-90)", r: 4 }}
        />
      </LineChart>
    </ResponsiveContainer>
  );
}
