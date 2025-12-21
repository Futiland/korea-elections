import { Calendar } from 'lucide-react';
import { Button } from '@/components/ui/button';
import StatusBadge from '@/components/StatusBadge';
import { MyPollData } from '@/lib/types/poll';
import router from 'next/router';
import { formatDateTimeLocal } from '@/lib/date';
import { Card } from '@/components/ui/card';

interface PollSummaryItemProps {
	item: MyPollData;
}

export default function PollSummaryItem({ item }: PollSummaryItemProps) {
	return (
		<Card
			className="group flex h-full cursor-pointer flex-col gap-2 border-blue-100/80  p-4 transition-all hover:-translate-y-1 hover:border-blue-300 hover:bg-blue-50 hover:shadow-md"
			onClick={
				item.id ? () => router.push(`/everyone-polls/${item.id}`) : undefined
			}
		>
			<header className="flex items-center justify-between gap-1">
				<div className="flex flex-col items-end text-right text-[11px] text-slate-500">
					<span className="inline-flex items-center gap-1">
						<Calendar className="h-3 w-3 text-slate-400" />
						<span>
							{`${formatDateTimeLocal(
								item.startAt,
								'yyyy-MM-dd HH:mm'
							)} ~ ${formatDateTimeLocal(item.endAt, 'yyyy-MM-dd HH:mm')}`}
						</span>
					</span>
				</div>
				<StatusBadge status={item.status} />
			</header>

			<section className="">
				<p className="line-clamp-2 text-sm font-semibold tracking-tight text-slate-900 md:text-base">
					{item.title}
				</p>
				<p className="line-clamp-2 whitespace-pre-line text-xs leading-relaxed text-slate-700 md:text-sm">
					{item.description || ''}
				</p>
			</section>

			<div className="flex items-center justify-between text-[11px] text-slate-500 md:text-xs">
				<span className="rounded-full bg-blue-100/60 px-2 py-1 font-medium text-blue-700">
					{/* TODO: 참여자 수 표시 */}
					참여자 {item.responseCount}명
				</span>
			</div>
		</Card>
	);
}
