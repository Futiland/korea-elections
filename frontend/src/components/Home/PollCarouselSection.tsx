import { Button } from '@/components/ui/button';
import useEmblaCarousel from 'embla-carousel-react';
import { useCallback, useEffect, useState, type ComponentType } from 'react';

import { PublicPollData } from '@/lib/types/poll';
import type { PollCardProps } from './types';
import PollCarouselCard from './cards/PollCarouselCard';

interface PollCarouselSectionProps {
	title?: string;
	description?: string;
	moreLabel?: boolean;
	polls?: PublicPollData[];
	onClickMore?: () => void;
	onClickPoll?: (pollId: string) => void;
	CardComponent?: ComponentType<PollCardProps>;
}

const DEFAULT_POLLS: PublicPollData[] = [
	{
		id: 1,
		title: '이번 주 가장 기대되는 공약은 무엇인가요?',
		description:
			'후보들의 공약 중에서 가장 마음에 드는 공약을 선택해 주세요. 여러분의 의견이 여론을 바꿉니다.',
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
		title: '우리 동네에 가장 시급한 정책은?',
		description:
			'교통, 복지, 교육 등 우리 동네를 더 좋게 만들기 위해 가장 먼저 필요한 정책을 골라 주세요.',
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
		title: '다음 대통령에게 가장 기대하는 역량은?',
		description:
			'외교, 경제, 소통 등 앞으로의 5년을 위해 가장 중요한 역량이 무엇인지 선택해 주세요.',
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

export default function PollCarouselSection({
	title = '',
	description = '',
	moreLabel = false,
	polls = DEFAULT_POLLS,
	onClickMore,
	onClickPoll,
	CardComponent = PollCarouselCard,
}: PollCarouselSectionProps) {
	const [emblaRef, emblaApi] = useEmblaCarousel({
		loop: true,
		skipSnaps: false,
		align: 'start',
	});

	const [selectedIndex, setSelectedIndex] = useState(0);
	const [scrollSnaps, setScrollSnaps] = useState<number[]>([]);

	const onSelect = useCallback(() => {
		if (!emblaApi) return;
		setSelectedIndex(emblaApi.selectedScrollSnap());
	}, [emblaApi]);

	useEffect(() => {
		if (!emblaApi) return;

		emblaApi.reInit();
		setScrollSnaps(emblaApi.scrollSnapList());
		onSelect();
		emblaApi.on('select', onSelect);

		return () => {
			emblaApi.off('select', onSelect);
		};
	}, [emblaApi, onSelect, polls]);

	return (
		<section className="w-full max-w-5xl pl-4 py-4 md:py-10">
			{title && (
				<header>
					<div className="flex items-baseline justify-between gap-4">
						<h2 className="text-lg font-semibold md:text-xl">{title}</h2>
						{moreLabel && (
							<Button
								variant="ghost"
								size="sm"
								className="text-xs md:text-sm"
								type="button"
								onClick={onClickMore}
							>
								<span>더보기</span>
							</Button>
						)}
					</div>
					{description && (
						<p className="text-xs text-muted-foreground md:text-sm">
							{description}
						</p>
					)}
				</header>
			)}

			<div className="block md:hidden ">
				<div className="overflow-hidden" ref={emblaRef}>
					<div className="flex py-4">
						{polls.map((poll) => (
							<div
								key={poll.id}
								className="min-w-0 flex-[0_0_95%] pr-3 sm:flex-[0_0_80%]"
							>
								<CardComponent pollData={poll} onClickPoll={onClickPoll} />
							</div>
						))}
					</div>
				</div>

				{/* 하단 페이지네이션 */}
				<div className="mt-1 flex items-center justify-center">
					<div className="flex items-center gap-2">
						{scrollSnaps.map((_, index) => {
							const isActive = index === selectedIndex;
							return (
								<button
									key={index}
									type="button"
									className={`h-2 w-2 rounded-full transition-colors ${
										isActive ? 'bg-blue-600' : 'bg-slate-300'
									}`}
									onClick={() => emblaApi && emblaApi.scrollTo(index)}
									aria-label={`${index + 1}번 슬라이드로 이동`}
								/>
							);
						})}
					</div>
				</div>
			</div>
		</section>
	);
}
