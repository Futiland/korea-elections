import { Button } from '@/components/ui/button';
import StatusBadge from '@/components/StatusBadge';

interface VoteListItemProps {
	title: string;
	description: string;
	status: 'progress' | 'stopped' | 'ended';
	participantCount: number;
	endDate: string;
	onViewDetails: () => void;
}

export default function VoteListItem({
	title,
	description,
	status,
	participantCount,
	endDate,
	onViewDetails,
}: VoteListItemProps) {
	return (
		<div
			className="border border-slate-200 rounded-lg p-4 mb-3 bg-white hover:bg-slate-50 transition-colors cursor-pointer"
			onClick={onViewDetails}
		>
			<div className="flex items-center justify-between mb-2">
				<div className="flex items-center gap-2">
					<StatusBadge status={status} />
					<span className="text-slate-600 text-xs">
						참여자 {participantCount}명
					</span>
					<span className="text-slate-600 text-xs">•</span>
					<span className="text-slate-600 text-xs">종료일 {endDate}</span>
				</div>
			</div>
			<div className="flex items-center justify-between mb-1">
				<h3 className="font-semibold text-gray-900">{title}</h3>
			</div>
			<p className="text-sm text-gray-600 line-clamp-2">{description}</p>
		</div>
	);
}
