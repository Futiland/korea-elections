import { useState } from 'react';
import { Card, CardHeader, CardTitle } from '../ui/card';
import StatusBadge from '../StatusBadge';
import PollCardOptions from './PollCardOptions';
import { Share2, Users } from 'lucide-react';
import { addCommas } from '@/lib/utils';
import PollCardResults from './PollCardResults';
import { PublicPollData, PollStatus } from '@/lib/types/poll';
import { formatDateTimeLocal, getRemainingTimeLabel } from '@/lib/date';

interface PollCardProps {
	pollData?: PublicPollData;
}

export default function PollCard({ pollData }: PollCardProps) {
	const [showResults, setShowResults] = useState<boolean>(false);

	const handleOptionChange = (value: string | string[] | number) => {
		// 선택된 값 처리 (필요시 API 호출 등)
		console.log('Selected value:', value);
	};

	const participationMessage =
		pollData?.responseCount && pollData.responseCount > 0
			? `지금까지 ${addCommas(pollData.responseCount)}명이 참여했어요!`
			: '첫 번째 참여자가 되어 주세요!';

	const remainingTimeLabel = pollData?.endAt
		? getRemainingTimeLabel(pollData.endAt)
		: null;

	return (
		<Card className="w-full transition-colors">
			<div className="px-6 py-4">
				{/* 헤더 영역 - 참여자 수, 상태값, 제목, 공유 버튼 */}

				{/* 참여자 수 */}
				<div className="mb-3 inline-flex items-center gap-2 rounded-full bg-fuchsia-50 px-3 py-1 text-sm font-medium text-fuchsia-600">
					<Users className="w-4 h-4 text-fuchsia-600" />
					<span>
						{participationMessage}
						{remainingTimeLabel ? ` · ${remainingTimeLabel}` : ''}
					</span>
				</div>

				<div className="flex items-center justify-between mb-2">
					<div className="flex items-center gap-3">
						<StatusBadge status={pollData?.status ?? 'IN_PROGRESS'} />

						<span className="text-xs text-slate-500">
							{pollData?.createdAt && pollData?.endAt
								? `${formatDateTimeLocal(
										pollData.createdAt,
										'yyyy-MM-dd HH:mm'
								  )} ~ ${formatDateTimeLocal(
										pollData.endAt,
										'yyyy-MM-dd HH:mm'
								  )}`
								: ''}
						</span>
					</div>
					{pollData?.userName && (
						<p className="text-sm text-slate-500">{pollData.userName}</p>
					)}
				</div>

				{/* 투표 제목 */}
				<CardHeader className="px-0 py-0">
					<CardTitle className="text-lg">{pollData?.title}</CardTitle>
				</CardHeader>

				{/* 상세 내용 */}
				<div className="mb-6 text-sm text-slate-600 leading-relaxed">
					{pollData?.description}
				</div>

				{/* 선택 옵션 */}
				{!showResults && pollData && (
					<PollCardOptions
						responseType={pollData.responseType}
						options={pollData.options}
						onChange={handleOptionChange}
					/>
				)}

				{/* 결과 차트 */}
				{showResults && <PollCardResults />}

				{/* 버튼 영역 */}
				<div className="flex gap-3">
					{/* 진행중인 투표일 때만 투표하러가기 버튼 표시 */}
					{pollData?.status === 'IN_PROGRESS' && (
						<button
							className="flex-1 bg-blue-800 hover:bg-blue-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors shadow-sm"
							type="button"
							onClick={() => setShowResults(false)}
						>
							투표하러가기
						</button>
					)}
					{/* 진행중이거나 완료된 투표일 때 결과보기 버튼 표시 */}
					{pollData?.isRevotable && (
						<button
							className={`${
								pollData.status === 'IN_PROGRESS' ? 'flex-1' : 'w-full'
							} bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors`}
							type="button"
							onClick={() => setShowResults(true)}
						>
							{pollData.status === 'IN_PROGRESS'
								? '결과보기'
								: '완료한 투표 & 결과보기'}
						</button>
					)}

					{/* TODO: 완료된 투표 & 종료된 투표 결과보기 버튼 표시 */}
					{/* 종료된 투표일 때만 결과보기 버튼 표시 */}
					{pollData?.status === 'EXPIRED' && (
						<button
							className="w-full bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors"
							type="button"
							onClick={() => setShowResults(true)}
						>
							결과보기
						</button>
					)}
					<button
						className="bg-slate-100 hover:bg-slate-100 py-3 px-4 rounded-lg font-semibold"
						type="button"
						title="공유하기"
					>
						<Share2 className="w-5 h-5 text-slate-700" />
					</button>
				</div>
			</div>
		</Card>
	);
}
