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

const DEFAULT_POLLS: PublicPollData[] = [
	{
		id: 1,
		title: '가장 좋아하는 캐릭터는?',
		description:
			'가장 좋아하는 캐릭터를 선택해 주세요. 여러분의 의견이 여론을 바꿉니다.',
		startAt: new Date('2025-03-01'),
		endAt: new Date('2025-03-07'),
		responseType: 'SINGLE_CHOICE',
		status: 'IN_PROGRESS',
		createdAt: new Date('2025-03-01'),
		creatorInfo: {
			accountId: 1,
			name: 'John Doe',
		},
		responseCount: 100,
		isVoted: false,
		options: [
			{
				id: 1,
				optionText: '옵션 1',
				optionOrder: 1,
			},
		],
		isRevotable: true,
	},
	{
		id: 2,
		title: '생일선물로 뭘 받고 싶나요?',
		description: '생일선물로 뭘 받고 싶나요? 여러분의 의견이 여론을 바꿉니다.',
		startAt: new Date('2025-03-02'),
		endAt: new Date('2025-03-09'),
		responseType: 'SINGLE_CHOICE',
		status: 'IN_PROGRESS',
		createdAt: new Date('2025-03-02'),
		creatorInfo: {
			accountId: 2,
			name: 'Jane Doe',
		},
		responseCount: 100,
		isVoted: false,
		options: [
			{
				id: 1,
				optionText: '옵션 1',
				optionOrder: 1,
			},
		],
		isRevotable: true,
	},
	{
		id: 3,
		title: '어떤 음식 조합이 가장 맛있을까요?',
		description:
			'어떤 음식 조합이 가장 맛있을까요? 여러분의 의견이 여론을 바꿉니다.',
		startAt: new Date('2025-03-03'),
		endAt: new Date('2025-03-10'),
		responseType: 'SINGLE_CHOICE',
		status: 'IN_PROGRESS',
		createdAt: new Date('2025-03-03'),
		creatorInfo: {
			accountId: 3,
			name: 'John Doe',
		},
		responseCount: 100,
		isVoted: false,
		options: [
			{
				id: 1,
				optionText: '옵션 1',
				optionOrder: 1,
			},
		],
		isRevotable: true,
	},
];

export default function PollPreviewSection({
	title = '인기있는 모두의 투표',
	description = '',
	moreLabel = '더보기',
	polls = DEFAULT_POLLS,
	onClickMore,
	onClickPoll,
	CardComponent = PollPreviewCard,
}: PollPreviewSectionProps) {
	return (
		<section className="w-full max-w-5xl px-4 py-8 md:py-10">
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

			<div className="grid gap-4 md:grid-cols-3">
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
