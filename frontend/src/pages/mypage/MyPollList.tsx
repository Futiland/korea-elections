import { Button } from '@/components/ui/button';
import { ChevronRight } from 'lucide-react';
import PollListItem from './PollListItem';

export interface MyPollListProps {
	title: string;
	// items: {
	//   title: string;
	//   description: string;
	//   status: 'progress' | 'stopped' | 'ended';
	//   participantCount: number;
	//   endDate: string;
	// }[];
}

export default function MyPollList({ title }: MyPollListProps) {
	// TODO: 리스트 없을 때 빈 상태 표시
	return (
		<div className="py-2">
			<div className="flex items-center justify-between py-2">
				<p className="text-lg font-bold">{title}</p>
				<Button variant="ghost" className="text-slate-600 h-10">
					더보기
					<ChevronRight className="w-4 h-4" />
				</Button>
			</div>

			<div className="space-y-3">
				<PollListItem
					title="샘플 투표 제목 1"
					description="이것은 샘플 투표 설명입니다. 투표에 대한 자세한 내용을 여기에 표시합니다."
					status="progress"
					participantCount={25}
					endDate="2024-12-31"
					startDate="2024-12-01"
					onViewDetails={() => console.log('상세보기 클릭')}
				/>
				<PollListItem
					title="샘플 투표 제목 2"
					description="또 다른 샘플 투표 설명입니다."
					status="ended"
					participantCount={42}
					endDate="2024-12-25"
					startDate="2024-12-01"
					onViewDetails={() => console.log('상세보기 클릭')}
				/>
			</div>
		</div>
	);
}
