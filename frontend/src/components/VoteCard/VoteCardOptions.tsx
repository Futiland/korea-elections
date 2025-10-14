import SingleChoiceOption from '../VoteOptions/SingleChoiceOption';
import MultipleChoiceOption from '../VoteOptions/MultipleChoiceOption';
import ScoreOption from '../VoteOptions/ScoreOption';

type VoteCardOptionsProps = {
	selectedSingleChoice: string;
	setSelectedSingleChoice: (v: string) => void;
	selectedMultipleChoices: string[];
	setSelectedMultipleChoices: (v: string[]) => void;
	selectedScore: number;
	setSelectedScore: (v: number) => void;
};

export default function VoteCardOptions({
	selectedSingleChoice,
	setSelectedSingleChoice,
	selectedMultipleChoices,
	setSelectedMultipleChoices,
	selectedScore,
	setSelectedScore,
}: VoteCardOptionsProps) {
	return (
		<div className="mb-6 space-y-4 flex flex-col items-start">
			{/* 단일 선택 옵션 */}
			<SingleChoiceOption
				options={[
					'단일 선택 A',
					'단일 선택 B',
					'단일 선택 단일 선택단일 선택단일 선택 C',
					'단일 선택 D',
					'단일 선택 E',
					'단일 선택 F',
					'단일 선택 G',
				]}
				value={selectedSingleChoice}
				onValueChange={setSelectedSingleChoice}
			/>

			{/* 다중 선택 옵션 */}
			<MultipleChoiceOption
				options={[
					'다중 선택 1',
					'다중 선택 2',
					'다중 선택 3',
					'다중 선택 4',
					'다중 선택 다중 선택다중 선택다중 선택 5',
				]}
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
