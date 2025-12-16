type ApiMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';

interface ApiFetchOptions {
	path: string;
	method?: ApiMethod;
	params?: Record<string, any>;
	body?: any;
	headers?: HeadersInit;
}

const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

export async function apiFetch<T>(options: ApiFetchOptions): Promise<T> {
	const { path, method = 'GET', params, body, headers } = options;

	const queryString = params
		? '?' +
		  Object.entries(params)
				.map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
				.join('&')
		: '';

	const url = `${BASE_URL}${path}${queryString}`;

	// ✅ 토큰 불러오기
	let token: string | null = null;
	if (typeof window !== 'undefined') {
		token = localStorage.getItem('token'); // 키 이름은 사용 중인 값으로 조정 가능
	}

	const customHeaders: Record<string, string> = {
		...(token
			? { Authorization: `Bearer ${token}` }
			: { Authorization: 'None' }),
		...(headers as Record<string, string>),
	};

	if (body && method !== 'GET' && method !== 'DELETE') {
		customHeaders['Content-Type'] = 'application/json';
	}

	const res = await fetch(url, {
		method,
		// credentials: 'include',
		headers: customHeaders,
		body: body ? JSON.stringify(body) : undefined,
	});

	// 토큰 만료처리
	if (res.status === 401) {
		if (typeof window !== 'undefined') {
			localStorage.removeItem('token');
			window.location.href = '/login'; //토큰이 없으면 로그인 페이지로..
		}
		throw new Error('인증이 만료되었습니다. 다시 로그인 해주세요.');
	}

	if (!res.ok) {
		const errData = await res.json().catch(() => ({}));
		throw new Error(errData.message || 'API Error');
	}

	return res.json();
}

export const apiGet = <T>(path: string, params?: Record<string, any>) => {
	return apiFetch<T>({ path, method: 'GET', params });
};

export const apiPost = <T>(path: string, body?: any) => {
	return apiFetch<T>({ path, method: 'POST', body });
};

export const apiPut = <T>(path: string, body?: any) => {
	return apiFetch<T>({ path, method: 'PUT', body });
};

export const apiPatch = <T>(path: string, body?: any) => {
	return apiFetch<T>({ path, method: 'PATCH', body });
};

export const apiDelete = <T>(path: string, body?: any) => {
	return apiFetch<T>({ path, method: 'DELETE', body });
};
