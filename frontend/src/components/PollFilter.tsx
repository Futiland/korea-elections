import type { PollStatus, PollSort } from '@/lib/types/poll';

export type FilterOptionId = 'LATEST' | 'POPULAR' | 'ENDING_SOON' | 'EXPIRED';

export type FilterOption = {
	id: FilterOptionId;
	label: string;
	sort?: PollSort;
	status?: Extract<PollStatus, 'IN_PROGRESS' | 'EXPIRED' | 'CANCELLED'> | 'ALL';
};

export const filterOptions: FilterOption[] = [
	{ id: 'LATEST', label: '최신순', sort: 'LATEST', status: 'IN_PROGRESS' },
	{ id: 'POPULAR', label: '인기순', sort: 'POPULAR', status: 'IN_PROGRESS' },
	{ id: 'ENDING_SOON', label: '마감 임박순', sort: 'ENDING_SOON' },
	{ id: 'EXPIRED', label: '종료된 투표', status: 'EXPIRED' },
];

interface PollFilterProps {
	selectedFilter: FilterOption;
	onFilterChange: (filter: FilterOption) => void;
	className?: string;
}

export default function PollFilter({
	selectedFilter,
	onFilterChange,
	className = '',
}: PollFilterProps) {
	return (
		<div className={`flex gap-2 ${className}`}>
			{filterOptions.map((option) => (
				<button
					key={option.id}
					onClick={() => onFilterChange(option)}
					className={`px-3 sm:px-4 py-1 sm:py-2 text-sm font-medium rounded-full border transition-colors ${
						selectedFilter.id === option.id
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
