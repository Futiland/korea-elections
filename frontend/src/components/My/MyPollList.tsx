import { Button } from '@/components/ui/button';
import { ChevronRight } from 'lucide-react';
import PollListItem from './PollListItem';
import { MyPollData, PollStatus } from '@/lib/types/poll';

export interface MyPollListProps {
	title: string;
	items: MyPollData[];
}

export default function MyPollList({ title, items }: MyPollListProps) {
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
				{items.map((item) => (
					<PollListItem key={item.pollId} item={item} />
				))}
			</div>
		</div>
	);
}
