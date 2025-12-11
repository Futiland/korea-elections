import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface PaginationProps {
	page: number;
	totalPages: number;
	isLoading?: boolean;
	onPageChange: (page: number) => void;
}

export default function Pagination({
	page,
	totalPages,
	isLoading = false,
	onPageChange,
}: PaginationProps) {
	const hasPrev = page > 1;
	const hasNext = page < totalPages;

	const handlePrev = () => {
		if (hasPrev) onPageChange(page - 1);
	};

	const handleNext = () => {
		if (hasNext) onPageChange(page + 1);
	};

	return (
		<nav
			className="flex items-center justify-between gap-3 rounded-lg border border-slate-200 bg-white px-3 py-2"
			aria-label="Pagination"
		>
			<Button
				variant="ghost"
				size="icon"
				onClick={handlePrev}
				disabled={!hasPrev || isLoading}
				className={cn(
					'h-9 w-9',
					!hasPrev || isLoading ? 'text-slate-300' : 'text-slate-700'
				)}
			>
				<ChevronLeft className="h-4 w-4" />
			</Button>

			<span className="text-sm text-slate-700">
				<span className="font-semibold text-blue-700">{page}</span>
				<span className="mx-1 text-slate-400">/</span>
				<span>{totalPages || 1}</span>
			</span>

			<Button
				variant="ghost"
				size="icon"
				onClick={handleNext}
				disabled={!hasNext || isLoading}
				className={cn(
					'h-9 w-9',
					!hasNext || isLoading ? 'text-slate-300' : 'text-slate-700'
				)}
			>
				<ChevronRight className="h-4 w-4" />
			</Button>
		</nav>
	);
}
