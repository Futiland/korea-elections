import * as React from 'react';
import { cn } from '@/lib/utils';
import { Button } from './ui/button';
import Link from 'next/link';

interface EmptyProps extends React.ComponentProps<'div'> {
	icon?: React.ReactNode;
	title?: string;
	description?: string;
	action?: React.ReactNode;
	buttonUrl?: string;
	buttonText?: string;
}

function Empty({
	className,
	icon,
	title,
	description,
	action,
	buttonUrl,
	buttonText,
	...props
}: EmptyProps) {
	return (
		<div
			data-slot="empty"
			className={cn(
				'flex flex-col items-center justify-center gap-3 py-20 px-4 text-center',
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
				<h3 className="text-lg font-semibold text-slate-600">{title}</h3>
			)}
			{description && (
				<p className="max-w-sm text-sm text-slate-600">{description}</p>
			)}
			{action && <div className="mt-2">{action}</div>}
			{buttonUrl && (
				<Button
					asChild
					variant="outline"
					className="bg-blue-50 text-blue-500 hover:bg-blue-700 hover:text-white rounded-full border-blue-500"
				>
					<Link href={buttonUrl}>{buttonText ?? '투표하러가기'}</Link>
				</Button>
			)}
		</div>
	);
}

export { Empty };
