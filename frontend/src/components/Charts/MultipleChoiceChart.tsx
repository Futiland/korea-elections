import { OptionResultsDate } from '@/lib/types/poll';
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

type MultipleChoiceChartProps = {
	data: OptionResultsDate[];
	totalResponses: number;
	height?: number;
};

export default function MultipleChoiceChart({
	data,
	totalResponses,
	height = 300,
}: MultipleChoiceChartProps) {
	// 데이터를 투표 수 기준으로 내림차순 정렬
	// const sortedData = [...data].sort((a, b) => b.count - a.count);

	const color = '#2b7fff'; // blue

	const getTickStep = (maxValue: number) => {
		if (maxValue <= 10) return 1;
		if (maxValue <= 50) return 5;
		if (maxValue <= 100) return 10;
		if (maxValue <= 500) return 50;
		if (maxValue <= 1000) return 100;
		const magnitude = Math.pow(10, Math.floor(Math.log10(maxValue)) - 1);
		return magnitude > 0 ? magnitude * 5 : 100;
	};

	const axisTicks = useMemo(() => {
		const maxVoteCount =
			data.reduce((max, cur) => Math.max(max, cur.voteCount), 0) || 0;
		const step = getTickStep(maxVoteCount);
		const upperBound = Math.ceil(maxVoteCount / step) * step || step;

		const ticks: number[] = [];
		for (let i = 0; i <= upperBound; i += step) {
			ticks.push(i);
		}
		// 마지막 값이 upperBound가 아니면 추가
		if (ticks[ticks.length - 1] !== upperBound) {
			ticks.push(upperBound);
		}

		return ticks;
	}, [data]);

	const CustomTooltip = ({ active, payload, label }: any) => {
		if (active && payload && payload.length) {
			const data = payload[0].payload;
			return (
				<div className="bg-white p-3 border border-gray-200 rounded-lg shadow-lg">
					<p className="font-semibold text-gray-800">{label}</p>
					<p className="text-blue-600">
						<span className="font-bold">
							{data.voteCount.toLocaleString()}명
						</span>
					</p>
					<p className="text-gray-600">
						<span className="font-bold">{Math.round(data.percentage)}%</span>
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
					총 응답: {totalResponses.toLocaleString()}명
				</span>
			</div>
			<ResponsiveContainer width="100%" height={height}>
				<BarChart
					data={data}
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
						allowDecimals={false}
						ticks={axisTicks}
						domain={[0, axisTicks[axisTicks.length - 1] || 0]}
						label={{ value: '', position: 'insideBottom', offset: -5 }}
					/>
					<YAxis
						type="category"
						dataKey="optionText"
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
					<Bar dataKey="voteCount" fill={color} />
				</BarChart>
			</ResponsiveContainer>
		</div>
	);
}
