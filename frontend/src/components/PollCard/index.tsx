import type { PublicPollData } from '@/lib/types/poll';
import PollCardView from './PollCardView';
import { usePollCardPresenter } from './usePollCardPresenter';

interface PollCardProps {
	pollData: PublicPollData;
}

export default function PollCard({ pollData }: PollCardProps) {
	const presenter = usePollCardPresenter({ pollData });

	return <PollCardView {...presenter} />;
}
