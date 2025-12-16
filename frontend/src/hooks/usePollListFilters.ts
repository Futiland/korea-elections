import { useState, useEffect, useMemo } from 'react';
import { useRouter } from 'next/router';
import { filterOptions, type FilterOption } from '@/components/PollFilter';
import type { PollSort } from '@/lib/types/poll';

// URL 파라미터로부터 필터 찾기
const findFilterByParams = (sort?: string, status?: string): FilterOption => {
	const found = filterOptions.find((option) => {
		const sortMatch = !sort || option.sort === sort;
		const statusMatch = !status || option.status === status;
		return sortMatch && statusMatch;
	});

	return found || filterOptions[0]; // 기본값: 첫 번째 필터
};

interface UsePollListFiltersOptions {
	pathname: string; // URL 업데이트 시 사용할 pathname
}

export function usePollListFilters({ pathname }: UsePollListFiltersOptions) {
	const router = useRouter();
	const {
		sort: querySort,
		status: queryStatus,
		search: querySearch,
	} = router.query;

	// URL 파라미터로부터 초기 필터 결정
	const initialFilter = useMemo(() => {
		return findFilterByParams(
			querySort as PollSort | undefined,
			queryStatus as string | undefined
		);
	}, [querySort, queryStatus]);

	const initialSearchTerm = typeof querySearch === 'string' ? querySearch : '';

	const [searchTerm, setSearchTerm] = useState(initialSearchTerm);
	const [selectedFilter, setSelectedFilter] =
		useState<FilterOption>(initialFilter);

	// URL 파라미터가 변경되면 필터 업데이트
	useEffect(() => {
		const newFilter = findFilterByParams(
			querySort as PollSort | undefined,
			queryStatus as string | undefined
		);
		setSelectedFilter(newFilter);
	}, [querySort, queryStatus]);

	// URL 쿼리 파라미터와 state 동기화 (검색어)
	useEffect(() => {
		const nextSearch = typeof querySearch === 'string' ? querySearch : '';
		setSearchTerm(nextSearch);
	}, [querySearch]);

	const updateUrl = (filter: FilterOption, search: string) => {
		const query: Record<string, string> = {};
		if (filter.sort) query.sort = filter.sort;
		if (filter.status) query.status = filter.status;
		if (search) query.search = search;

		router.push(
			{
				pathname,
				query,
			},
			undefined,
			{ shallow: true }
		);
	};

	// 필터 변경 시 URL 업데이트
	const handleFilterChange = (filter: FilterOption) => {
		setSelectedFilter(filter);
		updateUrl(filter, searchTerm);
	};

	// 검색어 변경 시 URL 업데이트
	const handleSearchChange = (value: string) => {
		setSearchTerm(value);
		updateUrl(selectedFilter, value);
	};

	const sort = selectedFilter.sort;
	const status = selectedFilter.status;

	return {
		searchTerm,
		selectedFilter,
		sort: sort ?? 'LATEST',
		status: status ?? 'ALL',
		handleFilterChange,
		handleSearchChange,
	};
}
