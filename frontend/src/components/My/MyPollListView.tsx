import { ReactNode } from 'react';
import { Spinner } from '@/components/ui/spinner';
import { MyPollData } from '@/lib/types/poll';
import Pagination from '../Pagination';
import MyPollListItem from './MyPollListItem';

interface MyPollListViewProps {
	title: string;
	description?: ReactNode;
	items: MyPollData[];
	page: number;
	totalPages: number;
	isLoading: boolean;
	isError: boolean;
	onPageChange: (page: number) => void;
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
}: MyPollListViewProps) {
	return (
		<div className="min-h-screen px-4 py-6">
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
					<div className="rounded-lg border border-slate-200 bg-white p-8 text-center text-sm text-slate-500">
						표시할 투표가 없습니다.
					</div>
				) : (
					<div className="mb-10">
						{items.map((item) => (
							<MyPollListItem key={item.id} item={item} />
						))}
					</div>
				)}

				{/* Pagination */}
				{totalPages > 0 && (
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
