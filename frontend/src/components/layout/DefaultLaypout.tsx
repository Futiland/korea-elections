import { ReactNode, useState } from 'react';
import Header from './Header';
// import BottomMenuBar from './BottomMenubar';
import { useRouter } from 'next/router';
import CreatePollDialog from '../CreatePoll';
import { Plus } from 'lucide-react';
import { useAuthToken } from '@/hooks/useAuthToken';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';
import { Button } from '@/components/ui/button';

interface LayoutProps {
	children: ReactNode;
}

export default function DefaultLaypout({ children }: LayoutProps) {
	const [isCreatePollOpen, setIsCreatePollOpen] = useState(false);

	const router = useRouter();
	const { isLoggedIn, isReady } = useAuthToken();
	const { showDialog, hideDialog } = useAlertDialog();

	const isLoginPage = router.pathname === '/login';
	const isSignUpPage = router.pathname === '/signup';

	const visibleHeader = !isLoginPage;
	const visibleCreatePollButton = !isLoginPage && !isSignUpPage;
	// const visibleBottomMenuBar = !isLoginPage && !isSignUpPage;

	const handleCreatePollClick = () => {
		if (!isReady) return; // 로그인 상태 확인 중

		if (!isLoggedIn) {
			showDialog({
				message: '로그인이 필요합니다',
				discription: '모두의 투표를 생성하려면 로그인이 필요합니다.',
				actions: (
					<div className="flex gap-2 w-full">
						<Button
							className="flex-1 h-10 bg-slate-200 text-slate-900 hover:bg-slate-200"
							onClick={() => hideDialog()}
						>
							취소
						</Button>
						<Button
							className="flex-1 h-10 bg-blue-800 text-white hover:bg-blue-700"
							onClick={() => {
								hideDialog();
								router.push({
									pathname: '/login',
									query: { redirect: router.asPath },
								});
							}}
						>
							로그인하기
						</Button>
					</div>
				),
			});
			return;
		}

		setIsCreatePollOpen(true);
	};

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
