import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar, Sparkles } from 'lucide-react';

import { formatDateTimeLocal, getDateRangeDurationLabel } from '@/lib/date';
import { getParticipationMessage } from '@/lib/utils';
import { PollParticipationBadge } from '../../PollCard/PollParticipationBadge';
import type { PollCardProps } from '../types';
import { useCallback } from 'react';
import { googleAnalyticsCustomEvent } from '@/lib/gtag';

export default function PollCarouselEventCard({
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
			action: 'home_page_poll_carousel_event_card_click',
			category: 'home_page_poll_carousel_event_card',
			label: pollData?.title ?? '',
			value: pollData?.id,
		});
	}, [onClickPoll, pollData?.id, pollData?.title]);

	return (
		<Card className="group relative h-full overflow-hidden border-2 border-purple-200/60 bg-gradient-to-br from-purple-50 via-pink-50/50 to-orange-50/40 gap-1 shadow-lg transition-all hover:shadow-xl hover:border-purple-300/80">
			{/* 장식용 그라데이션 오버레이 */}
			<div className="absolute inset-0 bg-gradient-to-br from-purple-100/20 via-transparent to-orange-100/20 opacity-50" />

			{/* 장식용 반짝이 아이콘 (배경) */}
			<div className="absolute inset-0 opacity-5">
				<div className="absolute top-4 left-4">
					<Sparkles className="h-6 w-6 text-purple-400" />
				</div>
				<div className="absolute top-8 right-8">
					<Sparkles className="h-4 w-4 text-pink-400" />
				</div>
				<div className="absolute bottom-12 left-8">
					<Sparkles className="h-5 w-5 text-orange-400" />
				</div>
			</div>

			<CardHeader className="relative pt-3 z-10">
				<div className="mb-2 flex items-center justify-between gap-2">
					<PollParticipationBadge
						participationMessage={participationMessage}
						remainingTimeLabel={remainingTimeLabel}
						color="fuchsia"
					/>
				</div>
				<CardTitle className="line-clamp-2 text-base font-bold tracking-tight text-slate-900 md:text-lg">
					{pollData.title}
				</CardTitle>
				<p className="min-h-[2.5rem] line-clamp-2 whitespace-pre-line text-xs leading-relaxed text-slate-700 md:text-sm">
					{pollData.description || '\u00A0'}
				</p>
			</CardHeader>

			<CardContent className="relative flex h-full flex-col px-5 pb-3 pt-0 z-10">
				<div className="mb-3 flex items-center gap-2 text-[11px] text-slate-600 md:text-xs mt-auto">
					<span className="inline-flex items-center gap-1 rounded-full bg-white/80 px-2 py-1 border border-purple-200">
						<Calendar className="h-3 w-3 text-purple-600" />
						<span className="text-xs font-medium text-slate-700">
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
				</div>

				<div className="">
					<Button
						type="button"
						size="sm"
						variant="default"
						className="group/btn w-full inline-flex items-center justify-center gap-2 rounded-full bg-gradient-to-r from-purple-600 via-pink-500 to-orange-500 px-5 py-3 text-[13px] font-bold text-white shadow-lg transition-all hover:from-purple-700 hover:via-pink-600 hover:to-orange-600 hover:scale-105 md:text-sm cursor-pointer"
						onClick={(event) => {
							event.stopPropagation();
							handleClickPoll;
						}}
					>
						<span className="inline-flex items-center gap-1.5">
							<Sparkles className="h-4 w-4 fill-yellow-200 text-yellow-200" />
							<span>이벤트 투표 참여하기</span>
						</span>
						<span className="translate-x-0 text-base opacity-90 transition-transform group-hover/btn:translate-x-1">
							→
						</span>
					</Button>
				</div>
			</CardContent>
		</Card>
	);
}
