import '@/styles/globals.css';
import type { AppProps } from 'next/app';
import {
	QueryClient,
	QueryClientProvider,
	HydrationBoundary,
} from '@tanstack/react-query';
import { QueryErrorResetBoundary } from '@tanstack/react-query';
import DefaultLaypout from '@/components/layout/DefaultLaypout';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { ErrorBoundary } from 'react-error-boundary';
import { ErrorFallback } from '@/components/ErrorBoundary';
import { Toaster } from '@/components/ui/sonner';
import { AlertDialogProvider } from '@/components/providers/AlertDialogProvider';
import Head from 'next/head';
import MaintenancePage from './maintenance';

export default function App({ Component, pageProps }: AppProps) {
	const [queryClient] = useState(() => new QueryClient());
	const router = useRouter();
	const isMaintenanceMode = process.env.NEXT_PUBLIC_MAINTENANCE_MODE === 'true';

	useEffect(() => {
		if (
			isMaintenanceMode &&
			router.isReady &&
			router.pathname !== '/maintenance'
		) {
			router.replace('/maintenance');
		}
	}, [isMaintenanceMode, router.isReady, router.pathname, router]);

	// 점검 모드가 활성화되어 있으면 점검 페이지만 표시
	if (isMaintenanceMode && router.pathname !== '/maintenance') {
		return <MaintenancePage />;
	}

	return (
		<>
			<Head>
				{/* 공통 메타 태그만 유지 (각 페이지에서 덮어쓸 수 있음) */}
				<meta property="og:type" content="website" />
				<meta property="og:site_name" content="모두의 투표" />
				<meta property="og:locale" content="ko_KR" />
				{/* <meta property="og:title" content="모두의 투표" />
				<meta
					property="og:description"
					content="KEP는 본인 인증된 시민만 참여하는 여론조사 플랫폼입니다."
				/>
				<meta property="og:image" content="/img/everyone-polls.png" />
				<meta property="og:image:width" content="1200" />
				<meta property="og:image:height" content="630" />
				<meta property="og:url" content={process.env.NEXT_PUBLIC_BASE_URL} /> */}

				<link rel="icon" href="/favicon.ico" />
			</Head>

			<QueryClientProvider client={queryClient}>
				<Toaster position="top-center" />
				<AlertDialogProvider>
					<QueryErrorResetBoundary>
						{({ reset }) => (
							<ErrorBoundary
								onReset={reset}
								fallbackRender={(props) => <ErrorFallback {...props} />}
							>
								<DefaultLaypout>
									<HydrationBoundary state={pageProps.dehydratedState}>
										<Component {...pageProps} />
									</HydrationBoundary>
								</DefaultLaypout>
							</ErrorBoundary>
						)}
					</QueryErrorResetBoundary>
				</AlertDialogProvider>
			</QueryClientProvider>
		</>
	);
}
