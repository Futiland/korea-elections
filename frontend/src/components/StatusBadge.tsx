import { PollStatus } from '@/lib/types/poll';

interface StatusBadgeProps {
	status: PollStatus;
	className?: string;
}

export default function StatusBadge({
	status,
	className = '',
}: StatusBadgeProps) {
	const getStatusConfig = () => {
		switch (status) {
			case 'IN_PROGRESS':
				return {
					text: '진행중',
					className: 'bg-green-50 text-green-700 border border-green-200',
				};
			case 'EXPIRED':
				return {
					text: '종료',
					className: 'bg-slate-100 text-slate-600 border border-slate-200',
				};
			case 'DELETED':
				return {
					text: '삭제된투표',
					className: 'bg-slate-100 text-slate-600 border border-slate-200',
				};
			case 'CANCELLED':
				return {
					text: '투표중단',
					className: 'bg-red-50 text-red-600 border border-red-200',
				};
			case 'DRAFT':
				return {
					text: '투표예정',
					className: 'bg-sky-50 text-sky-700 border border-sky-200',
				};
			default:
				return {
					text: '진행중',
					className: 'bg-green-50 text-green-700 border border-green-200',
				};
		}
	};

	const config = getStatusConfig();

	return (
		<span
			className={`inline-flex items-center gap-2 rounded-full px-3 py-1 text-sm font-medium ${config.className} ${className}`}
		>
			{config.text}
		</span>
	);
}
