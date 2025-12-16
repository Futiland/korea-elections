import { useState } from 'react';
import { Search, X } from 'lucide-react';
import PollFilter, { FilterOption } from './PollFilter';

interface PollSearchAndFilterProps {
	onSearchChange: (searchTerm: string) => void;
	onFilterChange: (filter: FilterOption) => void;
	searchTerm: string;
	selectedFilter: FilterOption;
	className?: string;
	isFilterVisible?: boolean;
}

export default function PollSearchAndFilter({
	onSearchChange,
	onFilterChange,
	searchTerm,
	selectedFilter,
	className = '',
	isFilterVisible = true,
}: PollSearchAndFilterProps) {
	const [localSearchTerm, setLocalSearchTerm] = useState(searchTerm);

	const handleSearchSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		onSearchChange(localSearchTerm);
	};

	const handleClearSearch = () => {
		setLocalSearchTerm('');
		onSearchChange('');
	};

	return (
		<div className={`space-y-4 ${className}`}>
			{/* 검색 입력 */}
			<form onSubmit={handleSearchSubmit} className="relative">
				<div className="relative">
					<Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-slate-400" />
					<input
						type="text"
						value={localSearchTerm}
						onChange={(e) => setLocalSearchTerm(e.target.value)}
						placeholder="투표 제목이나 내용을 검색하세요."
						className="w-full pl-10 pr-10 py-3 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent bg-white"
					/>
					{localSearchTerm && (
						<button
							type="button"
							onClick={handleClearSearch}
							className="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-400 hover:text-slate-600"
						>
							<X className="w-4 h-4" />
						</button>
					)}
				</div>
			</form>

			{/* 필터 버튼들 */}
			{isFilterVisible && (
				<PollFilter
					selectedFilter={selectedFilter}
					onFilterChange={onFilterChange}
				/>
			)}
		</div>
	);
}
