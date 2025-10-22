import { useState } from 'react';
import VoteCard from '@/components/VoteCard';
import VoteSearchAndFilter from '@/components/VoteSearchAndFilter';
import { FilterOption } from '@/components/VoteFilter';
import Head from 'next/head';

// 임시 데이터 - 나중에 API에서 가져올 데이터
const mockVoteData = [
	{
		id: 1,
		title: '모두의 투표 제목입니다. 투표해주세요.',
		description: '이 투표는 모두가 참여할 수 있는 예시 투표입니다.',
		status: 'progress' as const,
		endDate: '2024-07-01',
		startDate: '2024-06-01',
		voteCount: 150,
	},
	{
		id: 2,
		title: '새로운 정책에 대한 의견을 듣고 싶습니다.',
		description: '시민 여러분의 소중한 의견을 들려주세요.',
		status: 'progress' as const,
		endDate: '2024-06-30',
		startDate: '2024-06-01',
		voteCount: 89,
	},
	{
		id: 3,
		title: '종료된 투표 예시입니다.',
		description: '이미 종료된 투표의 예시입니다.',
		status: 'ended' as const,
		endDate: '2024-05-15',
		startDate: '2024-05-01',
		voteCount: 234,
	},
];

export default function EveryoneVote() {
	const [searchTerm, setSearchTerm] = useState('');
	const [selectedFilter, setSelectedFilter] = useState<FilterOption>('latest');

	// 필터링 및 검색 로직
	const filteredVotes = mockVoteData.filter((vote) => {
		const matchesSearch =
			vote.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
			vote.description.toLowerCase().includes(searchTerm.toLowerCase());

		const matchesFilter = (() => {
			switch (selectedFilter) {
				case 'latest':
					return true; // 최신순은 정렬로 처리
				case 'popular':
					return true; // 인기순은 정렬로 처리
				case 'ended':
					return vote.status === 'ended';
				default:
					return true;
			}
		})();

		return matchesSearch && matchesFilter;
	});

	// 정렬 로직
	const sortedVotes = [...filteredVotes].sort((a, b) => {
		switch (selectedFilter) {
			case 'latest':
				return b.id - a.id; // ID가 높을수록 최신
			case 'popular':
				return b.voteCount - a.voteCount; // 투표수가 높을수록 인기
			case 'ended':
				return b.id - a.id; // 종료된 투표도 최신순
			default:
				return 0;
		}
	});

	return (
		<>
			<Head>
				<title>모두의 투표</title>
			</Head>

			<div className="min-h-screen bg-slate-50">
				<div className="max-w-4xl mx-auto px-4 py-8">
					{/* 페이지 제목 */}
					<div className="mb-8">
						<h1 className="text-3xl font-bold text-slate-900 mb-2">
							모두의 투표
						</h1>
						<p className="text-slate-600">여러분의 소중한 의견을 들려주세요.</p>
					</div>

					{/* 검색 및 필터 */}
					<VoteSearchAndFilter
						searchTerm={searchTerm}
						selectedFilter={selectedFilter}
						onSearchChange={setSearchTerm}
						onFilterChange={setSelectedFilter}
						className="mb-8"
					/>

					{/* 투표 카드 리스트 */}
					<div className="space-y-6">
						{sortedVotes.length > 0 ? (
							sortedVotes.map((vote) => (
								<VoteCard key={vote.id} voteData={vote} />
							))
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
