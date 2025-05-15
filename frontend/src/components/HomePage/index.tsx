// pages/vote.tsx
import { GetServerSideProps } from 'next';
import { dehydrate, QueryClient, useQuery } from '@tanstack/react-query';
import { getElectionList } from '@/lib/api/election';
import type { ListResponseInfinity } from '@/lib/types/common';
import type { ElectionItem } from '@/lib/types/election';
import { Separator } from '@/components/ui/separator';

export const getServerSideProps: GetServerSideProps = async () => {
	const queryClient = new QueryClient();

	await queryClient.prefetchQuery({
		queryKey: ['electionList'],
		queryFn: () => getElectionList(10),
	});

	return {
		// props: {},
		props: {
			dehydratedState: dehydrate(queryClient),
		},
	};
};

export default function ElectionList() {
	// const { data, isLoading, isError } = useQuery<
	// 	ListResponseInfinity<ElectionItem>,
	// 	Error
	// >({
	// 	queryKey: ['electionList'],
	// 	queryFn: () => getElectionList(10),
	// 	retry: 3,
	// 	refetchOnWindowFocus: false,
	// });

	// if (isLoading) return <div>로딩 중...</div>;
	// if (isError) return <div>에러 발생!</div>;

	return (
		<div>
			{/* <h1>선거 리스트</h1> */}
			{/* <ul>
				{data?.content?.map((item) => (
					<li key={item.id}>
						<h3>{item.title}</h3>
						<p>{item.description}</p>
						<p>
							기간: {item.startDate} ~ {item.endDate}
						</p>
					</li>
				))}
			</ul> */}
			<div className="flex h-14 flex-col py-10 px-15 gap-4">
				<h1 className="text-2xl px-2">Korea Elections Project 서비스 소개</h1>
				<Separator className="my-4" />
				<div className="px-2">
					정확한 여론, 신뢰받는 선거
					<br />
					이 서비스는 선거의 투명성과 신뢰도를 높이기 위한 여론조사 기반
					플랫폼입니다.
					<br />
					본인인증을 통해 발급된 CI(Connecting Information) 값을 활용하여,
					<br />
					하나의 개인이 중복 없이 단 한 번만 참여할 수 있도록 설계되었습니다.
					<br />
					이를 통해 익명성과 개인정보 보호는 유지하면서도,
					<br />
					투표 데이터의 고유성과 정합성을 보장합니다.
					<br />
					개별 참여자의 투표 내역은 안전하게 집계되며,
					<br />
					신뢰할 수 있는 통계 기반 분석으로 선거 결과에 대한 국민의 신뢰를
					높이고자 합니다.
				</div>
			</div>
		</div>
	);
}
