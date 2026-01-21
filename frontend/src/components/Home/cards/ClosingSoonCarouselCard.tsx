import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar, Clock, Users } from 'lucide-react';

import { formatDateTimeLocal, getDateRangeDurationLabel } from '@/lib/date';
import { getParticipationMessage } from '@/lib/utils';
import { PollParticipationBadge } from '../../PollCard/PollParticipationBadge';
import type { PollCardProps } from '../types';
import { useCallback } from 'react';
import { googleAnalyticsCustomEvent } from '@/lib/gtag';

export default function ClosingSoonCarouselCard({
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

	const isClosingSoon =
		!!remainingTimeLabel && pollData?.status === 'IN_PROGRESS';

	const handleClickPoll = useCallback(() => {
		onClickPoll?.(pollData?.id.toString());
		googleAnalyticsCustomEvent({
			action: 'home_page_closing_soon_carousel_card_click',
			category: 'home_page_closing_soon_carousel_card',
			label: pollData?.title ?? '',
			value: pollData?.id,
		});
	}, [onClickPoll, pollData?.id, pollData?.title]);

	return (
		<Card className="group h-full border-amber-100/80 bg-gradient-to-br from-amber-50/80 via-orange-30/60 to-white py-3 transition-all hover:-translate-y-1 hover:border-amber-300 hover:bg-amber-50/90 hover:shadow-md gap-2">
			<CardHeader className="px-5 pt-2">
				<div className="mb-2 flex items-center justify-between gap-2">
					{isClosingSoon && (
						<span className="inline-flex items-center gap-1 rounded-full bg-red-50 px-2.5 py-1 text-[11px] font-semibold text-red-600">
							<Clock className="h-3.5 w-3.5" />
							<span>{remainingTimeLabel}</span>
						</span>
					)}
					<span className="inline-flex items-center gap-1 text-[11px] text-slate-500">
						<Users className="h-3.5 w-3.5 text-slate-400" />
						<span className="font-medium text-slate-700">
							{pollData.responseCount?.toLocaleString() ?? 0}명 참여
						</span>
					</span>
				</div>

				<CardTitle className="line-clamp-2 text-base font-semibold tracking-tight text-slate-900 md:text-lg">
					{pollData.title}
				</CardTitle>
			</CardHeader>

			<CardContent className="flex h-full flex-col px-5 pb-3 pt-1">
				<p className="mb-3 min-h-[2.5rem] line-clamp-2 whitespace-pre-line text-xs leading-relaxed text-slate-700 md:text-sm">
					{pollData.description || '\u00A0'}
				</p>

				<div className="mt-auto">
					<Button
						type="button"
						size="sm"
						variant="default"
						className="cursor-pointer inline-flex items-center justify-between rounded-full border border-red-200 bg-red-50 px-4 py-2.5 text-[13px] font-semibold text-red-700  hover:border-red-300 hover:bg-red-100 hover:text-red-800"
						onClick={(event) => {
							event.stopPropagation();
							handleClickPoll();
						}}
					>
						<span className="inline-flex items-center gap-1">
							<span>마감 임박! 지금 투표하기</span>
						</span>
						<span className="translate-x-0 text-[12px] opacity-80 transition-transform group-hover:translate-x-0.5">
							→
						</span>
					</Button>
				</div>
			</CardContent>
		</Card>
	);
}
