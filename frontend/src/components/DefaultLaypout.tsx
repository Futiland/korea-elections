import { ReactNode } from 'react';
import Header from './Header';
// import BottomMenuBar from './BottomMenubar';
import { useRouter } from 'next/router';

interface LayoutProps {
	children: ReactNode;
}

export default function DefaultLaypout({ children }: LayoutProps) {
	const router = useRouter();
	const isLoginPage = router.pathname === '/login';
	const isSignUpPage = router.pathname === '/signup';

	const visibleHeader = !isLoginPage;
	// const visibleBottomMenuBar = !isLoginPage && !isSignUpPage;

	return (
		<>
			{visibleHeader && <Header />}
			<main
				className="bg-slate-50"
				style={{
					minHeight: `${
						visibleHeader ? 'calc(100vh - var(--header-height))' : 0
					}`,
					// paddingBottom: `${
					// 	visibleBottomMenuBar ? 'var(--bottom-nav-height)' : 0
					// }`,
				}}
			>
				{children}
			</main>

			{/* {visibleBottomMenuBar && <BottomMenuBar />} */}
		</>
	);
}
