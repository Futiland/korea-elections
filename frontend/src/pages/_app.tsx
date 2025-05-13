import '@/styles/globals.css';
import type { AppProps } from 'next/app';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import DefaultLaypout from '@/components/DefaultLaypout';
import { useState } from 'react';

export default function App({ Component, pageProps }: AppProps) {
	const [queryClient] = useState(() => new QueryClient());
	return (
		<QueryClientProvider client={queryClient}>
			<DefaultLaypout>
				<Component {...pageProps} />
			</DefaultLaypout>
		</QueryClientProvider>
	);
}
