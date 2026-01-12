import { Calendar, Clock } from 'lucide-react';
import { Button } from '@/components/ui/button';

import { formatDateTimeLocal, getDateRangeDurationLabel } from '@/lib/date';
import { getParticipationMessage } from '@/lib/utils';
import { PollParticipationBadge } from '../../PollCard/PollParticipationBadge';
import type { PollCardProps } from '../types';
import { useCallback } from 'react';
import { googleAnalyticsCustomEvent } from '@/lib/gtag';

export default function PollListTypeCard({
	pollData,
	onClickPoll,
}: PollCardProps) {
	const participationMessage = getParticipationMessage(
		pollData?.status,
		pollData?.responseCount
	);

	const remainingTimeLabel = pollData?.endAt
		? getDateRangeDurationLabel(pollData.endAt)
		: null;

	const handleClickPoll = useCallback(() => {
		onClickPoll?.(pollData?.id.toString());
		googleAnalyticsCustomEvent({
			action: 'home_page_poll_list_type_card_click',
			category: 'home_page_poll_list_type_card',
			label: pollData?.title ?? '',
			value: pollData?.id,
		});
	}, [onClickPoll, pollData?.id, pollData?.title]);

	return (
		<article
			className="group flex h-full flex-col justify-between border-b border-slate-200 px-1 py-3 transition-colors hover:bg-sky-50/60"
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
				<p className="min-h-[2.5rem] line-clamp-2 whitespace-pre-line text-xs leading-relaxed text-slate-700 md:text-sm">
					{pollData.description || '\u00A0'}
				</p>
			</section>

			<footer className="mt-2 flex items-center justify-between text-[11px] text-slate-500 md:text-xs">
				<div className="inline-flex items-center">
					<span className="rounded-full bg-slate-200 px-2 py-1 font-medium text-slate-500">
						모두의 투표
					</span>
					{remainingTimeLabel && (
						<span className="text-sky-700 inline-flex items-center gap-1 ml-1 px-1 py-1 text-[11px] font-medium">
							<Clock className="h-3.5 w-3.5 text-sky-700" />
							{remainingTimeLabel}
						</span>
					)}
				</div>

				<Button
					type="button"
					size="sm"
					variant="default"
					className="cursor-pointer inline-flex items-center gap-1 rounded-full border bg-blue-700 px-3 py-1 text-[11px] font-semibold text-white shadow-sm hover:bg-blue-700 hover:shadow-md md:text-sm "
					onClick={(event) => {
						event.stopPropagation();
						handleClickPoll();
					}}
				>
					<span>투표하기</span>
					<span className="translate-x-0 text-[11px] opacity-80 transition-transform group-hover:translate-x-0.5">
						→
					</span>
				</Button>
			</footer>
		</article>
	);
}
