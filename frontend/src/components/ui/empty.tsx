import * as React from 'react';
import { cn } from '@/lib/utils';

interface EmptyProps extends React.ComponentProps<'div'> {
	icon?: React.ReactNode;
	title?: string;
	description?: string;
	action?: React.ReactNode;
}

function Empty({
	className,
	icon,
	title,
	description,
	action,
	...props
}: EmptyProps) {
	return (
		<div
			data-slot="empty"
			className={cn(
				'flex flex-col items-center justify-center gap-4 py-12 px-4 text-center',
				className
			)}
			{...props}
		>
			{icon && (
				<div className="flex size-12 items-center justify-center rounded-full bg-slate-100 text-slate-400">
					{icon}
				</div>
			)}
			{title && (
				<h3 className="text-lg font-semibold text-slate-900">{title}</h3>
			)}
			{description && (
				<p className="max-w-sm text-sm text-slate-600">{description}</p>
			)}
			{action && <div className="mt-2">{action}</div>}
		</div>
	);
}

export { Empty };
