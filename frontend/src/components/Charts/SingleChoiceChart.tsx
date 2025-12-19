import { OptionResultsDate } from '@/lib/types/poll';
import React from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';

type SingleChoiceChartProps = {
	data: OptionResultsDate[];
	totalResponses: number;
	height?: number;
};

export default function SingleChoiceChart({
	data,
	totalResponses,
	height = 200,
}: SingleChoiceChartProps) {
	const colors = [
		'#3B82F6', // blue
		'#EF4444', // red
		'#10B981', // green
		'#F59E0B', // yellow
		'#8B5CF6', // purple
		'#EC4899', // pink
		'#06B6D4', // cyan
		'#84CC16', // lime
		'#F97316', // orange
		'#6366F1', // indigo
	];

	const CustomTooltip = ({ active, payload }: any) => {
		if (active && payload && payload.length) {
			const data = payload[0].payload;
			return (
				<div className="bg-white py-1.5 px-2 sm:py-2 sm:px-3 rounded-lg shadow-lg flex flex-col items-start gap-1 max-w-[260px]">
					<p className="font-semibold text-gray-800 text-xs sm:text-sm break-words leading-snug">
						{data.optionText}
					</p>
					<div className="flex items-baseline gap-1 text-xs sm:text-sm">
						<p className="text-blue-600 font-bold">
							{data.voteCount.toLocaleString()}명
						</p>
						<p className="text-gray-600 font-bold">
							({Math.round(data.percentage)}%)
						</p>
					</div>
				</div>
			);
		}
		return null;
	};

	const CustomLegend = ({ payload }: any) => {
		return (
			<div className="flex flex-col gap-2 sm:mt-4 mb-6">
				{payload?.map((entry: any, index: number) => (
					<div key={index} className="flex items-start gap-2 text-sm min-w-0">
						<div
							className="w-4 h-4 rounded-full flex-shrink-0 mt-0.5"
							style={{ backgroundColor: entry.color }}
						/>
						<div className="flex-1 min-w-0">
							<div className="flex items-center justify-between gap-2 flex-wrap">
								<span className="text-gray-700 break-words line-clamp-2 flex-1 min-w-0">
									{entry.value}
								</span>
								<div className="text-right flex-shrink-0 whitespace-nowrap">
									<span className="font-semibold text-gray-800">
										{entry.payload.voteCount.toLocaleString()}명
									</span>
									<span className="text-gray-500 ml-1">
										({Math.round(entry.payload.percentage)}%)
									</span>
								</div>
							</div>
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
				{`${Math.round(percent * 100)}%`}
			</text>
		);
	};

	return (
		<div className="w-full">
			<div className="flex items-center justify-between mb-4">
				<h3 className="text-lg font-semibold">투표 결과</h3>
				<span className="text-sm text-gray-500">
					총 응답: {totalResponses.toLocaleString()}명
				</span>
			</div>
			<div className="flex flex-col sm:flex-row gap-3 sm:items-start">
				<div className="w-full sm:min-w-[160px] sm:flex-1 flex justify-center sm:justify-start">
					<ResponsiveContainer width="100%" height={height} className="">
						<PieChart>
							<Pie
								data={data}
								cx="50%"
								cy="50%"
								labelLine={false}
								label={renderSliceLabel}
								outerRadius={80}
								fill="#8884d8"
								dataKey="voteCount"
							>
								{data.map((entry, index) => (
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
				<div className="w-full sm:flex-1 min-w-0">
					{/* 차트 외부 사이드 패널로 범례 표시 */}
					<CustomLegend
						payload={data.map((d, i) => ({
							value: d.optionText,
							color: colors[i % colors.length],
							payload: d,
						}))}
					/>
				</div>
			</div>
		</div>
	);
}
