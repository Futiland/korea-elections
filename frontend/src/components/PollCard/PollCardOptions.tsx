import { useState } from 'react';
import SingleChoiceOption from '../PollOptions/SingleChoiceOption';
import MultipleChoiceOption from '../PollOptions/MultipleChoiceOption';
import ScoreOption from '../PollOptions/ScoreOption';
import { QuestionType } from '@/lib/types/poll';
import { OptionData } from '@/lib/types/poll';

type PollCardOptionsProps = {
	responseType: QuestionType;
	options: OptionData[];
	onChange?: (value: OptionData[]) => void;
};

export default function PollCardOptions({
	responseType,
	options,
	onChange,
}: PollCardOptionsProps) {
	const [selectedSingleChoice, setSelectedSingleChoice] = useState<string>('');
	const [selectedMultipleChoices, setSelectedMultipleChoices] = useState<
		string[]
	>([]);
	const [selectedScore, setSelectedScore] = useState<number>(1);

	const handleSingleChoiceChange = (value: string) => {
		const selecteOoption = options.find(
			(option) => option.id === Number(value)
		);

		setSelectedSingleChoice(value);

		if (selecteOoption) {
			onChange?.([selecteOoption]);
		}
	};

	const handleMultipleChoiceChange = (values: string[]) => {
		const selectedOptions = options.filter((option) =>
			values.includes(option.id.toString())
		);
		setSelectedMultipleChoices(values);
		onChange?.(selectedOptions);
	};

	const handleScoreChange = (score: number) => {
		setSelectedScore(score);
		// onChange?.(score);
	};

	return (
		<div className="mb-6 space-y-4 flex flex-col items-start">
			{responseType === 'SINGLE_CHOICE' && (
				<SingleChoiceOption
					options={options}
					value={selectedSingleChoice}
					onValueChange={handleSingleChoiceChange}
				/>
			)}

			{responseType === 'MULTIPLE_CHOICE' && (
				<MultipleChoiceOption
					options={options}
					selectedValues={selectedMultipleChoices}
					onChange={handleMultipleChoiceChange}
				/>
			)}

			{responseType === 'SCORE' && (
				<ScoreOption
					maxScore={10}
					selectedScore={selectedScore}
					onChange={handleScoreChange}
				/>
			)}
		</div>
	);
}
