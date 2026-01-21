import { useRouter } from 'next/router';
import { Calendar, ChevronRight } from 'lucide-react';

import StatusBadge from '@/components/StatusBadge';
import { Button } from '@/components/ui/button';
import { formatDateTimeLocal } from '@/lib/date';
import { MyPollData } from '@/lib/types/poll';
import { cn } from '@/lib/utils';

interface MyPollListItemProps {
	item: MyPollData;
	className?: string;
}

export default function MyPollListItem({
	item,
	className,
}: MyPollListItemProps) {
	const router = useRouter();

	const handleDetailClick = (e: React.MouseEvent) => {
		e.stopPropagation();
		if (item.id) {
			router.push(`/everyone-polls/${item.id}`);
		}
	};

	return (
		<div
			className={cn(
				'flex items-center justify-between gap-3 border-b border-slate-200 px-0 py-4',
				className
			)}
		>
			<div className="flex-1 space-y-1">
				<div className="flex items-start gap-2">
					{/* 상태 배지 */}
					<StatusBadge status={item.status} className="shrink-0" />

					<div className="flex-1 space-y-1">
						{/* 제목 + (모바일 전용 재투표 가능 배지) - 한 줄에 붙어서 표시 */}
						<div className="flex flex-wrap items-center gap-2 min-w-0">
							<p className="text-sm font-semibold text-slate-900 break-words">
								{item.title}
								{item.isRevotable && (
									<span className="inline-flex sm:hidden shrink-0 rounded-full bg-yellow-50 px-2.5 py-1 text-[11px] font-semibold text-yellow-700">
										재투표 가능
									</span>
								)}
							</p>
						</div>
					</div>
				</div>
				<p className="line-clamp-2 whitespace-pre-line text-xs leading-relaxed text-slate-600">
					{item.description || ''}
				</p>
				<div className="flex items-center gap-1 text-[11px] text-slate-500">
					<span className="rounded-full bg-blue-50 px-2.5 py-1 text-[11px] font-semibold text-blue-700">
						참여 {item.responseCount}명
					</span>
					{/* 데스크톱: 참여자 옆 표시 */}
					{item.isRevotable && (
						<span className="hidden sm:inline-flex rounded-full bg-yellow-50 px-2.5 py-1 text-[11px] font-semibold text-yellow-700">
							재투표 가능
						</span>
					)}
					<span className="inline-flex items-center gap-1">
						<Calendar className="h-3 w-3 text-slate-400" />
						<span>
							{formatDateTimeLocal(item.startAt, 'yyyy-MM-dd HH:mm')} ~{' '}
							{formatDateTimeLocal(item.endAt, 'yyyy-MM-dd HH:mm')}
						</span>
					</span>
				</div>
			</div>
			<Button
				variant="ghost"
				size="icon"
				onClick={handleDetailClick}
				className="h-10 w-10 shrink-0 text-slate-600 hover:text-slate-900 cursor-pointer"
			>
				<ChevronRight className="h-10 w-10" />
			</Button>
		</div>
	);
}
