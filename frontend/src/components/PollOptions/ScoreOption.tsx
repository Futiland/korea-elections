import { Slider } from '@/components/ui/slider';
import { useState } from 'react';

interface ScoreOptionProps {
	maxScore?: number;
	selectedScore?: number;
	onChange?: (score: number) => void;
	isVoted: boolean;
}

export default function ScoreOption({
	maxScore = 10,
	selectedScore: initialScore = 1,
	onChange,
	isVoted,
}: ScoreOptionProps) {
	const [selectedScore, setSelectedScore] = useState(initialScore);

	const handleValueChange = (values: number[]) => {
		const newScore = values[0];
		setSelectedScore(newScore);
		onChange?.(newScore);
	};

	return (
		<div className="flex flex-col gap-3 w-full">
			<div className="flex items-center justify-between">
				{/* <span className="text-sm font-medium text-slate-700">점수 평가</span>
				<span className="text-sm font-bold text-blue-600">{selectedScore}</span> */}
			</div>
			<div className="px-2">
				<Slider
					value={[selectedScore]}
					onValueChange={handleValueChange}
					min={1}
					max={maxScore}
					step={1}
					className="w-full"
				/>
				<div className="flex justify-between text-xs text-slate-500 mt-2">
					{Array.from({ length: maxScore }, (_, i) => (
						<button
							key={i + 1}
							onClick={() => handleValueChange([i + 1])}
							className={`text-center px-1 py-1 transition-colors rounded-4xl w-6 h-6 ${
								selectedScore === i + 1
									? 'text-primary font-bold bg-slate-200 '
									: 'text-slate-500 hover:text-slate-700'
							}`}
							disabled={isVoted}
						>
							{i + 1}
						</button>
					))}
				</div>
			</div>
		</div>
	);
}
