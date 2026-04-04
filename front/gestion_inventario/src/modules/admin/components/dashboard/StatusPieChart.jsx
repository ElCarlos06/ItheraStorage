import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Legend,
  Tooltip,
} from "recharts";

const COLORS = [
  "var(--moon-roshi-70)",
  "var(--moon-piccolo-60)",
  "var(--moon-krillin-70)",
];

export default function StatusPieChart({ data = [] }) {
  return (
    <ResponsiveContainer width="100%" height={240}>
      <PieChart>
        <Pie
          data={data}
          cx="50%"
          cy="50%"
          innerRadius={60}
          outerRadius={90}
          paddingAngle={2}
          dataKey="value"
          nameKey="name"
          label={({ name, value }) => `${value}`}
        >
          {data.map((_, index) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip
          formatter={(value, name) => [`${value}`, name]}
          contentStyle={{
            borderRadius: "8px",
            border: "1px solid var(--moon-beerus)",
          }}
        />
        <Legend
          layout="horizontal"
          align="center"
          verticalAlign="bottom"
          formatter={(value, item) => (
            <span style={{ color: "var(--moon-trunks)", fontSize: 14 }}>
              {value}: {item.payload?.value}
            </span>
          )}
        />
      </PieChart>
    </ResponsiveContainer>
  );
}
