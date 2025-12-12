import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';

import PaginatedPollListView from '@/components/My/MyPollListView';
import { useAuthToken } from '@/hooks/useAuthToken';
import { getMyPolls } from '@/lib/api/poll';
import type { MyPollResponse } from '@/lib/types/poll';

const PAGE_SIZE = 10;

export default function MyCreatedPollsPage() {
	const router = useRouter();
	const { isLoggedIn, isReady } = useAuthToken();

	useEffect(() => {
		if (isReady && !isLoggedIn) {
			router.replace({
				pathname: '/login',
				query: { redirect: router.asPath },
			});
		}
	}, [isLoggedIn, isReady, router]);

	const page = useMemo(() => {
		const raw = Number(router.query.page);
		return Number.isFinite(raw) && raw > 0 ? raw : 1;
	}, [router.query.page]);

	const { data, isLoading, isError, isFetching } = useQuery<MyPollResponse>({
		queryKey: ['myPolls', page, PAGE_SIZE],
		queryFn: () => getMyPolls({ size: PAGE_SIZE, page }),
		enabled: isLoggedIn && isReady,
		refetchOnWindowFocus: false,
		retry: 2,
	});

	const handlePageChange = (nextPage: number) => {
		router.replace(
			{
				pathname: '/mypage/my-polls',
				query: { page: nextPage },
			},
			undefined,
			{ shallow: true }
		);
	};

	const list = data?.data.content ?? [];
	const totalPages = data?.data.totalPages ?? 1;

	return (
		<>
			<Head>
				<title>내가 만든 모두의 투표 | KEP</title>
			</Head>
			<PaginatedPollListView
				title="내가 만든 모두의 투표"
				description="내가 만든 모두의 투표 전체 목록을 볼 수 있어요."
				items={list}
				page={page}
				totalPages={totalPages}
				isLoading={isLoading || isFetching || !isReady}
				isError={isError}
				onPageChange={handlePageChange}
			/>
		</>
	);
}
