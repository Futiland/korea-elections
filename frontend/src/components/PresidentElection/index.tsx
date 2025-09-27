import { useEffect, useState } from 'react';
import { GetServerSideProps } from 'next';
import {
	dehydrate,
	QueryClient,
	useQuery,
	useMutation,
} from '@tanstack/react-query';
import { getCandidateList, getMyVotedCandidate } from '@/lib/api/election';
import type {
	CandidateResponse,
	MyVotedCandidateResponse,
} from '@/lib/types/election';
import Image from 'next/image';
import { Spinner } from '../ui/spinner';
import { CandidateCard } from './CandidateCard';
import { Button } from '@/components/ui/button';
import { election } from '@/lib/api/election';
import { toast } from 'sonner';
import { useRouter } from 'next/router';
import { Loader2 } from 'lucide-react';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';
import { useAuthToken } from '@/hooks/useAuthToken';
import Footer from '@/components/Footer';

export const getServerSideProps: GetServerSideProps = async () => {
	const queryClient = new QueryClient();

	await queryClient.prefetchQuery({
		queryKey: ['electionList'],
		queryFn: () => getCandidateList(10),
	});

	return {
		props: {
			dehydratedState: dehydrate(queryClient),
		},
	};
};

export default function ElectionList() {
	const router = useRouter();
	const { showDialog, hideDialog } = useAlertDialog();

	const [selectedCandidateId, setSelectedCandidateId] = useState<number | null>(
		null
	);

	const { isLoggedIn, isReady } = useAuthToken();

	const {
		data: myVotedCandidate,
		isFetching: isMyVotedCandidateFetching,
		isError: isMyVotedCandidateError,
	} = useQuery<MyVotedCandidateResponse, Error>({
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

	const {
		data: candidateList,
		isFetching: isCandidateListFetching,
		isError: isCandidateListError,
	} = useQuery<CandidateResponse, Error>({
		queryKey: ['candidateList'],
		queryFn: () => getCandidateList(),
		retry: 1,
		refetchOnWindowFocus: false,
	});

	const electionMutation = useMutation({
		mutationFn: ({
			electionId,
			candidateId,
		}: {
			electionId: number;
			candidateId: number;
		}) => election(electionId, candidateId),

		onSuccess: (data) => {
			toast.success('투표 성공!');
			// 투표 성공 시 결과 페이지로 이동
			router.push('/resultPage');
		},
		onError: (error: any) => {
			toast.error('투표 실패: ' + (error?.message || '다시 시도해주세요.'));
		},
	});

	const handleVote = () => {
		if (!isLoggedIn) {
			// 토큰이 없으면 로그인 페이지로 이동
			showDialog({
				message: '투표는 로그인 후 가능합니다.',
				actions: (
					<Button
						className="w-full bg-blue-900 text-white"
						onClick={() => {
							router.push('/login');
							hideDialog();
						}}
					>
						로그인하고 투표하기
					</Button>
				),
			});
			return;
		}

		if (selectedCandidateId === null) {
			toast('후보를 선택해주세요.');
			return;
		}

		const electionId = 1;
		electionMutation.mutate({ electionId, candidateId: selectedCandidateId });
	};

	const handleVoteClick = (candidateId: number) => {
		setSelectedCandidateId(candidateId);
	};

	useEffect(() => {
		if (myVotedCandidateId) {
			// 이미 투표한 경우 선택된 후보 ID 설정
			setSelectedCandidateId(myVotedCandidateId);
		} else {
			// 투표하지 않은 경우 초기화
			setSelectedCandidateId(null);
		}
	}, [myVotedCandidateId]);

	if (isCandidateListFetching)
		return (
			<div className="text-center py-10 min-h-screen">
				<Spinner />
			</div>
		);

	if (isCandidateListError)
		return (
			<div className="text-center py-10 min-h-screen">
				문제가 발생했습니다. 잠시 후 다시 시도해주세요.
			</div>
		);

	return (
		<>
			<div className="mb-20">
				<div className="flex flex-col items-center pt-5 pb-4">
					<div className="text-slate-400 text-sm mb-5 text-center">
						“21대 대통령 선거에 누구를 뽑으셨나요?”
					</div>
					{/* 청화대 로고 */}
					<div className="relative w-[200px] h-[100px] mt-3 sm:w-[300px] sm:h-[150px]">
						<Image
							src="/img/chunghwadae-logo.png"
							alt="chunghwadae logo"
							fill
							className="object-contain"
							priority
						/>
						<div className="absolute bottom-0 left-0 w-full h-full bg-gradient-to-b from-transparent via-slate-50/70 to-slate-50" />
					</div>
					{/* 후보자 이미지 */}
					<div className="relative w-[320px] h-[200px] sm:w-[472px] sm:h-[295px] border border-gray-200 rounded-lg shadow-md overflow-hidden">
						<div className="absolute top-0 left-0 w-full h-full">
							<Image
								src="/img/main-img-bg.png"
								alt="background"
								fill
								className="object-contain"
							/>
						</div>
						<div className="absolute bottom-0 left-0 h-[150px] sm:h-[221px] w-full">
							<Image
								src="/img/candidate-group.png"
								alt="candidate group"
								fill
								className="object-cover"
							/>
						</div>
					</div>
				</div>

				<div className="text-slate-400 text-sm mb-5 text-center">
					"선거 종료 시까지 언제든지 재투표가 가능합니다."
				</div>

				<div className="max-w-lg mx-auto px-5">
					<ul className="flex flex-col space-y-3">
						{candidateList?.data?.map((item) => (
							<li key={item.id}>
								<CandidateCard
									item={item}
									handleVoteClick={handleVoteClick}
									isSelected={selectedCandidateId === item.id}
									selectedCandidateId={selectedCandidateId}
								/>
							</li>
						))}
					</ul>
				</div>

				<div className="px-5 fixed bottom-21 left-0 right-0 max-w-xl mx-auto z-5">
					<Button
						className="w-full bg-blue-900 hover:bg-blue-800 text-white h-10"
						disabled={
							electionMutation.isPending ||
							process.env.NEXT_PUBLIC_ENV === 'production'
						}
						onClick={() => handleVote()}
					>
						{electionMutation.isPending && (
							<Loader2 className="h-4 w-4 animate-spin" />
						)}
						{process.env.NEXT_PUBLIC_ENV === 'production'
							? '선거 종료'
							: myVotedCandidateId
							? '재 투표 & 결과보기'
							: '투표 & 결과보기'}
					</Button>
				</div>
			</div>
			<Footer />
		</>
	);
}
