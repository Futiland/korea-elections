export async function apiFetch<T>(
	input: RequestInfo,
	init?: RequestInit
): Promise<T> {
	const res = await fetch(input, {
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json',
			...(init?.headers || {}),
		},
		...init,
	});

	if (!res.ok) {
		const errData = await res.json();
		throw new Error(errData.message || 'API Error');
	}

	return res.json();
}
