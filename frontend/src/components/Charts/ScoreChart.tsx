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

type ScoreData = {
	score: number;
	count: number;
	percentage: number;
};

type ScoreChartProps = {
	data: ScoreData[];
	totalResponses: number;
	height?: number;
};

export default function ScoreChart({
	data,
	totalResponses,
	height = 300,
}: ScoreChartProps) {
	// 1-10점까지 모든 점수에 대한 데이터 생성
	const chartData = Array.from({ length: 10 }, (_, index) => {
		const score = index + 1;
		const existingData = data.find((item) => item.score === score);
		return {
			score,
			count: existingData?.count || 0,
			percentage: existingData?.percentage || 0,
		};
	});

	const CustomTooltip = ({ active, payload, label }: any) => {
		if (active && payload && payload.length) {
			const data = payload[0].payload;
			return (
				<div className="bg-white p-3 border border-gray-200 rounded-lg shadow-lg">
					<p className="font-semibold text-gray-800">{label}점</p>
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
				<h3 className="text-lg font-semibold">점수별 투표 결과</h3>
				<span className="text-sm text-gray-500">
					총 응답: {totalResponses.toLocaleString()}명
				</span>
			</div>
			<ResponsiveContainer width="100%" height={height}>
				<BarChart
					data={chartData}
					margin={{
						top: 20,
						right: 30,
						// left: 20,
						bottom: 5,
					}}
				>
					<CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
					<XAxis
						dataKey="score"
						tick={{ fontSize: 12 }}
						label={{ value: '', position: 'insideBottom' }}
					/>
					<YAxis
						tick={{ fontSize: 12 }}
						tickFormatter={(value) => value.toLocaleString()}
						label={{
							value: '명',
							// angle: 0,
							position: 'insideLeft',
							// offset: -5,
						}}
					/>
					<Tooltip content={<CustomTooltip />} />
					<Bar dataKey="count" radius={[4, 4, 0, 0]}>
						{chartData.map((entry, index) => (
							<Cell
								key={`cell-${index}`}
								fill={entry.count > 0 ? '#2b7fff' : '#E5E7EB'}
							/>
						))}
					</Bar>
				</BarChart>
			</ResponsiveContainer>
		</div>
	);
}
