import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

function PasswordField({
	label,
	placeholder,
	value,
	onChange,
}: {
	label: string;
	placeholder: string;
	value: string;
	onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}) {
	const [visible, setVisible] = useState(false);

	return (
		<div>
			<label className="text-sm">{label}</label>
			<div className="relative">
				<Button
					type="button"
					variant="ghost"
					size="icon"
					className="absolute right-3 top-1/2 -translate-y-1/2"
					onClick={() => setVisible((prev) => !prev)}
				>
					{visible ? (
						<Eye className="w-5 h-5" />
					) : (
						<EyeOff className="w-5 h-5" />
					)}
				</Button>
				<Input
					type={visible ? 'text' : 'password'}
					placeholder={placeholder}
					className="h-10"
					value={value}
					onChange={onChange}
					required
				/>
			</div>
		</div>
	);
}
export default PasswordField;
