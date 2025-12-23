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
				<title>모두의 투표</title>
				<meta property="og:type" content="website" />
				<meta property="og:site_name" content="KEP" />
				<meta property="og:title" content="모두의 투표" />
				<meta
					property="og:description"
					content="KEP는 간단한 본인 인증을 통해 투명하고 신뢰할 수 있는 데이터를 확인할 수
						있는 시민 참여형 서비스입니다."
				/>
				<meta property="og:image" content="/img/everyone-polls.png" />
				<meta property="og:image:width" content="1200" />
				<meta property="og:image:height" content="630" />
				<meta property="og:url" content={process.env.NEXT_PUBLIC_BASE_URL} />
				<meta property="og:locale" content="ko_KR"></meta>
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
