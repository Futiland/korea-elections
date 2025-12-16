import Head from 'next/head';
import { useState } from 'react';
import PollCard from '@/components/PollCard';
import { filterOptions, type FilterOption } from '@/components/PollFilter';
import { Spinner } from '@/components/ui/spinner';
import { useInfinitePolls } from '@/hooks/useInfinitePolls';
import { getOpinionPolls } from '@/lib/api/poll';
import PollSearchAndFilter from '@/components/PollSearchAndFilter';

const PAGE_SIZE = 10;

export default function OpinionPoll() {
	const [searchTerm, setSearchTerm] = useState('');
	const [selectedFilter, setSelectedFilter] = useState<FilterOption>(
		filterOptions[0]
	);

	const sort = selectedFilter.sort;
	const status = selectedFilter.status;

	const {
		polls,
		observerTarget,
		isLoading,
		isError,
		isFetchingNextPage,
		hasNextPage,
	} = useInfinitePolls({
		pageSize: PAGE_SIZE,
		queryKey: [
			'opinionPolls',
			PAGE_SIZE,
			searchTerm,
			sort ?? 'LATEST',
			status ?? 'ALL',
		],
		fetcher: (size, nextCursor) =>
			getOpinionPolls(
				size,
				nextCursor,
				searchTerm || undefined,
				status ?? 'ALL',
				sort ?? 'LATEST'
			),
	});

	return (
		<>
			<Head>
				<title>모두의 투표_여론조사</title>
				<meta
					name="description"
					content="쉽게 만들고, 바로 공유하고, 함께 참여하는 투표 플랫폼"
				/>
				<meta property="og:title" content="모두의 투표" />
				<meta
					property="og:description"
					content="쉽게 만들고, 바로 공유하고, 함께 참여하는 투표 플랫폼"
				/>
				<meta property="og:image" content="/img/everyone-poll.png" />
				<meta
					property="og:url"
					content={`${process.env.NEXT_PUBLIC_BASE_URL}/opinion-poll`}
				/>
				<meta property="og:locale" content="ko_KR" />
				<meta property="og:type" content="website" />
				<meta property="og:site_name" content="KEP" />
				<meta property="og:image:width" content="1200" />
			</Head>
			<div className="min-h-screen bg-slate-50">
				<div className="max-w-4xl mx-auto px-4 py-8">
					{/* 페이지 제목 */}
					<div className="mb-8">
						<h1 className="text-3xl font-bold text-slate-900 mb-2">
							여론조사 투표
						</h1>
						<p className="text-slate-600">여러분의 소중한 의견을 들려주세요.</p>
					</div>

					{/* 검색 및 필터 */}
					{polls.length > 0 && (
						<PollSearchAndFilter
							searchTerm={searchTerm}
							selectedFilter={selectedFilter}
							onSearchChange={setSearchTerm}
							onFilterChange={setSelectedFilter}
							className="mb-8"
							isFilterVisible={false}
						/>
					)}

					{/* 투표 카드 리스트 */}
					<div className="space-y-6">
						{isLoading ? (
							<div className="flex justify-center py-12">
								<Spinner className="w-10 h-10 text-blue-500" />
							</div>
						) : isError ? (
							<div className="text-center py-12">
								<p className="text-slate-500 text-lg">
									데이터를 불러오는 중 오류가 발생했습니다.
								</p>
							</div>
						) : polls.length > 0 ? (
							<>
								{polls.map((poll) => (
									<PollCard key={poll.id} pollData={poll} />
								))}
								{/* 무한 스크롤 감지용 요소 */}
								<div ref={observerTarget} className="h-10 flex justify-center">
									{isFetchingNextPage && <Spinner />}
									{!hasNextPage && polls.length > 0 && (
										<p className="text-slate-500 text-sm py-4">
											모든 투표를 불러왔습니다.
										</p>
									)}
								</div>
							</>
						) : (
							<div className="text-center py-12">
								<p className="text-slate-500 text-lg">
									{searchTerm
										? '검색 결과가 없습니다.'
										: '등록된 투표가 없습니다.'}
								</p>
							</div>
						)}
					</div>
				</div>
			</div>
		</>
	);
}
