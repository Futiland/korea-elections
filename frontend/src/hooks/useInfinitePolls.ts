import { useInfiniteQuery } from '@tanstack/react-query';
import { getPublicPolls } from '@/lib/api/poll';
import type { PublicPollResponse } from '@/lib/types/poll';
import { useEffect, useRef } from 'react';

type UseInfinitePollsOptions = {
	pageSize?: number;
	/**
	 * react-query에서 사용할 queryKey.
	 * 기본값: ['publicPolls', pageSize]
	 */
	queryKey?: (string | number)[];
	/**
	 * 페이지네이션 API 호출 함수.
	 * 기본값: getPublicPolls
	 */
	fetcher?: (params: {
		size: number;
		nextCursor?: string;
		keyword?: string;
		status?: string;
		sort?: string;
	}) => Promise<PublicPollResponse>;
};

export function useInfinitePolls({
	pageSize = 10,
	queryKey,
	fetcher = getPublicPolls,
}: UseInfinitePollsOptions = {}) {
	const {
		data,
		fetchNextPage,
		hasNextPage,
		isFetchingNextPage,
		isLoading,
		isError,
	} = useInfiniteQuery({
		queryKey: queryKey ?? ['publicPolls', pageSize],
		queryFn: ({ pageParam }: { pageParam?: string }) =>
			fetcher({
				size: pageSize,
				nextCursor: pageParam,
			}),
		getNextPageParam: (lastPage: PublicPollResponse) => {
			const nextCursor = lastPage.data.nextCursor;
			return nextCursor && nextCursor !== '' ? nextCursor : undefined;
		},
		initialPageParam: undefined,
		refetchOnWindowFocus: true,
		retry: 2,
	});

	const observerTarget = useRef<HTMLDivElement>(null);

	useEffect(() => {
		const observer = new IntersectionObserver(
			(entries) => {
				if (entries[0].isIntersecting && hasNextPage && !isFetchingNextPage) {
					fetchNextPage();
				}
			},
			{ threshold: 0.1 }
		);

		const currentTarget = observerTarget.current;
		if (currentTarget) {
			observer.observe(currentTarget);
		}

		return () => {
			if (currentTarget) {
				observer.unobserve(currentTarget);
			}
		};
	}, [fetchNextPage, hasNextPage, isFetchingNextPage]);

	const polls =
		data?.pages.flatMap((page: PublicPollResponse) => page.data.content) ?? [];

	return {
		polls,
		observerTarget,
		isLoading,
		isError,
		isFetchingNextPage,
		hasNextPage,
	};
}
