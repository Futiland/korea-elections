import { Dialog, DialogContent, DialogTrigger } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { useState, ReactNode } from 'react';

interface AlertDialogProps {
	open?: boolean;
	onOpenChange?: (open: boolean) => void;
	message?: string;
	actions?: ReactNode;
}

export function AlertDialog({
	open = true,
	onOpenChange,
	message,
	actions,
}: AlertDialogProps) {
	const [internalOpen, setInternalOpen] = useState(open);
	const handleOpenChange = (value: boolean) => {
		setInternalOpen(value);
		onOpenChange?.(value);
	};

	return (
		<Dialog open={internalOpen} onOpenChange={handleOpenChange}>
			<DialogTrigger asChild>
				<Button className="hidden">열기</Button>
			</DialogTrigger>
			<DialogContent className="w-full max-w-sm bg-white rounded-xl shadow-xl p-6">
				<div className="space-y-4 text-center">
					<p className="text-base font-medium">{message}</p>
					<div className="flex flex-col gap-2">{actions}</div>
				</div>
			</DialogContent>
		</Dialog>
	);
}
