import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
const fmt = (d) => `${Number(d.slice(5, 7))}/${Number(d.slice(8, 10))}`;
export function BloodPressureChart({ data }) {
  const rows = [...data]
    .slice(0, 7)
    .reverse()
    .map((r) => ({ ...r, label: fmt(r.date) }));
  return (
    <ResponsiveContainer width="100%" height={230}>
      <LineChart data={rows} margin={{ top: 10, right: 8, left: -24, bottom: 0 }}>
        <CartesianGrid stroke="#edf1f4" vertical={false} />
        <XAxis
          dataKey="label"
          tickLine={false}
          axisLine={false}
          tick={{ fill: '#8a94a3', fontSize: 12 }}
        />
        <YAxis
          domain={[70, 130]}
          tickLine={false}
          axisLine={false}
          tick={{ fill: '#8a94a3', fontSize: 12 }}
        />
        <Tooltip />
        <Line
          type="monotone"
          dataKey="systolic"
          stroke="#16324F"
          strokeWidth={2.5}
          dot={{ r: 3 }}
        />
        <Line
          type="monotone"
          dataKey="diastolic"
          stroke="#2DBE9B"
          strokeWidth={2.5}
          dot={{ r: 3 }}
        />
      </LineChart>
    </ResponsiveContainer>
  );
}
export function StepsChart({ data }) {
  const rows = [...data]
    .slice(0, 7)
    .reverse()
    .map((r) => ({ ...r, label: fmt(r.date) }));
  return (
    <ResponsiveContainer width="100%" height={230}>
      <BarChart data={rows} margin={{ top: 10, right: 5, left: -25 }}>
        <CartesianGrid stroke="#edf1f4" vertical={false} />
        <XAxis
          dataKey="label"
          tickLine={false}
          axisLine={false}
          tick={{ fill: '#8a94a3', fontSize: 12 }}
        />
        <YAxis tickLine={false} axisLine={false} tick={{ fill: '#8a94a3', fontSize: 12 }} />
        <Tooltip />
        <Bar dataKey="steps" fill="#2DBE9B" radius={[6, 6, 0, 0]} maxBarSize={28} />
      </BarChart>
    </ResponsiveContainer>
  );
}
export function WeightChart({ data }) {
  const rows = [...data]
    .slice(0, 30)
    .reverse()
    .map((r) => ({ ...r, label: fmt(r.date) }));
  return (
    <ResponsiveContainer width="100%" height={230}>
      <AreaChart data={rows} margin={{ top: 10, right: 8, left: -25 }}>
        <defs>
          <linearGradient id="weight" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#2DBE9B" stopOpacity=".24" />
            <stop offset="100%" stopColor="#2DBE9B" stopOpacity="0" />
          </linearGradient>
        </defs>
        <CartesianGrid stroke="#edf1f4" vertical={false} />
        <XAxis
          dataKey="label"
          interval={5}
          tickLine={false}
          axisLine={false}
          tick={{ fill: '#8a94a3', fontSize: 12 }}
        />
        <YAxis
          domain={[67, 70]}
          tickLine={false}
          axisLine={false}
          tick={{ fill: '#8a94a3', fontSize: 12 }}
        />
        <Tooltip />
        <Area
          type="monotone"
          dataKey="weight"
          stroke="#2DBE9B"
          strokeWidth={2.5}
          fill="url(#weight)"
        />
      </AreaChart>
    </ResponsiveContainer>
  );
}
