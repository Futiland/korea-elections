import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar, Snowflake, Star } from 'lucide-react';

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

	const remainingTimeLabel = pollData?.endAt
		? getDateRangeDurationLabel(pollData.endAt)
		: null;

	return (
		<Card className="group relative h-full overflow-hidden border-2 bg-gradient-to-br from-red-50 via-green-50/30 to-amber-50/40 gap-1 shadow-lg">
			{/* 크리스마스 배지 */}
			{/* <div className="absolute right-3 top-3 z-10">
				<span className="inline-flex items-center gap-1 rounded-full bg-gradient-to-r from-red-600 to-green-600 px-3 py-1 text-[11px] font-bold text-white shadow-lg">
					<Star className="h-3 w-3 fill-yellow-300 text-yellow-300" />
					<span>크리스마스</span>
				</span>
			</div> */}

			{/* 장식용 크리스마스 그라데이션 오버레이 */}
			<div className="absolute inset-0 bg-gradient-to-br from-red-100/30 via-transparent to-green-100/20 opacity-60" />

			{/* 눈송이 장식 (배경) */}
			<div className="absolute inset-0 opacity-10">
				<div className="absolute top-4 left-4">
					<Snowflake className="h-6 w-6 text-blue-300" />
				</div>
				<div className="absolute top-8 right-8">
					<Snowflake className="h-4 w-4 text-blue-200" />
				</div>
				<div className="absolute bottom-12 left-8">
					<Snowflake className="h-5 w-5 text-blue-200" />
				</div>
			</div>

			<CardHeader className="relative pt-3 z-10">
				<div className="mb-2 flex items-center justify-between gap-2">
					<PollParticipationBadge
						participationMessage={participationMessage}
						remainingTimeLabel={remainingTimeLabel}
						color="red"
					/>
					{/* <span className="inline-flex items-center gap-1 rounded-full bg-white/90 px-2.5 py-1 text-[11px] font-semibold text-red-600 shadow-sm border border-red-200">
						<Star className="h-3 w-3 fill-amber-400 text-amber-400" />
						<span>{pollData.responseCount?.toLocaleString() ?? 0}명 참여</span>
					</span> */}
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
					<span className="inline-flex items-center gap-1 rounded-full bg-white/80 px-2 py-1 border border-green-200">
						<Calendar className="h-3 w-3 text-green-600" />
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
						className="group/btn w-full inline-flex items-center justify-center gap-2 rounded-full bg-gradient-to-r from-red-600 via-red-500 to-green-600 px-5 py-3 text-[13px] font-bold text-white shadow-lg transition-all hover:from-red-700 hover:via-red-600 hover:to-green-700 hover:scale-105 md:text-sm cursor-pointer "
						onClick={(event) => {
							event.stopPropagation();
							onClickPoll?.(pollData?.id.toString());
						}}
					>
						<span className="inline-flex items-center gap-1.5">
							<Star className="h-4 w-4 fill-yellow-300 text-yellow-300" />
							<span>크리스마스 투표 참여하기</span>
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
