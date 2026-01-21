import { cn } from '@/lib/utils';

interface DividerProps {
	orientation?: 'horizontal' | 'vertical';
	thickness?: string; // "1px" | "2px" | "0.5rem" 등
	color?: string; // "bg-border" | "#222" 등
	className?: string;
}

export function Divider({
	orientation = 'horizontal',
	thickness = '1px',
	color = 'bg-border', // Tailwind color or custom style
	className,
}: DividerProps) {
	const isVertical = orientation === 'vertical';

	return (
		<div
			className={cn(isVertical ? 'h-full' : 'w-full', className)}
			style={{
				backgroundColor: color.startsWith('#') ? color : undefined,
				// Tailwind 색상을 사용할 때는 className으로 적용됨
				width: isVertical ? thickness : '100%',
				height: isVertical ? '100%' : thickness,
			}}
		/>
	);
}
