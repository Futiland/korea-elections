import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Label } from '@/components/ui/label';

interface SingleChoiceOptionProps {
	options: { id: number; optionText: string; optionOrder: number }[];
	value?: string;
	onValueChange?: (value: string) => void;
}

export default function SingleChoiceOption({
	options,
	value,
	onValueChange,
}: SingleChoiceOptionProps) {
	return (
		<RadioGroup
			value={value}
			onValueChange={onValueChange}
			className="flex flex-wrap items-start gap-2 w-full"
		>
			{options.map((option, index) => (
				<div
					key={index}
					className="flex items-center gap-3 cursor-pointer p-2 rounded"
				>
					<RadioGroupItem value={option.id.toString()} id={`option-${index}`} />
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
