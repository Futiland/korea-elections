import { useEffect, useState } from 'react';
import SingleChoiceOption from '../PollOptions/SingleChoiceOption';
import MultipleChoiceOption from '../PollOptions/MultipleChoiceOption';
import ScoreOption from '../PollOptions/ScoreOption';
import { QuestionType } from '@/lib/types/poll';
import { OptionData } from '@/lib/types/poll';

type PollCardOptionsProps = {
	pollId: number;
	responseType: QuestionType;
	options: OptionData[];
	onChange: (value: number[] | number) => void;
	isVoted: boolean;
};

export default function PollCardOptions({
	pollId,
	responseType,
	options,
	onChange,
	isVoted,
}: PollCardOptionsProps) {
	const [selectedSingleChoice, setSelectedSingleChoice] = useState<string>('');
	const [selectedMultipleChoices, setSelectedMultipleChoices] = useState<
		string[]
	>([]);
	const [selectedScore, setSelectedScore] = useState<number>(5);

	// 점수제의 경우 초기값을 부모에게 전달
	useEffect(() => {
		if (responseType === 'SCORE') {
			onChange(selectedScore);
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []); // 마운트 시 한 번만 실행

	const handleSingleChoiceChange = (value: string) => {
		setSelectedSingleChoice(value);

		onChange(Number(value));
	};

	const handleMultipleChoiceChange = (values: string[]) => {
		setSelectedMultipleChoices(values);
		const selectedOptions = values.map(Number);

		onChange(selectedOptions);
	};

	const handleScoreChange = (score: number) => {
		setSelectedScore(score);
		onChange(score);
	};

	return (
		<div className="mb-6 space-y-4 flex flex-col items-start">
			{responseType === 'SINGLE_CHOICE' && (
				<SingleChoiceOption
					pollId={pollId}
					options={options}
					value={selectedSingleChoice}
					onValueChange={handleSingleChoiceChange}
					isVoted={isVoted}
				/>
			)}

			{responseType === 'MULTIPLE_CHOICE' && (
				<MultipleChoiceOption
					options={options}
					selectedValues={selectedMultipleChoices}
					onChange={handleMultipleChoiceChange}
					isVoted={isVoted}
				/>
			)}

			{responseType === 'SCORE' && (
				<ScoreOption
					maxScore={10}
					selectedScore={selectedScore}
					onChange={handleScoreChange}
					isVoted={isVoted}
				/>
			)}
		</div>
	);
}
