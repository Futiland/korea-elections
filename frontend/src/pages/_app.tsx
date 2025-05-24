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

export default function App({ Component, pageProps }: AppProps) {
	const [queryClient] = useState(() => new QueryClient());
	return (
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
	);
}
