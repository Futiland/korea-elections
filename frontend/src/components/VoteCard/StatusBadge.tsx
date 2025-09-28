interface StatusBadgeProps {
	status: 'progress' | 'completed' | 'ended';
	className?: string;
}

export default function StatusBadge({
	status,
	className = '',
}: StatusBadgeProps) {
	const getStatusConfig = () => {
		switch (status) {
			case 'progress':
				return {
					text: '진행중',
					className: 'bg-green-100 text-green-800',
				};
			case 'completed':
				return {
					text: '투표 완료',
					className: 'bg-blue-100 text-blue-800',
				};
			case 'ended':
				return {
					text: '종료',
					className: 'bg-gray-100 text-gray-800',
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
