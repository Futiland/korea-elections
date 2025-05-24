import { useEffect, useState } from 'react';
import { GetServerSideProps } from 'next';
import {
	dehydrate,
	QueryClient,
	useQuery,
	useMutation,
} from '@tanstack/react-query';
import { getCandidateList } from '@/lib/api/election';
import type { CandidateResponse } from '@/lib/types/election';
import Image from 'next/image';
import { Spinner } from '../ui/spinner';
import { CandidateCard } from './CandidateCard';
import { Button } from '@/components/ui/button';
import { election } from '@/lib/api/election';
import { toast } from 'sonner';
import { useRouter } from 'next/router';
import { Loader2 } from 'lucide-react';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';

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

	const [token, setToken] = useState<string | null | undefined>(undefined);
	const [selectedCandidateId, setSelectedCandidateId] = useState<number | null>(
		null
	);

	const { data, isFetching, isError } = useQuery<CandidateResponse, Error>({
		queryKey: ['electionList'],
		queryFn: () => getCandidateList(),
		retry: 2,
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
		if (!token) {
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
		console.log('candidateId', candidateId);
	};

	useEffect(() => {
		if (typeof window !== 'undefined') {
			setToken(localStorage.getItem('token'));
		}
	}, []);

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
					<div className="text-slate-400 text-sm mb-5 text-center">
						우리는 선거의 공정성과 <br />
						시민의 의견을 최우선으로 생각합니다.
					</div>
					<div className="relative w-[300px] h-[150px]">
						<Image
							src="/img/vote.svg"
							alt="K제 21대 대통령 선거"
							fill
							className="object-contain"
							priority
						/>
					</div>
				</div>

				<div className="max-w-lg mx-auto px-5">
					<ul className="flex flex-col space-y-3">
						{data?.data?.map((item) => (
							<li key={item.id}>
								<CandidateCard item={item} handleVoteClick={handleVoteClick} />
							</li>
						))}
					</ul>
				</div>

				<div className="px-5 fixed bottom-21 left-0 right-0">
					<Button
						className="w-full bg-blue-900 hover:bg-blue-800 text-white h-10"
						disabled={electionMutation.isPending}
						onClick={() => handleVote()}
					>
						{electionMutation.isPending && (
							<Loader2 className="h-4 w-4 animate-spin" />
						)}
						투표 & 결과보기
					</Button>
				</div>
			</div>
		</>
	);
}
