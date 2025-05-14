// pages/vote.tsx
import { GetServerSideProps } from 'next';
import { dehydrate, QueryClient, useQuery } from '@tanstack/react-query';
import { getElectionList } from '@/lib/api/election';
import type { ListResponseInfinity } from '@/lib/types/common';
import type { ElectionItem } from '@/lib/types/election';

export const getServerSideProps: GetServerSideProps = async () => {
	const queryClient = new QueryClient();

	await queryClient.prefetchQuery({
		queryKey: ['electionList'],
		queryFn: () => getElectionList(10),
	});

	return {
		props: {
			dehydratedState: dehydrate(queryClient),
		},
	};
};

export default function ElectionList() {
	const { data, isLoading, isError } = useQuery<
		ListResponseInfinity<ElectionItem>,
		Error
	>({
		queryKey: ['electionList'],
		queryFn: () => getElectionList(10),
		retry: 3,
		refetchOnWindowFocus: false,
	});

	if (isLoading) return <div>로딩 중...</div>;
	if (isError) return <div>에러 발생!</div>;

	return (
		<div>
			<h1>선거 리스트</h1>
			<ul>
				{data?.content.map((item) => (
					<li key={item.id}>
						<h3>{item.title}</h3>
						<p>{item.description}</p>
						<p>
							기간: {item.startDate} ~ {item.endDate}
						</p>
					</li>
				))}
			</ul>
		</div>
	);
}
