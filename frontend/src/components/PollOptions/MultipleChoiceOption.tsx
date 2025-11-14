import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';

interface MultipleChoiceOptionProps {
	options: string[];
	selectedValues?: string[];
	onChange?: (values: string[]) => void;
}

export default function MultipleChoiceOption({
	options,
	selectedValues = [],
	onChange,
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
			{options.map((option, index) => (
				<div
					key={index}
					className="flex items-center gap-3 cursor-pointer p-2 rounded"
				>
					<Checkbox
						id={`checkbox-${index}`}
						checked={selectedValues.includes(option)}
						onCheckedChange={(checked) => handleOptionChange(option, !!checked)}
					/>
					<Label
						htmlFor={`checkbox-${index}`}
						className="cursor-pointer text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
					>
						{option}
					</Label>
				</div>
			))}
		</div>
	);
}
