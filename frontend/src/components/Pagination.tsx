import { ChevronLeft, ChevronRight } from 'lucide-react';
import {
	Pagination as ShadPagination,
	PaginationContent,
	PaginationEllipsis,
	PaginationItem,
	PaginationLink,
} from '@/components/ui/pagination';

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
	const hasPrev = page > 1 && !isLoading;
	const hasNext = page < totalPages && !isLoading;

	const goTo = (target: number) => {
		if (isLoading) return;
		if (target < 1 || target > totalPages) return;
		if (target === page) return;
		onPageChange(target);
	};

	const pagesToRender = (() => {
		if (totalPages <= 5) {
			return Array.from({ length: totalPages }, (_, i) => i + 1);
		}

		const set = new Set<number>([1, totalPages, page - 1, page, page + 1]);
		return Array.from(set)
			.filter((p) => p >= 1 && p <= totalPages)
			.sort((a, b) => a - b);
	})();

	const renderPages = () => {
		const items: (number | 'ellipsis')[] = [];
		for (let i = 0; i < pagesToRender.length; i++) {
			const current = pagesToRender[i];
			const prev = pagesToRender[i - 1];
			if (prev !== undefined && current - prev > 1) {
				items.push('ellipsis');
			}
			items.push(current);
		}
		return items;
	};

	return (
		<ShadPagination className="w-full justify-center">
			<PaginationContent>
				<PaginationItem>
					<PaginationLink
						href="#"
						size="default"
						onClick={(e) => {
							e.preventDefault();
							if (hasPrev) goTo(page - 1);
						}}
						aria-label="이전 페이지"
						className={
							!hasPrev
								? 'pointer-events-none opacity-40 gap-1 px-2.5 sm:pl-2.5'
								: 'gap-1 px-2.5 sm:pl-2.5'
						}
					>
						<ChevronLeft className="h-4 w-4" />
						<span className="hidden sm:block">이전</span>
					</PaginationLink>
				</PaginationItem>

				{renderPages().map((item, idx) =>
					item === 'ellipsis' ? (
						<PaginationItem key={`ellipsis-${idx}`}>
							<PaginationEllipsis />
						</PaginationItem>
					) : (
						<PaginationItem key={item}>
							<PaginationLink
								href="#"
								isActive={item === page}
								onClick={(e) => {
									e.preventDefault();
									goTo(item);
								}}
								className={
									item === page
										? 'min-w-9 rounded-full border border-blue-600 bg-blue-50 text-blue-700'
										: 'min-w-9'
								}
							>
								{item}
							</PaginationLink>
						</PaginationItem>
					)
				)}

				<PaginationItem>
					<PaginationLink
						href="#"
						size="default"
						onClick={(e) => {
							e.preventDefault();
							if (hasNext) goTo(page + 1);
						}}
						aria-label="다음 페이지"
						className={
							!hasNext
								? 'pointer-events-none opacity-40 gap-1 px-2.5 sm:pr-2.5'
								: 'gap-1 px-2.5 sm:pr-2.5'
						}
					>
						<span className="hidden sm:block">다음</span>
						<ChevronRight className="h-4 w-4" />
					</PaginationLink>
				</PaginationItem>
			</PaginationContent>
		</ShadPagination>
	);
}
