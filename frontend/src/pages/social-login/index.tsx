import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useMemo, useState } from 'react';
import { useMutation } from '@tanstack/react-query';

import { Spinner } from '@/components/ui/spinner';
import { socialLogin } from '@/lib/api/account';

const PROVIDERS = {
	kakao: {
		label: '카카오',
	},
} as const;

type ProviderKey = keyof typeof PROVIDERS;

export default function SocialLoginPage() {
	const router = useRouter();
	const { provider, redirect } = router.query;

	const [error, setError] = useState<string | null>(null);

	const SOCIAL_LOGIN_REDIRECT_KEY = 'socialLoginRedirect';

	const providerKey: ProviderKey | null = useMemo(() => {
		if (typeof provider !== 'string') return null;
		return (Object.keys(PROVIDERS) as ProviderKey[]).includes(
			provider as ProviderKey
		)
			? (provider as ProviderKey)
			: null;
	}, [provider]);

	const redirectPath = useMemo(() => {
		if (typeof redirect === 'string' && redirect.startsWith('/'))
			return redirect;
		return '/';
	}, [redirect]);

	const socialLoginMutation = useMutation({
		mutationFn: (data: { provider: string; redirectUri: string }) =>
			socialLogin(data),
		onSuccess: (data) => {
			// 백엔드가 인증 URL을 반환한 경우 (첫 번째 호출)
			if (data.data.authorizationUrl) {
				// 카카오 갔다 와도 redirect 복구하려고 저장 (돌아올 땐 URL에 redirect 없음)
				if (typeof window !== 'undefined' && redirectPath && redirectPath !== '/') {
					sessionStorage.setItem(SOCIAL_LOGIN_REDIRECT_KEY, redirectPath);
				}
				window.location.href = data.data.authorizationUrl;
				return;
			}

			setError('예상치 못한 응답입니다. 다시 시도해 주세요.');
		},
		onError: (error: any) => {
			setError(
				error?.message || '소셜 로그인에 실패했습니다. 다시 시도해 주세요.'
			);
		},
	});

	useEffect(() => {
		if (!router.isReady) return;

		// 백엔드 콜백 후 /social-login?token=... 로 들어온 경우
		const token =
			typeof router.query.token === 'string' ? router.query.token : null;
		if (token && token.length > 0) {
			localStorage.setItem('token', token);
			// URL에 redirect 없을 수 있음 → 카카오 가기 전에 저장해 둔 값 사용
			let destination = '/';
			if (typeof redirect === 'string' && redirect.startsWith('/')) {
				destination = redirect;
			} else if (typeof window !== 'undefined') {
				const saved = sessionStorage.getItem(SOCIAL_LOGIN_REDIRECT_KEY);
				if (saved && saved.startsWith('/')) {
					destination = saved;
					sessionStorage.removeItem(SOCIAL_LOGIN_REDIRECT_KEY);
				}
			}
			router.replace(destination);
			return;
		}

		if (!providerKey) {
			setError('지원하지 않는 소셜 로그인입니다. 다시 시도해 주세요.');
			return;
		}

		socialLoginMutation.mutate({
			provider: providerKey,
			redirectUri: '/social-login',
		});
	}, [router.isReady, providerKey, redirect]);

	const titleText = providerKey
		? `${PROVIDERS[providerKey].label} 로그인 중...`
		: '소셜 로그인 중...';

	return (
		<>
			<Head>
				<title>{titleText}</title>
			</Head>
			<main className="min-h-screen flex items-center justify-center bg-slate-50 px-4">
				<div className="w-full max-w-sm rounded-xl bg-white shadow-md px-6 py-8 flex flex-col items-center gap-4">
					<Spinner className="w-8 h-8 text-blue-500" />
					<div className="text-center space-y-1">
						<p className="text-sm font-semibold text-slate-900">
							{providerKey
								? `${PROVIDERS[providerKey].label}로 로그인하는 중입니다.`
								: '소셜 로그인 정보를 확인하는 중입니다.'}
						</p>
						<p className="text-xs text-slate-500">
							잠시만 기다려 주세요.
						</p>
						{error && (
							<p className="mt-2 text-xs text-red-600">{error}</p>
						)}
						{socialLoginMutation.isPending && (
							<p className="mt-2 text-xs text-slate-500">
								로그인 처리 중...
							</p>
						)}
					</div>
					<button
						type="button"
						className="mt-2 text-xs text-slate-500 underline hover:text-slate-700"
						onClick={() => router.push('/login')}
					>
						로그인 화면으로 돌아가기
					</button>
				</div>
			</main>
		</>
	);
}
