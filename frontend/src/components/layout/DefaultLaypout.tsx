import { ReactNode, useState } from 'react';
import Header from './Header';
// import BottomMenuBar from './BottomMenubar';
import { useRouter } from 'next/router';
import CreatePollDialog from '../CreatePoll';
import { Plus } from 'lucide-react';
import { useRequireLogin } from '@/hooks/useRequireLogin';

interface LayoutProps {
	children: ReactNode;
}

export default function DefaultLaypout({ children }: LayoutProps) {
	const [isCreatePollOpen, setIsCreatePollOpen] = useState(false);

	const router = useRouter();
	const { ensureLoggedIn } = useRequireLogin();

	const isLoginPage = router.pathname === '/login';
	const isSignUpPage = router.pathname === '/signup';
	const isOpinionPollPage = router.pathname === '/opinion-polls';
	const isMaintenanceMode = process.env.NEXT_PUBLIC_MAINTENANCE_MODE === 'true';

	const visibleHeader = !isLoginPage;
	const visibleCreatePollButton =
		!isLoginPage && !isSignUpPage && !isMaintenanceMode && !isOpinionPollPage;
	// const visibleBottomMenuBar = !isLoginPage && !isSignUpPage;

	const handleCreatePollClick = () =>
		// 로그인 여부 체크
		ensureLoggedIn({
			onSuccess: () => setIsCreatePollOpen(true),
			description: '로그인 후 모두의 투표를 생성해보세요. 😃',
		});

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
					<div className="fixed bottom-6 right-6 z-50 md:right-[calc(50%-350px+20px)]">
						<button
							type="button"
							className="w-14 h-14 rounded-full bg-blue-800 text-white shadow-lg flex items-center justify-center hover:bg-blue-700 transition-colors"
							aria-label="투표 생성 버튼"
							onClick={handleCreatePollClick}
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
