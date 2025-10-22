import { useState } from 'react';
import { Card, CardHeader, CardTitle } from '../ui/card';
import StatusBadge from '../StatusBadge';
import VoteCardOptions from './VoteCardOptions';
import { Share2 } from 'lucide-react';
import VoteCardResults from './VoteCardResults';

interface VoteData {
	id: number;
	title: string;
	description: string;
	status: 'progress' | 'stopped' | 'ended';
	endDate: string;
	voteCount: number;
}

interface VoteCardProps {
	voteData?: VoteData;
}

export default function VoteCard({ voteData }: VoteCardProps) {
	// 기본값 설정
	const data = voteData || {
		id: 1,
		title: '모두의 투표 제목입니다. 투표해주세요.',
		description:
			'이 투표는 모두가 참여할 수 있는 예시 투표입니다. 아래 옵션 중 하나를 선택하거나 점수를 매겨주세요.',
		status: 'progress' as const,
		endDate: '2024-07-01',
		voteCount: 150,
	};

	// 상태 관리
	const [selectedSingleChoice, setSelectedSingleChoice] = useState<string>('');
	const [selectedMultipleChoices, setSelectedMultipleChoices] = useState<
		string[]
	>([]);
	const [selectedScore, setSelectedScore] = useState<number>(1);
	const [showResults, setShowResults] = useState<boolean>(false);
	return (
		<Card className="w-full transition-colors">
			<div className="px-6 py-4">
				{/* 헤더 영역 - 상태값, 제목, 공유 버튼 */}
				<div className="flex items-start justify-between mb-2">
					<div className="flex items-center gap-3">
						<StatusBadge status={data.status} />
						<span className="text-xs text-slate-500">
							~ {data.endDate} 까지
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
					<CardTitle className="text-lg">{data.title}</CardTitle>
				</CardHeader>

				{/* 상세 내용 */}
				<div className="mb-6 text-sm text-slate-600 leading-relaxed">
					{data.description}
				</div>

				{/* 선택 옵션 */}
				{!showResults && (
					<VoteCardOptions
						selectedSingleChoice={selectedSingleChoice}
						setSelectedSingleChoice={setSelectedSingleChoice}
						selectedMultipleChoices={selectedMultipleChoices}
						setSelectedMultipleChoices={setSelectedMultipleChoices}
						selectedScore={selectedScore}
						setSelectedScore={setSelectedScore}
					/>
				)}

				{/* 결과 차트 */}
				{showResults && <VoteCardResults />}

				{/* 버튼 영역 */}
				<div className="flex gap-3">
					{/* 진행중인 투표일 때만 투표하러가기 버튼 표시 */}
					{data.status === 'progress' && (
						<button
							className="flex-1 bg-blue-900 hover:bg-blue-800 text-white py-3 px-4 rounded-lg font-semibold transition-colors"
							type="button"
							onClick={() => setShowResults(false)}
						>
							투표하러가기
						</button>
					)}
					{/* 진행중이거나 완료된 투표일 때 결과보기 버튼 표시 */}
					{data.status === 'progress' && (
						<button
							className={`${
								data.status === 'progress' ? 'flex-1' : 'w-full'
							} bg-slate-200 hover:bg-slate-300 text-slate-800 py-3 px-4 rounded-lg font-semibold transition-colors`}
							type="button"
							onClick={() => setShowResults(true)}
						>
							{data.status === 'progress'
								? '결과보기'
								: '완료한 투표 & 결과보기'}
						</button>
					)}
					{/* 종료된 투표일 때만 결과보기 버튼 표시 */}
					{data.status === 'ended' && (
						<button
							className="w-full bg-slate-200 hover:bg-slate-300 text-slate-800 py-3 px-4 rounded-lg font-semibold transition-colors"
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
