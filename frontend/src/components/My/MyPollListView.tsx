import { ReactNode, useEffect, useRef } from 'react';
import { Spinner } from '@/components/ui/spinner';
import { MyPollData } from '@/lib/types/poll';
import Pagination from '../Pagination';
import MyPollListItem from './MyPollListItem';
import { Empty } from '@/components/EmptyCase';
import { VoteIcon } from 'lucide-react';

interface MyPollListViewProps {
	title: string;
	description?: ReactNode;
	items: MyPollData[];
	page: number;
	totalPages: number;
	isLoading: boolean;
	isError: boolean;
	onPageChange: (page: number) => void;
	emptyMessage: string;
	action?: ReactNode;
	buttonUrl?: string;
	buttonText?: string;
	emptyDescription?: string;
}

export default function MyPollListView({
	title,
	description,
	items,
	page,
	totalPages,
	isLoading,
	isError,
	onPageChange,
	emptyMessage,
	action,
	buttonUrl,
	buttonText,
	emptyDescription,
}: MyPollListViewProps) {
	const containerRef = useRef<HTMLDivElement>(null);

	// 페이지네이션이 변경되거나 items 데이터가 변경되면 스크롤을 맨 위로 이동
	useEffect(() => {
		if (typeof window !== 'undefined') {
			window.scrollTo({ top: 0, behavior: 'smooth' });
		}
	}, [page, items]);

	return (
		<div ref={containerRef} className="min-h-screen px-4 py-6">
			<div className="w-full max-w-lg mx-auto space-y-4">
				<header className="space-y-1">
					<h1 className="text-xl font-bold">{title}</h1>
					{description ? (
						<p className="text-sm text-slate-600">{description}</p>
					) : null}
				</header>

				{isLoading ? (
					<div className="flex items-center justify-center py-10 min-h-screen ">
						<Spinner className="w-10 h-10 text-blue-500" />
					</div>
				) : isError ? (
					<div className="rounded-lg border border-slate-200 bg-white p-4 text-sm text-red-600">
						목록을 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.
					</div>
				) : items.length === 0 ? (
					<Empty
						title={emptyMessage}
						buttonUrl={buttonUrl}
						buttonText={buttonText}
						description={emptyDescription}
						icon={
							<div className="flex items-center justify-center rounded-full bg-blue-50 text-blue-500 p-2">
								<VoteIcon
									className="w-10 h-10 text-slate-600"
									color="#193cb8"
								/>
							</div>
						}
					/>
				) : (
					<div className="mb-10">
						{items.map((item) => (
							<MyPollListItem key={item.id} item={item} />
						))}
					</div>
				)}

				{/* Pagination */}
				{totalPages > 0 && !isLoading && (
					<Pagination
						page={page}
						totalPages={totalPages}
						isLoading={isLoading}
						onPageChange={onPageChange}
					/>
				)}
			</div>
		</div>
	);
}
