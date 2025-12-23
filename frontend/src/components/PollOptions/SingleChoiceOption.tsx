import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Label } from '@/components/ui/label';
import { OptionData } from '@/lib/types/poll';

interface SingleChoiceOptionProps {
	pollId: number;
	options: OptionData[];
	value?: string;
	onValueChange?: (value: string) => void;
	isVoted: boolean;
}

export default function SingleChoiceOption({
	pollId,
	options,
	value,
	onValueChange,
	isVoted,
}: SingleChoiceOptionProps) {
	return (
		<RadioGroup
			value={value}
			onValueChange={onValueChange}
			className="flex flex-wrap items-start gap-2 w-full"
			disabled={isVoted}
		>
			{options.map((option, index) => (
				<div
					key={pollId}
					className="flex items-center gap-2 cursor-pointer p-2 rounded"
				>
					<RadioGroupItem
						value={option.id.toString()}
						id={`option-${pollId}`}
					/>
					<Label
						htmlFor={`option-${index}`}
						className={`cursor-pointer text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70`}
					>
						{option.optionText}
					</Label>
				</div>
			))}
		</RadioGroup>
	);
}
