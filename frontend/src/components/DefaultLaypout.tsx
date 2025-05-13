import { ReactNode } from 'react';

interface LayoutProps {
	children: ReactNode;
}

export default function DefaultLaypout({ children }: LayoutProps) {
	return (
		<div>
			<main>{children}</main>
		</div>
	);
}
