// pages/vote.tsx
import { GetServerSideProps } from 'next';
import { dehydrate, QueryClient, useQuery } from '@tanstack/react-query';
import { getCandidateList } from '@/lib/api/election';
import type { ListResponseInfinity } from '@/lib/types/common';
import type { CandidateResponse } from '@/lib/types/election';
import { Separator } from '@/components/ui/separator';
import Image from 'next/image';
import { Spinner } from '../ui/spinner';

export const getServerSideProps: GetServerSideProps = async () => {
	const queryClient = new QueryClient();

	await queryClient.prefetchQuery({
		queryKey: ['electionList'],
		queryFn: () => getCandidateList(10),
	});

	return {
		// props: {},
		props: {
			dehydratedState: dehydrate(queryClient),
		},
	};
};

export default function ElectionList() {
	const { data, isFetching, isError } = useQuery<CandidateResponse, Error>({
		queryKey: ['electionList'],
		queryFn: () => getCandidateList(),
		retry: 2,
		refetchOnWindowFocus: false,
	});

	if (isFetching)
		return (
			<div className="text-center py-10 min-h-screen">
				<Spinner />
			</div>
		);
	if (isError) return <div>에러 발생!</div>;

	return (
		<>
			<div className="min-h-screen">
				<div className="flex flex-col items-center pt-5 pb-21">
					<div className="text-slate-400 text-sm mb-2 text-center">
						우리는 선거의 공정성과 <br />
						시민의 의견을 최우선으로 생각합니다.
					</div>
					<Image
						src="/img/vote.svg"
						alt="K제 21대 대통령 선거"
						width={300}
						height={150}
						className="mb-2"
					/>
				</div>

				<div className="flex items-center mb-6  max-w-lg mx-auto">
					<h1 className="text-xl font-bold">선거 리스트</h1>
					<ul>
						{data?.data?.map((item) => (
							<li key={item.id}>
								<h3>{item.name}</h3>
								<p>{item.number}</p>
								<p>{item.description}</p>
							</li>
						))}
					</ul>
				</div>
			</div>
		</>
	);
}
