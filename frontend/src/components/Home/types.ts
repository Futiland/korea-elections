import type { PublicPollData } from '@/lib/types/poll';

export interface PollCardProps {
	pollData: PublicPollData;
	onClickPoll?: (pollId: string) => void;
}
