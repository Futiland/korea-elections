import Image from 'next/image';
import { CheckCircle2 } from 'lucide-react';
import { CandidateDate } from '@/lib/types/election';

type CandidateCardProps = {
	item: CandidateDate;
	handleVoteClick: (candidateId: number) => void;
};

export function CandidateCard({ item, handleVoteClick }: CandidateCardProps) {
	const { name, party, number } = item;

	return (
		<div
			className="flex items-center justify-between p-4 rounded-xl shadow-md bg-white
			border border-gray-200 hover:border-gray-200 transition-all duration-200 ease-in-out
		"
			onClick={() => handleVoteClick(item.id)}
		>
			<div className="flex items-center gap-4">
				<Image
					src={`/img/logo-c.svg`}
					alt={name}
					width={40}
					height={40}
					className="rounded-full"
				/>
				<div>
					<p className="font-semibold text-base">
						기호 {number}번 {name}
					</p>
					<p className="text-sm text-muted-foreground">{party}</p>
				</div>
			</div>
			<CheckCircle2 className="w-6 h-6 text-muted-foreground" />
		</div>
	);
}
