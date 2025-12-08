import { useState } from 'react';

export type FilterOption = 'latest' | 'popular' | 'ended';

interface PollFilterProps {
	selectedFilter: FilterOption;
	onFilterChange: (filter: FilterOption) => void;
	className?: string;
}

const filterOptions = [
	{ value: 'latest' as FilterOption, label: '최신순' },
	{ value: 'popular' as FilterOption, label: '인기순' },
	{ value: 'ended' as FilterOption, label: '종료된 투표' },
];

export default function PollFilter({
	selectedFilter,
	onFilterChange,
	className = '',
}: PollFilterProps) {
	return (
		<div className={`flex gap-2 ${className}`}>
			{filterOptions.map((option) => (
				<button
					key={option.value}
					onClick={() => onFilterChange(option.value)}
					className={`px-4 py-2 text-sm font-medium rounded-full border transition-colors ${
						selectedFilter === option.value
							? 'border-blue-800 bg-blue-800 text-white hover:bg-blue-700'
							: 'border-slate-300 bg-white text-slate-700 hover:bg-slate-50 hover:text-slate-900'
					}`}
				>
					{option.label}
				</button>
			))}
		</div>
	);
}
