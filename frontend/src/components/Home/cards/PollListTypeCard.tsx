import { Calendar } from 'lucide-react';
import { Button } from '@/components/ui/button';

import { formatDateTimeLocal, getDateRangeDurationLabel } from '@/lib/date';
import { getParticipationMessage } from '@/lib/utils';
import { PollParticipationBadge } from '../../PollCard/PollParticipationBadge';
import type { PollCardProps } from '../types';

export default function PollListTypeCard({
	pollData,
	onClickPoll,
}: PollCardProps) {
	const participationMessage = getParticipationMessage(
		pollData?.status,
		pollData?.responseCount
	);

	const remainingTimeLabel =
		pollData?.startAt && pollData?.endAt
			? getDateRangeDurationLabel(pollData.startAt, pollData.endAt)
			: null;

	return (
		<article
			className="group flex h-full cursor-pointer flex-col justify-between border-b border-slate-200 px-1 py-3 transition-colors hover:bg-sky-50/60"
			onClick={() => onClickPoll?.(pollData?.id.toString())}
		>
			<header className="mb-1 flex items-center justify-between gap-3 pb-2">
				<div className="flex flex-col items-end text-right text-[11px] text-slate-500">
					<span className="inline-flex items-center gap-1">
						<Calendar className="h-3 w-3 text-slate-400" />
						<span>
							{pollData?.startAt && pollData?.endAt
								? `${formatDateTimeLocal(
										pollData.startAt,
										'MM.dd'
								  )} ~ ${formatDateTimeLocal(pollData.endAt, 'MM.dd')}`
								: '상시 투표'}
						</span>
					</span>
				</div>
				<PollParticipationBadge
					participationMessage={participationMessage}
					// remainingTimeLabel={remainingTimeLabel}
					color="sky"
				/>
			</header>

			<section className="space-y-1">
				<p className="line-clamp-2 text-sm font-semibold tracking-tight text-slate-900 md:text-base">
					{pollData.title}
				</p>
				<p className="line-clamp-2 text-xs leading-relaxed text-slate-700 md:text-sm">
					{pollData.description}
				</p>
			</section>

			<footer className="mt-2 flex items-center justify-between text-[11px] text-slate-500 md:text-xs">
				<span className="rounded-full bg-slate-100 px-2 py-1 font-medium text-slate-500">
					모두의 투표
				</span>

				<Button
					type="button"
					size="sm"
					variant="default"
					className="inline-flex items-center gap-1 rounded-full border border-blue-200 bg-blue-50 px-3 py-1 text-[11px] font-semibold text-sky-700 hover:border-sky-300 hover:bg-sky-100 hover:text-blue-800 "
					onClick={(event) => {
						event.stopPropagation();
						onClickPoll?.(pollData?.id.toString());
					}}
				>
					<span>상세 보기</span>
					<span className="translate-x-0 text-[11px] opacity-80 transition-transform group-hover:translate-x-0.5">
						→
					</span>
				</Button>
			</footer>
		</article>
	);
}
