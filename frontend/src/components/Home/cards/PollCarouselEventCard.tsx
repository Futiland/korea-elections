import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar, Users, Sparkles } from 'lucide-react';

import { formatDateTimeLocal, getDateRangeDurationLabel } from '@/lib/date';
import { getParticipationMessage } from '@/lib/utils';
import { PollParticipationBadge } from '../../PollCard/PollParticipationBadge';
import type { PollCardProps } from '../types';

export default function PollCarouselEventCard({
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
		<Card className="group relative h-full cursor-pointer overflow-hidden border-2 ">
			{/* 이벤트 배지 */}
			{/* <div className="absolute right-3 top-3 z-10">
				<span className="inline-flex items-center gap-1 rounded-full bg-gradient-to-r from-red-500 to-rose-500 px-3 py-1 text-[11px] font-bold text-white shadow-lg">
					<Sparkles className="h-3 w-3" />
					<span>이벤트</span>
				</span>
			</div> */}

			{/* 장식용 그라데이션 오버레이 */}
			<div className="absolute inset-0 bg-gradient-to-br from-transparent via-transparent to-red-100/20 opacity-50" />

			<CardHeader className="relative pt-3">
				<div className="mb-2 flex items-center justify-between gap-2">
					<PollParticipationBadge
						participationMessage={participationMessage}
						remainingTimeLabel={remainingTimeLabel}
						color="red"
					/>
					{/* <span className="inline-flex items-center gap-1 rounded-full bg-white/80 px-2.5 py-1 text-[11px] font-semibold text-red-600 shadow-sm">
						<Users className="h-3.5 w-3.5" />
						<span>{pollData.responseCount?.toLocaleString() ?? 0}명</span>
					</span> */}
				</div>
				<CardTitle className="line-clamp-2 text-base font-bold tracking-tight text-slate-900 md:text-lg">
					{pollData.title}
				</CardTitle>
				<p className="line-clamp-3 text-xs leading-relaxed text-slate-700 md:text-sm">
					{pollData.description}
				</p>
			</CardHeader>

			<CardContent className="relative flex h-full flex-col px-5 pb-3 pt-0">
				<div className="mb-3 flex items-center gap-2 text-[11px] text-slate-600 md:text-xs">
					<span className="inline-flex items-center gap-1 rounded-full bg-white/60 px-2 py-1">
						<Calendar className="h-3 w-3 text-red-500" />
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

				<div className="mt-auto">
					<Button
						type="button"
						size="sm"
						variant="default"
						className="group/btn w-full inline-flex items-center justify-center gap-2 rounded-full bg-gradient-to-r from-red-500 to-rose-500 px-5 py-3 text-[13px] font-bold text-white shadow-lg transition-all hover:from-red-600 hover:to-rose-600 hover:shadow-xl hover:shadow-red-300/50 hover:scale-105 md:text-sm"
						onClick={(event) => {
							event.stopPropagation();
							onClickPoll?.(pollData?.id.toString());
						}}
					>
						<span className="inline-flex items-center gap-1.5">
							<Sparkles className="h-4 w-4" />
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
