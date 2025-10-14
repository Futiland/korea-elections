import React from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';

type SingleChoiceData = {
	option: string;
	count: number;
	percentage: number;
};

type SingleChoiceChartProps = {
	data: SingleChoiceData[];
	totalResponses: number;
	height?: number;
};

export default function SingleChoiceChart({
	data,
	totalResponses,
	height = 300,
}: SingleChoiceChartProps) {
	// 데이터를 투표 수 기준으로 내림차순 정렬
	const sortedData = [...data].sort((a, b) => b.count - a.count);

	const colors = [
		'#3b82f6', // blue
		'#ef4444', // red
		'#22c55e', // green
		'#ca8a04', // yellow
		'#9333ea', // purple
		'#db2777', // pink
		'#0891b2', // cyan
		'#ea580c', // orange
		'#65a30d', // lime
		'#4f46e5', // indigo
	];

	// const colors = [
	// 	'#3B82F6', // blue
	// 	'#EF4444', // red
	// 	'#10B981', // green
	// 	'#F59E0B', // yellow
	// 	'#8B5CF6', // purple
	// 	'#EC4899', // pink
	// 	'#06B6D4', // cyan
	// 	'#84CC16', // lime
	// 	'#F97316', // orange
	// 	'#6366F1', // indigo
	// ];

	const CustomTooltip = ({ active, payload }: any) => {
		if (active && payload && payload.length) {
			const data = payload[0].payload;
			return (
				<div className="bg-white p-3 border border-gray-200 rounded-lg shadow-lg">
					<p className="font-semibold text-gray-800">{data.option}</p>
					<p className="text-blue-600">
						명수:{' '}
						<span className="font-bold">{data.count.toLocaleString()}</span>
					</p>
					<p className="text-gray-600">
						비율:{' '}
						<span className="font-bold">{data.percentage.toFixed(1)}%</span>
					</p>
				</div>
			);
		}
		return null;
	};

	const CustomLegend = ({ payload }: any) => {
		return (
			<div className="flex flex-col gap-2 mt-4">
				{payload?.map((entry: any, index: number) => (
					<div
						key={index}
						className="flex items-center justify-between text-sm"
					>
						<div className="flex items-center gap-2">
							<div
								className="w-4 h-4 rounded-full"
								style={{ backgroundColor: entry.color }}
							/>
							<span className="text-gray-700">{entry.value}</span>
						</div>
						<div className="text-right">
							<span className="font-semibold text-gray-800">
								{entry.payload.count.toLocaleString()}명
							</span>
							<span className="text-gray-500 ml-2">
								({entry.payload.percentage.toFixed(1)}%)
							</span>
						</div>
					</div>
				))}
			</div>
		);
	};

	// 파이 조각 내부에 비율을 렌더링하는 커스텀 라벨러
	const renderSliceLabel = ({
		cx,
		cy,
		midAngle,
		innerRadius,
		outerRadius,
		percent,
	}: any) => {
		if (!percent || percent < 0.03) return null; // 3% 미만은 가독성 위해 숨김
		const RAD = Math.PI / 180;
		const r = innerRadius + (outerRadius - innerRadius) * 0.5;
		const x = cx + r * Math.cos(-midAngle * RAD);
		const y = cy + r * Math.sin(-midAngle * RAD);
		return (
			<text
				x={x}
				y={y}
				fill="#ffffff"
				textAnchor="middle"
				dominantBaseline="central"
				fontSize={12}
				fontWeight={600}
			>
				{`${(percent * 100).toFixed(1)}%`}
			</text>
		);
	};

	return (
		<div className="w-full">
			<div className="flex items-center justify-between mb-4">
				<h3 className="text-lg font-semibold">단일선택 투표 결과</h3>
				<span className="text-sm text-gray-500">
					총 응답: {totalResponses.toLocaleString()}명
				</span>
			</div>
			<div className="flex gap-6">
				<div className="flex-1">
					<ResponsiveContainer width="100%" height={height}>
						<PieChart>
							<Pie
								data={sortedData}
								cx="50%"
								cy="50%"
								labelLine={false}
								label={renderSliceLabel}
								outerRadius={80}
								fill="#8884d8"
								dataKey="count"
							>
								{sortedData.map((entry, index) => (
									<Cell
										key={`cell-${index}`}
										fill={colors[index % colors.length]}
									/>
								))}
							</Pie>
							<Tooltip content={<CustomTooltip />} />
						</PieChart>
					</ResponsiveContainer>
				</div>
				<div className="w-56">
					{/* 차트 외부 사이드 패널로 범례 표시 */}
					<CustomLegend
						payload={sortedData.map((d, i) => ({
							value: d.option,
							color: colors[i % colors.length],
							payload: d,
						}))}
					/>
				</div>
			</div>
		</div>
	);
}
