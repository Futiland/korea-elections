import { GetServerSideProps } from 'next';
import Head from 'next/head';
import { useEffect, useState } from 'react';
import {
	dehydrate,
	QueryClient,
	useQuery,
	useMutation,
} from '@tanstack/react-query';

import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { getElectionResult } from '@/lib/api/election';
import { useAuthToken } from '@/hooks/useAuthToken';

type resultPageProps = {
	questions: string[];
};

const ages = [
	{
		age: '20대',
		id: 20,
	},
	{
		age: '30대',
		id: 30,
	},
	{
		age: '40대',
		id: 40,
	},
	{
		age: '50대',
		id: 50,
	},
	{
		age: '60대~',
		id: 60,
	},
];

// export const getServerSideProps: GetServerSideProps = async () => {
// 	const queryClient = new QueryClient();

// 	await queryClient.prefetchQuery({
// 		queryKey: ['electionList'],
// 		queryFn: () => getCandidateList(10),
// 	});

// 	return {
// 		props: {
// 			dehydratedState: dehydrate(queryClient),
// 		},
// 	};
// };

export default function resultPage({ questions }: resultPageProps) {
	const { isLoggedIn, isReady } = useAuthToken();

	const {
		data: electionResultData,
		isFetching: isElectionResultFetching,
		isError: isElectionError,
	} = useQuery({
		queryKey: ['electionData'],
		queryFn: () => getElectionResult(),
		refetchOnWindowFocus: false,
		enabled: isLoggedIn && isReady,
	});

	console.log('electionResultData', electionResultData);

	return (
		<>
			<Head>
				<title>회원가입 | KEP</title>
			</Head>

			<div className="min-h-screen py-7 px-4 w-full max-w-lg mx-auto">
				<div className="flex items-center justify-between mb-4">
					<h1 className="text-xl font-bold">투표 결과</h1>
					<p className="tex-sm text-slate-500">
						최신 업데이트 : 2025.12.23 09:00
					</p>
				</div>

				{/* <Tabs defaultValue="40대" className="w-full">
				<TabsList className="w-full grid grid-cols-5 p-1 rounded-lg border border-slate-300 h-10 bg-white">
					{ages.map((item) => (
						<TabsTrigger
							key={item.id}
							value={item.age}
							className="text-sm data-[state=active]:bg-blue-100 data-[state=active]:shadow-sm"
						>
							{item.age}
						</TabsTrigger>
					))}
				</TabsList>
				<TabsContent value="20"></TabsContent>
			</Tabs> */}

				{/* <div className="bg-white rounded-xl p-6 shadow-sm space-y-4">
				<h2 className="text-lg font-semibold">투표 결과 요약</h2>
				<div className="space-y-3">
					{voteData.map((item, idx) => {
						const percent = (item.value / maxValue) * 100;

						return (
							<div key={idx} className="relative">
								<div className="text-sm font-medium mb-1">{item.label}</div>
								<div className="relative h-8 bg-slate-200 rounded-full overflow-hidden">
									<div
										className="bg-blue-600 h-full rounded-full transition-all"
										style={{ width: `${percent}%` }}
									/>
								
								</div>
								<div className="absolute right-0 top-1/2 -translate-y-1/2 text-sm font-semibold text-black">
									{item.value}
								</div>
							</div>
						);
					})}
				</div>
			</div> */}
			</div>
		</>
	);
}

export const getServerSideProps: GetServerSideProps = async () => {
	// 서버에서 데이터 불러오기 (예: DB나 API)
	const questions = ['후보 A vs 후보 B', '정책 1 vs 정책 2']; // 예시

	return {
		props: {
			questions,
		},
	};
};
