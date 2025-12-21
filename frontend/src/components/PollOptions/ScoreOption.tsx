import { Slider } from '@/components/ui/slider';
import { useState, useEffect } from 'react';

interface ScoreOptionProps {
	maxScore?: number;
	selectedScore?: number;
	onChange?: (score: number) => void;
	isVoted: boolean;
}

const INITIAL_SCORE = 5;

export default function ScoreOption({
	maxScore = 10,
	selectedScore: initialScore = INITIAL_SCORE,
	onChange,
	isVoted,
}: ScoreOptionProps) {
	const [selectedScore, setSelectedScore] = useState(initialScore);

	useEffect(() => {
		setSelectedScore(initialScore);
	}, [initialScore]);

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
					min={0}
					max={maxScore}
					step={1}
					className="w-full"
					style={{
						cursor: 'pointer',
					}}
				/>
				<div className="flex justify-between text-xs text-slate-500 mt-2">
					{Array.from({ length: maxScore + 1 }, (_, i) => (
						<button
							key={i}
							onClick={() => handleValueChange([i])}
							className={`text-center px-1 py-1 transition-colors rounded-4xl w-6 h-6 ${
								selectedScore === i
									? 'text-primary font-bold bg-slate-200 '
									: 'text-slate-500 hover:text-slate-700'
							}`}
							disabled={isVoted}
						>
							{i}
						</button>
					))}
				</div>
			</div>
		</div>
	);
}
