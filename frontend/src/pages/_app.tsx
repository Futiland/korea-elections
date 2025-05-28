import '@/styles/globals.css';
import type { AppProps } from 'next/app';
import {
	QueryClient,
	QueryClientProvider,
	HydrationBoundary,
} from '@tanstack/react-query';
import { QueryErrorResetBoundary } from '@tanstack/react-query';
import DefaultLaypout from '@/components/DefaultLaypout';
import { useState } from 'react';
import { ErrorBoundary } from 'react-error-boundary';
import { ErrorFallback } from '@/components/ErrorBoundary';
import { Toaster } from '@/components/ui/sonner';
import { AlertDialogProvider } from '@/components/providers/AlertDialogProvider';
import Head from 'next/head';

export default function App({ Component, pageProps }: AppProps) {
	const [queryClient] = useState(() => new QueryClient());
	return (
		<>
			<Head>
				<title>제 21대 대통령 선거 투표</title>
				<meta property="og:type" content="website" />
				<meta property="og:site_name" content="KEP" />
				<meta
					property="og:title"
					content="KEP | 안전한 선거 문화 확산을 위해"
				/>
				<meta
					property="og:description"
					content="KEP는 안전한 선거 문화 확산을 위해 만들어진 시민 중심 프로젝트입니다."
				/>
				<meta property="og:image" content="img/meta-image.png" />
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
