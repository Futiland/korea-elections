import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import { OptionData } from '@/lib/types/poll';

interface MultipleChoiceOptionProps {
	options: OptionData[];
	selectedValues?: string[];
	onChange?: (values: string[]) => void;
	isVoted: boolean;
}

export default function MultipleChoiceOption({
	options,
	selectedValues = [],
	onChange,
	isVoted,
}: MultipleChoiceOptionProps) {
	const handleOptionChange = (option: string, checked: boolean) => {
		if (checked) {
			onChange?.([...selectedValues, option]);
		} else {
			onChange?.(selectedValues.filter((value) => value !== option));
		}
	};

	return (
		<div className="flex flex-wrap items-start gap-2 w-full">
			{options.map((option) => (
				<div
					key={option.id}
					className="flex items-center gap-2 cursor-pointer p-2 rounded"
				>
					<Checkbox
						id={`checkbox-${option.id}`}
						checked={selectedValues.includes(option.id.toString())}
						onCheckedChange={(checked) =>
							handleOptionChange(option.id.toString(), !!checked)
						}
						disabled={isVoted}
					/>
					<Label
						htmlFor={`checkbox-${option.id}`}
						className="cursor-pointer text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
					>
						{option.optionText}
					</Label>
				</div>
			))}
		</div>
	);
}
