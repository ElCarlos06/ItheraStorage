import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

export default function TechnicianBarChart({ data = [] }) {
  return (
    <ResponsiveContainer width="100%" height={280}>
      <BarChart
        data={data}
        margin={{ top: 8, right: 8, left: 8, bottom: 24 }}
        layout="vertical"
      >
        <CartesianGrid strokeDasharray="3 3" stroke="var(--moon-beerus)" horizontal={false} />
        <XAxis
          type="number"
          tick={{ fill: "var(--moon-trunks)", fontSize: 12 }}
          tickLine={false}
          axisLine={false}
        />
        <YAxis
          type="category"
          dataKey="name"
          tick={{ fill: "var(--moon-trunks)", fontSize: 12 }}
          tickLine={false}
          axisLine={false}
          width={100}
        />
        <Tooltip
          contentStyle={{
            borderRadius: "8px",
            border: "1px solid var(--moon-beerus)",
          }}
        />
        <Bar
          dataKey="total"
          name="Mantenimientos"
          fill="var(--moon-teal)"
          radius={[0, 4, 4, 0]}
        />
      </BarChart>
    </ResponsiveContainer>
  );
}
