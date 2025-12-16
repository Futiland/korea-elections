import { Users } from 'lucide-react';

interface PollParticipationBadgeProps {
	participationMessage: string;
	remainingTimeLabel?: string | null;
	color?: 'fuchsia' | 'blue' | 'sky';
}

export function PollParticipationBadge({
	participationMessage,
	remainingTimeLabel,
	color = 'fuchsia',
}: PollParticipationBadgeProps) {
	const baseClasses =
		'inline-flex items-center gap-2 rounded-full px-3 py-1 text-xs sm:text-sm font-medium';

	const variants = {
		fuchsia: {
			badge: 'bg-fuchsia-50 text-fuchsia-600',
			icon: 'text-fuchsia-600',
		},
		blue: {
			badge: 'bg-blue-50 text-blue-700',
			icon: 'text-blue-500',
		},
		sky: {
			badge: 'bg-sky-50 text-sky-700',
			icon: 'text-sky-500',
		},
	} as const;

	const { badge: colorClasses, icon: iconColor } =
		variants[color] ?? variants.fuchsia;

	return (
		<div className={`${baseClasses} ${colorClasses}`}>
			<Users className={`h-4 w-4 ${iconColor}`} />
			<span>
				{participationMessage}
				{remainingTimeLabel ? ` · ${remainingTimeLabel}` : ''}
			</span>
		</div>
	);
}
