import React from 'react';
import {
	BarChart,
	Bar,
	XAxis,
	YAxis,
	CartesianGrid,
	Tooltip,
	ResponsiveContainer,
	Cell,
} from 'recharts';

type MultipleChoiceData = {
	option: string;
	count: number;
	percentage: number;
};

type MultipleChoiceChartProps = {
	data: MultipleChoiceData[];
	totalResponses: number;
	height?: number;
};

export default function MultipleChoiceChart({
	data,
	totalResponses,
	height = 300,
}: MultipleChoiceChartProps) {
	// 데이터를 투표 수 기준으로 내림차순 정렬
	const sortedData = [...data].sort((a, b) => b.count - a.count);

	// 디버깅을 위한 콘솔 출력
	console.log('MultipleChoiceChart data:', sortedData);

	const color = '#2b7fff'; // blue

	const CustomTooltip = ({ active, payload, label }: any) => {
		if (active && payload && payload.length) {
			const data = payload[0].payload;
			return (
				<div className="bg-white p-3 border border-gray-200 rounded-lg shadow-lg">
					<p className="font-semibold text-gray-800">{label}</p>
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

	return (
		<div className="w-full">
			<div className="flex items-center justify-between mb-4">
				<h3 className="text-lg font-semibold">다중선택 투표 결과</h3>
				<span className="text-sm text-gray-500">
					총 응답: {totalResponses.toLocaleString()}명
				</span>
			</div>
			<ResponsiveContainer width="100%" height={height}>
				<BarChart
					data={sortedData}
					layout="vertical"
					margin={{
						top: 20,
						right: 30,
						// left: 50,
						bottom: 5,
					}}
				>
					<CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
					<XAxis
						type="number"
						tick={{ fontSize: 12 }}
						tickFormatter={(value) => value.toLocaleString()}
						label={{ value: '', position: 'insideBottom', offset: -5 }}
					/>
					<YAxis
						type="category"
						dataKey="option"
						tick={{ fontSize: 12 }}
						max-width={200}
						min-width={60}
					/>
					<Tooltip content={<CustomTooltip />} />
					{/* <Bar dataKey="count" fill="#3B82F6">
						{sortedData.map((entry, index) => (
							<Cell
								key={`cell-${index}`}
								fill={colors[index % colors.length]}
							/>
						))}
					</Bar> */}
					<Bar dataKey="count" fill={color} />
				</BarChart>
			</ResponsiveContainer>
		</div>
	);
}
