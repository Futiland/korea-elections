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
					className: 'bg-green-100 text-green-800',
				};
			case 'EXPIRED':
				return {
					text: '종료',
					className: 'bg-gray-100 text-gray-800',
				};
			case 'DELETED':
				return {
					text: '삭제된투표',
					className: 'bg-gray-100 text-gray-800',
				};
			case 'CANCELLED':
				return {
					text: '투표중단',
					className: 'bg-red-100 text-red-800',
				};
			case 'DRAFT':
				return {
					text: '투표예정',
					className: 'bg-blue-100 text-blue-800',
				};
			default:
				return {
					text: '진행중',
					className: 'bg-green-100 text-green-800',
				};
		}
	};

	const config = getStatusConfig();

	return (
		<span
			className={`inline-block text-xs font-semibold px-2 py-1 rounded ${config.className} ${className}`}
		>
			{config.text}
		</span>
	);
}
