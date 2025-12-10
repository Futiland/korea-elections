import { Button } from '@/components/ui/button';
import StatusBadge from '@/components/StatusBadge';
import { MyPollData, PollStatus } from '@/lib/types/poll';
import router from 'next/router';
import { formatDateTimeLocal } from '@/lib/date';

interface PollListItemProps {
	item: MyPollData;
}

export default function PollListItem({ item }: PollListItemProps) {
	return (
		<div
			className="border border-slate-200 rounded-lg p-4 mb-3 bg-white hover:bg-blue-50 transition-colors cursor-pointer"
			onClick={
				item.pollDescription
					? () => router.push(`/everyones-poll/${item.pollId}`)
					: undefined
			}
		>
			<div className="flex items-center justify-between mb-2">
				<div className="flex items-center gap-2">
					<StatusBadge status={item.pollStatus} />
					<span className="text-slate-600 text-xs">
						{/* 참여자 {item.responseCount}명 */}
						{/* TODO: 참여자 수 표시 */}
						참여자 100명
					</span>
					<span className="text-slate-600 text-xs">•</span>
					<span className="text-slate-600 text-xs">
						{formatDateTimeLocal(item.startAt, 'yyyy-MM-dd HH:mm')} ~
						{formatDateTimeLocal(item.endAt, 'yyyy-MM-dd HH:mm')}
					</span>
				</div>
			</div>
			<div className="flex items-center justify-between mb-1">
				<h3 className="font-semibold text-gray-900">{item.pollTitle}</h3>
			</div>
			<p className="text-sm text-gray-600 line-clamp-2">
				{item.pollDescription}
			</p>
		</div>
	);
}
