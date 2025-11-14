import { useState } from 'react';
import { Card, CardHeader, CardTitle } from '../ui/card';
import StatusBadge from '../StatusBadge';
import PollCardOptions from './PollCardOptions';
import { Share2 } from 'lucide-react';
import PollCardResults from './PollCardResults';
import { PublicPollData, PollStatus } from '@/lib/types/poll';
import { formatDate } from '@/lib/date';

interface PollCardProps {
	pollData?: PublicPollData;
}

export default function PollCard({ pollData }: PollCardProps) {
	const [showResults, setShowResults] = useState<boolean>(false);

	const handleOptionChange = (value: string | string[] | number) => {
		// 선택된 값 처리 (필요시 API 호출 등)
		console.log('Selected value:', value);
	};

	return (
		<Card className="w-full transition-colors">
			<div className="px-6 py-4">
				{/* 헤더 영역 - 상태값, 제목, 공유 버튼 */}
				<div className="flex items-start justify-between mb-2">
					<div className="flex items-center gap-3">
						<StatusBadge status={pollData?.status ?? 'IN_PROGRESS'} />

						<span className="text-xs text-slate-500">
							{pollData?.startAt && pollData?.endAt
								? `${formatDate(
										pollData.startAt,
										'yyyy-MM-dd HH:mm'
								  )} ~ ${formatDate(pollData.endAt, 'yyyy-MM-dd HH:mm')}`
								: ''}
						</span>
					</div>
					<button
						className="p-2 hover:bg-slate-100 rounded-full transition-colors"
						type="button"
						title="공유하기"
					>
						<Share2 className="w-4 h-4 text-slate-600" />
					</button>
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
				</div>
			</div>
		</Card>
	);
}
