import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar, Users } from 'lucide-react';

import { formatDateTimeLocal, getDateRangeDurationLabel } from '@/lib/date';
import { getParticipationMessage } from '@/lib/utils';
import { PollParticipationBadge } from '../../PollCard/PollParticipationBadge';
import type { PollCardProps } from '../types';
import { useCallback } from 'react';
import { googleAnalyticsCustomEvent } from '@/lib/gtag';

export default function PollCarouselCard({
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
			action: 'home_page_poll_carousel_card_click',
			category: 'home_page_poll_carousel_card',
			label: pollData?.title ?? '',
			value: pollData?.id,
		});
	}, [onClickPoll, pollData?.id, pollData?.title]);

	return (
		<Card
			className="py-4 gap-2 group h-full border-blue-100/80 bg-gradient-to-br from-blue-50/80 via-sky-50/60 to-white transition-all hover:-translate-y-1 hover:border-blue-300 hover:bg-blue-50/80 hover:shadow-md"
			// onClick={() => onClickPoll?.(poll.id)}
		>
			<CardHeader className="pt-2">
				<PollParticipationBadge
					participationMessage={participationMessage}
					remainingTimeLabel={remainingTimeLabel}
					color="blue"
				/>
				<CardTitle className="line-clamp-2 text-base font-semibold tracking-tight text-slate-900 md:text-lg">
					{pollData.title}
				</CardTitle>
			</CardHeader>

			<CardContent className="flex h-full flex-col px-5 pb-2 pt-0">
				<p className="mb-2 min-h-[2.5rem] line-clamp-2 whitespace-pre-line text-xs leading-relaxed text-slate-700 md:text-sm">
					{pollData.description || '\u00A0'}
				</p>

				{/* <div className="flex items-center justify-between gap-3 text-[11px] text-slate-600 md:text-xs">
					<span className="inline-flex items-center gap-1 truncate">
						<Calendar className="h-3 w-3 text-blue-400" />
						<span className="text-xs text-slate-600">
							{pollData?.startAt && pollData?.endAt
								? `${formatDateTimeLocal(
										pollData.startAt,
										'yyyy-MM-dd HH:mm'
								  )} ~ ${formatDateTimeLocal(
										pollData.endAt,
										'yyyy-MM-dd HH:mm'
								  )}`
								: ''}
						</span>
					</span>
				</div> */}
				<div className="text-right mt-auto">
					<Button
						type="button"
						size="sm"
						variant="default"
						className="cursor-pointer inline-flex items-center justify-between rounded-full bg-blue-700 px-4 py-3 text-[13px] font-semibold text-white shadow-sm hover:bg-blue-700 hover:shadow-md md:text-sm"
						onClick={(event) => {
							event.stopPropagation();
							handleClickPoll();
						}}
					>
						<span className="inline-flex items-center gap-1">
							<span>지금 투표하러 가기</span>
						</span>
						<span className="translate-x-0 text-[12px] opacity-90 transition-transform group-hover:translate-x-0.5">
							→
						</span>
					</Button>
				</div>
			</CardContent>
		</Card>
	);
}
