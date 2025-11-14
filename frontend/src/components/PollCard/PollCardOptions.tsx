import SingleChoiceOption from '../PollOptions/SingleChoiceOption';
import MultipleChoiceOption from '../PollOptions/MultipleChoiceOption';
import ScoreOption from '../PollOptions/ScoreOption';

type PollCardOptionsProps = {
	selectedSingleChoice: string;
	setSelectedSingleChoice: (v: string) => void;
	selectedMultipleChoices: string[];
	setSelectedMultipleChoices: (v: string[]) => void;
	selectedScore: number;
	setSelectedScore: (v: number) => void;
	options: { id: number; optionText: string; optionOrder: number }[];
};

export default function PollCardOptions({
	selectedSingleChoice,
	setSelectedSingleChoice,
	selectedMultipleChoices,
	setSelectedMultipleChoices,
	selectedScore,
	setSelectedScore,
	options,
}: PollCardOptionsProps) {
	return (
		<div className="mb-6 space-y-4 flex flex-col items-start">
			{/* 단일 선택 옵션 */}
			<SingleChoiceOption
				options={options}
				value={selectedSingleChoice}
				onValueChange={setSelectedSingleChoice}
			/>

			{/* 다중 선택 옵션 */}
			<MultipleChoiceOption
				options={options}
				selectedValues={selectedMultipleChoices}
				onChange={setSelectedMultipleChoices}
			/>

			{/* 점수제 옵션 */}
			<ScoreOption
				maxScore={10}
				selectedScore={selectedScore}
				onChange={setSelectedScore}
			/>
		</div>
	);
}
