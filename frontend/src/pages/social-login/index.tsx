import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useMemo, useState } from 'react';

import { Spinner } from '@/components/ui/spinner';

const PROVIDERS = {
	kakao: {
		label: '카카오',
		// TODO: 백엔드와 상의하여 실제 카카오 로그인 엔드포인트를 확정하세요.
		// 예시: '/rest/account/v1/kakao/login'
		path: '/rest/account/v1/kakao/login',
	},
} as const;

type ProviderKey = keyof typeof PROVIDERS;

export default function SocialLoginPage() {
	const router = useRouter();
	const { provider, redirect } = router.query;

	const [error, setError] = useState<string | null>(null);

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

	useEffect(() => {
		if (!router.isReady) return;

		if (!providerKey) {
			setError('지원하지 않는 소셜 로그인입니다. 다시 시도해 주세요.');
			return;
		}

		const apiBase = process.env.NEXT_PUBLIC_API_URL;
		if (!apiBase) {
			setError(
				'API 서버 주소가 설정되지 않았습니다. 관리자에게 문의해 주세요.'
			);
			return;
		}

		const { path } = PROVIDERS[providerKey];
		const targetUrl = `${apiBase}${path}?redirect=${encodeURIComponent(
			redirectPath
		)}`;

		// 실제 소셜 로그인 엔드포인트로 이동
		window.location.href = targetUrl;
	}, [router.isReady, providerKey, redirectPath]);

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
							잠시만 기다려 주세요. 브라우저가 자동으로 인증 페이지로
							이동합니다.
						</p>
						{error && <p className="mt-2 text-xs text-red-600">{error}</p>}
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
