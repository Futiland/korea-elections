import { useCallback } from 'react';
import { useRouter } from 'next/router';
import { useAuthToken } from './useAuthToken';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';
import { Button } from '@/components/ui/button';

interface EnsureLoggedInOptions {
	onSuccess?: () => void;
	message?: string;
	description?: string;
}

export function useRequireLogin() {
	const router = useRouter();
	const { isLoggedIn, isReady } = useAuthToken();
	const { showDialog, hideDialog } = useAlertDialog();

	const promptLogin = useCallback(
		(message?: string, description?: string) => {
			showDialog({
				message: message ?? '로그인이 필요합니다',
				discription: description ?? '',
				actions: (
					<div className="flex w-full gap-2">
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
		},
		[hideDialog, router, showDialog]
	);

	const ensureLoggedIn = useCallback(
		({ onSuccess, message, description }: EnsureLoggedInOptions = {}) => {
			if (!isReady) return;
			if (!isLoggedIn) {
				promptLogin(message, description);
				return;
			}

			onSuccess?.();
		},
		[isLoggedIn, isReady, promptLogin]
	);

	return {
		isLoggedIn,
		isReady,
		ensureLoggedIn,
	};
}
