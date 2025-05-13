import { ReactNode } from 'react';
import Header from './Header';
import Footer from './Footer';

interface LayoutProps {
	children: ReactNode;
}

export default function DefaultLaypout({ children }: LayoutProps) {
	return (
		<>
			<Header />
			<main className="min-h-[calc(100vh-100px)]">{children}</main>
			<Footer />
		</>
	);
}
