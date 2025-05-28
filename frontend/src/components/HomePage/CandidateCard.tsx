import Image from 'next/image';
import { CheckCircle2 } from 'lucide-react';
import { CandidateDate } from '@/lib/types/election';
import { useState } from 'react';

type CandidateCardProps = {
	item: CandidateDate;
	handleVoteClick: (candidateId: number) => void;
	isSelected?: boolean;
	selectedCandidateId: number | null;
};

export function CandidateCard({
	item,
	handleVoteClick,
	isSelected,
	selectedCandidateId,
}: CandidateCardProps) {
	const { name, party, number, id } = item;

	return (
		<div
			className={`relative flex items-center justify-between shadow-md rounded-xl p-4 bg-white hover:bg-slate-50
			${isSelected ? 'ring-2 ring-blue-800' : 'ring-1 ring-gray-200'}
			transition-colors duration-200 ease-in-out`}
			onClick={() => handleVoteClick(item.id)}
		>
			<div className="flex items-center gap-4">
				<div className="relative w-[40px] h-[40px]">
					<Image
						src={`/img/candidate-0${number}.png`}
						alt={name}
						fill
						sizes="40px"
						className="rounded-full object-contain"
					/>
				</div>
				<div>
					<p className="font-semibold text-base">
						기호 {number}번 {name}
					</p>
					<p className="text-sm text-muted-foreground">{party}</p>
				</div>
			</div>
			<CheckCircle2
				className={`w-6 h-6 ${
					isSelected ? 'text-blue-800' : 'text-muted-foreground'
				}`}
			/>
			{selectedCandidateId && !isSelected && (
				<div className="bg-slate-50 opacity-70 absolute left-0 top-0 w-full h-full rounded-xl" />
			)}
		</div>
	);
}
