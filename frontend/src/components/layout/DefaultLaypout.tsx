import { ReactNode, useState } from 'react';
import Header from './Header';
// import BottomMenuBar from './BottomMenubar';
import { useRouter } from 'next/router';
import CreateVoteDialog from '../CreateVote';
import { Plus } from 'lucide-react';

interface LayoutProps {
	children: ReactNode;
}

export default function DefaultLaypout({ children }: LayoutProps) {
	const [isCreateVoteOpen, setIsCreateVoteOpen] = useState(false);

	const router = useRouter();
	const isLoginPage = router.pathname === '/login';
	const isSignUpPage = router.pathname === '/signup';

	const visibleHeader = !isLoginPage;
	const visibleCreateVoteButton = !isLoginPage && !isSignUpPage;
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
				{visibleCreateVoteButton && (
					<div className="fixed bottom-6 right-6 z-50 lg:right-[calc(50%-12rem)]">
						<button
							type="button"
							className="w-14 h-14 rounded-full bg-blue-900 text-white shadow-lg flex items-center justify-center hover:bg-blue-800 transition-colors"
							aria-label="투표 생성 버튼"
							onClick={() => setIsCreateVoteOpen(true)}
						>
							<Plus className="w-8 h-8 font-bold" />
						</button>
					</div>
				)}
			</main>

			<CreateVoteDialog
				isOpen={isCreateVoteOpen}
				setIsOpen={setIsCreateVoteOpen}
			/>

			{/* {visibleBottomMenuBar && <BottomMenuBar />} */}
		</>
	);
}
