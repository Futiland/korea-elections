import { useRouter } from 'next/router';
import { Calendar } from 'lucide-react';

import StatusBadge from '@/components/StatusBadge';
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

	return (
		<div
			onClick={() => item.id && router.push(`/everyone-poll/${item.id}`)}
			className={cn(
				'block w-full cursor-pointer border-b border-slate-200 px-0 py-3 text-left transition hover:bg-slate-50',
				className
			)}
		>
			<div className="flex items-start justify-between gap-3 px-1">
				<div className="flex-1 space-y-1">
					<div className="flex items-center gap-2">
						<p className="text-sm font-semibold text-slate-900">{item.title}</p>
						<StatusBadge status={item.status} />
					</div>
					<p className="line-clamp-2 text-xs leading-relaxed text-slate-600">
						{item.description || ''}
					</p>
					<div className="flex items-center gap-3 text-[11px] text-slate-500">
						<span className="rounded-full bg-blue-50 px-2.5 py-1 text-[11px] font-semibold text-blue-700">
							참여 {item.responseCount}명
						</span>
						<span className="inline-flex items-center gap-1">
							<Calendar className="h-3 w-3 text-slate-400" />
							<span>
								{formatDateTimeLocal(item.startAt, 'yyyy-MM-dd HH:mm')} ~{' '}
								{formatDateTimeLocal(item.endAt, 'yyyy-MM-dd HH:mm')}
							</span>
						</span>
					</div>
				</div>
			</div>
		</div>
	);
}
