import { CandidateResult } from '@/lib/types/election';

type AgesChartProps = {
	data: CandidateResult[];
	totalVoteCount: number;
};

export default function AgesChart({ data, totalVoteCount }: AgesChartProps) {
	return (
		<div className="relative bg-white rounded-xl px-4 ">
			{/* 데이터 막대 */}
			<div className="space-y-[10px] relative">
				{data?.map((item, idx) => {
					const percent = (item.voteCount / totalVoteCount) * 100;
					return (
						<div key={idx} className="flex items-center justify-between">
							<div className="flex items-center w-[calc(100%-50px)]">
								<div
									className={`rounded-md min-w-[2px] transition-all duration-300 h-7`}
									style={{
										backgroundColor: item.partyColor,
										transform: 'scaleX(0)',
										transformOrigin: 'left',
										animation: `growX 1s ease-out forwards`,
										animationDelay: `${idx * 0.1}s`,
										width: `${percent}%`,
									}}
								/>
								<div className="text-sm font-semibold text-black min-w-[20px] ml-2 animate-slide-in">
									{item.voteCount}
								</div>
							</div>
							<p className="text-sm text-slate-400">{item.name}</p>
						</div>
					);
				})}
			</div>
		</div>
	);
}
