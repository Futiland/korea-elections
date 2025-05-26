// hooks/useAuthToken.ts

import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';

interface UseAuthTokenOptions {
	redirectIfLoggedIn?: boolean;
	redirectTo?: string; // 기본값: "/"
}

export function useAuthToken(options: UseAuthTokenOptions = {}) {
	const { redirectIfLoggedIn = false, redirectTo = '/' } = options;

	const [token, setToken] = useState<string | null | undefined>(undefined);
	const router = useRouter();

	useEffect(() => {
		if (typeof window === 'undefined') return;

		const storedToken = localStorage.getItem('token');
		setToken(storedToken);

		if (redirectIfLoggedIn && storedToken) {
			router.replace(redirectTo);
		}
	}, [redirectIfLoggedIn, redirectTo, router]);

	const isReady = token !== undefined;
	const isLoggedIn = !!token;

	return { token, isLoggedIn, isReady };
}
