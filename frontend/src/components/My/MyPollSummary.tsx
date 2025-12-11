import { Button } from '@/components/ui/button';
import { ChevronRight } from 'lucide-react';
import PollListItem from './PollSummaryItem';
import { MyPollData, PollStatus } from '@/lib/types/poll';
import router from 'next/router';
import { Spinner } from '../ui/spinner';

export interface MyPollSummaryProps {
	title: string;
	items: MyPollData[];
	moreUrl: string;
	isLoading: boolean;
}

export default function MyPollSummary({
	title,
	items,
	moreUrl,
	isLoading,
}: MyPollSummaryProps) {
	return (
		<div className="py-2">
			<div className="flex items-center justify-between py-2">
				<p className="text-lg font-bold">{title}</p>
				<Button
					variant="ghost"
					className="text-slate-600 h-10"
					onClick={() => router.push(moreUrl)}
				>
					더보기
					<ChevronRight className="w-4 h-4" />
				</Button>
			</div>

			<div className="space-y-3">
				{isLoading && <Spinner className="w-6 h-6 text-blue-500" />}
				{items.length > 0 &&
					items.map((item) => <PollListItem key={item.id} item={item} />)}
			</div>
		</div>
	);
}
