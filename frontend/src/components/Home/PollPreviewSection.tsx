import { Button } from '@/components/ui/button';
import PollPreviewCard from './cards/PollListTypeCard';
import { PublicPollData } from '@/lib/types/poll';
import type { ComponentType } from 'react';
import type { PollCardProps } from './types';

interface PollPreviewSectionProps {
	title: string;
	description?: string;
	moreLabel?: string;
	polls?: PublicPollData[];
	onClickMore?: () => void;
	onClickPoll?: (pollId: string) => void;
	CardComponent?: ComponentType<PollCardProps>;
}

export default function PollPreviewSection({
	title = '모두의 투표',
	description = '',
	moreLabel = '더보기',
	polls = [],
	onClickMore,
	onClickPoll,
	CardComponent = PollPreviewCard,
}: PollPreviewSectionProps) {
	return (
		<section className="w-full max-w-5xl mx-auto px-4 py-8 md:py-10">
			<header className="mb-4">
				<div className="flex items-baseline justify-between gap-4">
					<h2 className="text-lg font-semibold md:text-xl">{title}</h2>
					<Button
						variant="ghost"
						size="sm"
						className="text-xs md:text-sm"
						type="button"
						onClick={onClickMore}
					>
						{moreLabel}
					</Button>
				</div>
				{description && (
					<p className="text-xs text-muted-foreground md:text-sm">
						{description}
					</p>
				)}
			</header>

			<div className="grid">
				{polls.map((poll) => (
					<CardComponent
						key={poll.id}
						pollData={poll}
						onClickPoll={onClickPoll}
					/>
				))}
			</div>
		</section>
	);
}
