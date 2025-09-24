import * as React from 'react';
import * as DialogPrimitive from '@radix-ui/react-dialog';
import { cn } from '@/lib/utils';
import { X } from 'lucide-react';

interface DialogContentProps
	extends React.ComponentPropsWithoutRef<typeof DialogPrimitive.Content> {
	withBackdrop?: boolean;
	closeOnOverlayClick?: boolean;
}

const Dialog = DialogPrimitive.Root;
const DialogTrigger = DialogPrimitive.Trigger;
const DialogPortal = DialogPrimitive.Portal;

const DialogOverlay = React.forwardRef<
	React.ElementRef<typeof DialogPrimitive.Overlay>,
	React.ComponentPropsWithoutRef<typeof DialogPrimitive.Overlay>
>(({ className, ...props }, ref) => (
	<DialogPrimitive.Overlay
		ref={ref}
		className={cn(
			'fixed inset-0 z-50 bg-black/50 transition-opacity animate-in fade-in',
			className
		)}
		{...props}
	/>
));
DialogOverlay.displayName = DialogPrimitive.Overlay.displayName;

const DialogContent = React.forwardRef<
	React.ElementRef<typeof DialogPrimitive.Content>,
	DialogContentProps
>(
	(
		{
			className,
			children,
			withBackdrop = true,
			closeOnOverlayClick = true,
			...props
		},
		ref
	) => (
		<DialogPortal>
			{/* 조건에 따라 백드롭 출력 */}
			{/* 백드롭 클릭 막기 */}
			{withBackdrop && (
				<DialogOverlay
					onPointerDown={(e) => {
						if (!closeOnOverlayClick) {
							e.stopPropagation(); // 클릭 전파 차단 → Dialog 닫힘 방지
						}
					}}
				/>
			)}
			<DialogPrimitive.Content
				ref={ref}
				onInteractOutside={(e) => {
					if (!closeOnOverlayClick) {
						e.preventDefault(); // 외부 클릭으로 인한 닫힘 방지
					}
				}}
				className={cn(
					'fixed z-50 grid w-full max-w-lg translate-x-[-50%] translate-y-[-50%] gap-4 border bg-white p-6 shadow-lg duration-200 rounded-md sm:rounded-lg top-[50%] left-[50%]',
					className
				)}
				{...props}
			>
				{/* Radix a11y: Provide a default (visually hidden) Title and Description
				to avoid accessibility warnings when consumers don't render them. */}
				<DialogPrimitive.Title className="sr-only">
					Dialog
				</DialogPrimitive.Title>
				<DialogPrimitive.Description className="sr-only">
					Dialog content
				</DialogPrimitive.Description>
				{children}

				{/* ❌ 기본 닫기 버튼 intentionally 제거됨 */}
			</DialogPrimitive.Content>
		</DialogPortal>
	)
);
DialogContent.displayName = DialogPrimitive.Content.displayName;

const DialogHeader = ({
	className,
	...props
}: React.HTMLAttributes<HTMLDivElement>) => (
	<div
		className={cn(
			'flex flex-col space-y-1.5 text-center sm:text-left',
			className
		)}
		{...props}
	/>
);
DialogHeader.displayName = 'DialogHeader';

const DialogFooter = ({
	className,
	...props
}: React.HTMLAttributes<HTMLDivElement>) => (
	<div
		className={cn(
			'flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2',
			className
		)}
		{...props}
	/>
);
DialogFooter.displayName = 'DialogFooter';

export { Dialog, DialogTrigger, DialogContent, DialogHeader, DialogFooter };
