import { ReactNode } from 'react';
import Header from './Header';
import Footer from './Footer';
import BottomMenuBar from './BottomMenubar';
import { useRouter } from 'next/router';

interface LayoutProps {
	children: ReactNode;
}

export default function DefaultLaypout({ children }: LayoutProps) {
	const router = useRouter();
	const isLoginPage = router.pathname === '/login';
	const isSignUpPage = router.pathname === '/signup';

	return (
		<>
			{!isLoginPage && <Header />}
			<main className="bg-slate-100">{children}</main>
			{/* <Footer /> */}
			{!(isLoginPage || isSignUpPage) && <BottomMenuBar />}
		</>
	);
}
