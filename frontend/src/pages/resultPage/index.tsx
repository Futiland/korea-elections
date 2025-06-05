import { GetServerSideProps } from 'next';
import Head from 'next/head';
import {
	dehydrate,
	QueryClient,
	useQuery,
	useMutation,
} from '@tanstack/react-query';
import { useRouter } from 'next/router';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
	getElectionResult,
	getMyVotedCandidate,
	getElectionResultAges,
} from '@/lib/api/election';
import { useAuthToken } from '@/hooks/useAuthToken';
import { Button } from '@/components/ui/button';
import { AlertDialog } from '@/components/AlertDialog';
import Image from 'next/image';
import { Spinner } from '@/components/ui/spinner';
import { formatDate } from '@/lib/date';
import AgesChart from './AgesChart';

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

// 토큰 처리 때문에 ssr로 처리하지 않음
// export const getServerSideProps: GetServerSideProps = async () => {
// 	const queryClient = new QueryClient();

// 	await queryClient.prefetchQuery({
// 		queryKey: ['electionResultData'],
// 		queryFn: () => getElectionResult(),
// 	});

// 	return {
// 		props: {
// 			dehydratedState: dehydrate(queryClient),
// 		},
// 	};
// };

export default function resultPage() {
	const router = useRouter();
	const { isLoggedIn, isReady } = useAuthToken();

	const {
		data: electionResultData,
		isFetching: isElectionResultFetching,
		isError: isElectionError,
	} = useQuery({
		queryKey: ['electionResultData'],
		queryFn: () => getElectionResult(),
		refetchOnWindowFocus: false,
		enabled: isLoggedIn && isReady,
	});

	const {
		data: electionResultAgesData,
		isFetching: isElectionResultAgesFetching,
		isError: isElectionAgesError,
	} = useQuery({
		queryKey: ['electionResultAgesData'],
		queryFn: () => getElectionResultAges(),
		refetchOnWindowFocus: false,
		enabled: isLoggedIn && isReady,
	});

	const {
		data: myVotedCandidate,
		isFetching: isMyVotedCandidateFetching,
		isError: isMyVotedCandidateError,
	} = useQuery({
		queryKey: ['myVotedCandidate'],
		queryFn: () => getMyVotedCandidate(),
		retry: 2,
		refetchOnWindowFocus: false,
		enabled: isLoggedIn && isReady, // 토큰이 있을 때만 쿼리 실행
	});

	const myVotedCandidateId = myVotedCandidate?.data
		? 'selectedCandidateId' in myVotedCandidate.data
			? (myVotedCandidate.data as { selectedCandidateId: number })
					.selectedCandidateId
			: null
		: null;

	if (!isReady || isElectionResultFetching || isMyVotedCandidateFetching) {
		return (
			<div className="min-h-screen flex items-center justify-center">
				<Spinner />
			</div>
		);
	}

	if (
		(isReady && !isLoggedIn) ||
		myVotedCandidateId === null ||
		process.env.NEXT_PUBLIC_ENV === 'production'
	) {
		return (
			<div className="flex items-center min-h-screen">
				<div className="w-full h-auto sm:w-[472px]">
					<Image
						src="/img/result-page-bg.png"
						alt="background"
						fill
						className="object-contain pt-5"
						priority
					/>
				</div>
				{process.env.NEXT_PUBLIC_ENV === 'production' && (
					<AlertDialog
						showBackdrop={false}
						closeOnOverlayClick={false}
						message="투표결과는 투표 후 확인이 가능합니다."
						actions={
							<Button
								className="w-full bg-blue-900 text-white"
								onClick={() => {
									router.push('/');
								}}
							>
								투표하고 결과보기
							</Button>
						}
					/>
				)}
			</div>
		);
	}

	return (
		<>
			<Head>
				<title>제 21대 대통령선거 결과 보기</title>
			</Head>

			<div className="min-h-screen py-7 px-4 w-full max-w-lg mx-auto space-y-4">
				<div className="flex items-center justify-between">
					<h1 className="text-xl font-bold">투표 결과</h1>
					<p className="tex-sm text-slate-500">
						{electionResultData?.data.updatedAt
							? `	최신 업데이트 : ${formatDate(
									electionResultData?.data.updatedAt,
									'yyyy.MM.dd HH:mm'
							  )}`
							: ''}
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

				<div className="bg-white rounded-xl p-6 shadow-sm space-y-2">
					<h2 className="text-sm font-semibold text-center">총 투표수</h2>
					<p className="text-3xl font-bold text-center">
						{electionResultData?.data.totalVoteCount.toLocaleString()}
					</p>
				</div>

				<div className="bg-white rounded-xl p-4 shadow-sm space-y-4">
					<h2 className="text-sm font-semibold text-center">투표 결과 요약</h2>

					<div className="relative px-4 ">
						{/* 데이터 막대 */}
						<div className="space-y-[10px] relative">
							{electionResultData?.data.results.map((item, idx) => {
								const percent =
									(item.voteCount / electionResultData.data.totalVoteCount) *
									100;
								return (
									<div key={idx} className="flex items-center justify-between">
										<div className="flex items-center w-[calc(100%-50px)]">
											<div
												className={`rounded-md min-w-[2px] transition-all duration-300 h-7`}
												style={{
													backgroundColor: item.partyColor,
													transform: 'scaleX(0)',
													transformOrigin: 'left',
													animation: `growX 1s ease-out forwards`,
													animationDelay: `${idx * 0.1}s`,
													width: `${percent}%`,
												}}
											/>
											<div className="text-sm font-semibold text-black min-w-[20px] ml-2 animate-slide-in">
												{item.voteCount}
											</div>
										</div>
										<p className="text-sm text-slate-400">{item.name}</p>
									</div>
								);
							})}
						</div>
					</div>
				</div>

				{/* 연령별 데이터 list */}
				<div className="space-y-4">
					{electionResultAgesData?.data?.results?.map((item) => {
						return (
							<div className="bg-white rounded-xl p-4 shadow-sm space-y-4">
								<div className="flex items-center justify-center">
									<h2 className="text-sm font-semibold">{item.age}대</h2>
									<p className="text-sm text-gray-500">
										&nbsp;(투표 인원: {item.totalCount})
									</p>
								</div>
								<AgesChart
									data={item.candidateResults}
									totalVoteCount={item.totalCount}
								/>
							</div>
						);
					})}
				</div>
			</div>
		</>
	);
}
