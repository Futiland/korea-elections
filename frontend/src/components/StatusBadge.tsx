interface StatusBadgeProps {
	status: 'progress' | 'stopped' | 'ended';
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
			case 'ended':
				return {
					text: '종료',
					className: 'bg-gray-100 text-gray-800',
				};
			case 'stopped':
				return {
					text: '투표중단',
					className: 'bg-red-100 text-red-800',
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
