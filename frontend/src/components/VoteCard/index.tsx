import { useState } from 'react';
import { Card, CardHeader, CardTitle } from '../ui/card';
import StatusBadge from './StatusBadge';
import SingleChoiceOption from './SingleChoiceOption';
import MultipleChoiceOption from './MultipleChoiceOption';
import ScoreOption from './ScoreOption';
import { Share2 } from 'lucide-react';

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
	return (
		<Card className="w-full hover:bg-slate-50 transition-colors">
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
				<div className="mb-6 space-y-4 flex flex-col items-start">
					{/* 단일 선택 옵션 */}

					<SingleChoiceOption
						options={[
							'단일 선택 A',
							'단일 선택 B',
							'단일 선택 단일 선택단일 선택단일 선택 C',
							'단일 선택 D',
							'단일 선택 E',
							'단일 선택 F',
							'단일 선택 G',
						]}
						value={selectedSingleChoice}
						onValueChange={setSelectedSingleChoice}
					/>

					{/* 다중 선택 옵션 */}
					<MultipleChoiceOption
						options={[
							'다중 선택 1',
							'다중 선택 2',
							'다중 선택 3',
							'다중 선택 4',
							'다중 선택 다중 선택다중 선택다중 선택 5',
						]}
						selectedValues={selectedMultipleChoices}
						onChange={setSelectedMultipleChoices}
					/>

					{/* 점수제 옵션 */}
					<ScoreOption
						maxScore={10}
						selectedScore={selectedScore}
						onChange={setSelectedScore}
					/>
				</div>

				{/* 버튼 영역 */}
				<div className="flex gap-3">
					{/* 진행중인 투표일 때만 투표하러가기 버튼 표시 */}
					{data.status === 'progress' && (
						<button
							className="flex-1 bg-blue-900 hover:bg-blue-800 text-white py-3 px-4 rounded-lg font-semibold transition-colors"
							type="button"
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
						>
							결과보기
						</button>
					)}
				</div>
			</div>
		</Card>
	);
}
