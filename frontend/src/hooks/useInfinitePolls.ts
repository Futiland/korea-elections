import { useInfiniteQuery } from '@tanstack/react-query';
import { getPublicPolls } from '@/lib/api/poll';
import { PublicPollResponse } from '@/lib/types/poll';
import { useEffect, useRef } from 'react';

type UseInfinitePollsOptions = {
	pageSize?: number;
};

export function useInfinitePolls({
	pageSize = 10,
}: UseInfinitePollsOptions = {}) {
	const {
		data,
		fetchNextPage,
		hasNextPage,
		isFetchingNextPage,
		isLoading,
		isError,
	} = useInfiniteQuery({
		queryKey: ['publicPolls', pageSize],
		queryFn: ({ pageParam }: { pageParam?: string }) =>
			getPublicPolls(pageSize, pageParam ?? ''),
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
