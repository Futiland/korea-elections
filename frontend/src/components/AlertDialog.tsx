import {
	Dialog,
	DialogContent,
	DialogTrigger,
} from '@/components/CustomDialog';
import { Button } from '@/components/ui/button';
import { useState, ReactNode } from 'react';

interface AlertDialogProps {
	open?: boolean;
	onOpenChange?: (open: boolean) => void;
	message?: string;
	actions?: ReactNode;
	showBackdrop?: boolean; // Optional prop to control backdrop visibility
	closeOnOverlayClick?: boolean; // Optional prop to control closing on overlay click
}

export function AlertDialog({
	open = true,
	onOpenChange,
	message,
	actions,
	showBackdrop = true, // Default to true for backdrop visibility
	closeOnOverlayClick = true, // Default to true for closing on overlay click
}: AlertDialogProps) {
	const [internalOpen, setInternalOpen] = useState(open);
	const handleOpenChange = (value: boolean) => {
		setInternalOpen(value);
		onOpenChange?.(value);
	};

	return (
		<Dialog open={internalOpen} onOpenChange={handleOpenChange}>
			<DialogContent
				withBackdrop={showBackdrop}
				closeOnOverlayClick={closeOnOverlayClick}
				className="w-[calc(100%-40px)] max-w-sm bg-white rounded-xl shadow-xl p-6"
			>
				<div className="space-y-4">
					<p className="text-base font-medium">{message}</p>
					<div className="flex flex-col gap-2">{actions}</div>
				</div>
			</DialogContent>
		</Dialog>
	);
}
