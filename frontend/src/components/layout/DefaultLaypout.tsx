import { ReactNode, useState } from 'react';
import Header from './Header';
// import BottomMenuBar from './BottomMenubar';
import { useRouter } from 'next/router';
import CreatePollDialog from '../CreatePoll';
import { Plus } from 'lucide-react';

interface LayoutProps {
	children: ReactNode;
}

export default function DefaultLaypout({ children }: LayoutProps) {
	const [isCreatePollOpen, setIsCreatePollOpen] = useState(false);

	const router = useRouter();
	const isLoginPage = router.pathname === '/login';
	const isSignUpPage = router.pathname === '/signup';

	const visibleHeader = !isLoginPage;
	const visibleCreatePollButton = !isLoginPage && !isSignUpPage;
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
				<div className="max-w-lg mx-auto">{children}</div>
				{/* 투표 생성 플로팅 버튼 */}
				{visibleCreatePollButton && (
					<div className="fixed bottom-6 right-6 z-50 lg:right-[calc(50%-12rem)]">
						<button
							type="button"
							className="w-14 h-14 rounded-full bg-blue-800 text-white shadow-lg flex items-center justify-center hover:bg-blue-700 transition-colors"
							aria-label="투표 생성 버튼"
							onClick={() => setIsCreatePollOpen(true)}
						>
							<Plus className="w-8 h-8 font-bold" />
						</button>
					</div>
				)}
			</main>

			<CreatePollDialog
				isOpen={isCreatePollOpen}
				setIsOpen={setIsCreatePollOpen}
			/>

			{/* {visibleBottomMenuBar && <BottomMenuBar />} */}
		</>
	);
}
