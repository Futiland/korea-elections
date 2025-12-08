import React, { useMemo } from 'react';
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
import { ScoreResultDate } from '@/lib/types/poll';

type ScoreChartProps = {
	data?: ScoreResultDate;
	totalResponses: number;
	height?: number;
};

export default function ScoreChart({
	data,
	totalResponses,
	height = 300,
}: ScoreChartProps) {
	const getTickStep = (maxValue: number) => {
		if (maxValue <= 10) return 1;
		if (maxValue <= 50) return 5;
		if (maxValue <= 100) return 10;
		if (maxValue <= 500) return 50;
		if (maxValue <= 1000) return 100;
		const magnitude = Math.pow(10, Math.floor(Math.log10(maxValue)) - 1);
		return magnitude > 0 ? magnitude * 5 : 100;
	};

	// 1-10점까지 모든 점수에 대한 데이터 생성
	const chartData = Array.from({ length: 10 }, (_, index) => {
		const score = index + 1;
		const count = data?.scoreDistribution?.[score] ?? 0;
		const percentage = totalResponses > 0 ? (count / totalResponses) * 100 : 0;
		return {
			score,
			count,
			percentage,
		};
	});

	const axisTicks = useMemo(() => {
		const maxCount = chartData.reduce(
			(max, cur) => Math.max(max, cur.count),
			0
		);
		const step = getTickStep(maxCount);
		const upperBound = Math.ceil(maxCount / step) * step || step;

		const ticks: number[] = [];
		for (let i = 0; i <= upperBound; i += step) {
			ticks.push(i);
		}
		if (ticks[ticks.length - 1] !== upperBound) {
			ticks.push(upperBound);
		}
		return ticks;
	}, [chartData]);

	const CustomTooltip = ({ active, payload, label }: any) => {
		if (active && payload && payload.length) {
			const data = payload[0].payload;
			return (
				<div className="bg-white py-2 px-3 rounded-lg shadow-lg flex justify-start items-center gap-1">
					<p className="text-blue-600 font-bold">
						{data.count.toLocaleString()}명
					</p>
					<p className="text-gray-600 font-bold">
						({Math.round(data.percentage)}%)
					</p>
				</div>
			);
		}
		return null;
	};

	return (
		<div className="w-full">
			<div className="flex items-center justify-between mb-4">
				<h3 className="text-lg font-semibold">투표 결과</h3>
				<span className="text-sm text-gray-500">
					총 응답: {totalResponses.toLocaleString()}명 • 평균:{' '}
					{Math.round(data?.averageScore ?? 0)}점
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
						allowDecimals={false}
						ticks={axisTicks}
						domain={[0, axisTicks[axisTicks.length - 1] || 0]}
						label={{
							value: '명',
							// angle: 0,
							position: 'insideLeft',
							// offset: -5,
						}}
					/>
					<Tooltip content={<CustomTooltip />} />
					<Bar dataKey="count" radius={[8, 8, 0, 0]}>
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
