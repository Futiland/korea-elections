import { useState } from 'react';
import { GetServerSideProps } from 'next';
import {
	dehydrate,
	QueryClient,
	useQuery,
	useMutation,
} from '@tanstack/react-query';
import { getCandidateList } from '@/lib/api/election';
import type { ListResponseInfinity } from '@/lib/types/common';
import type { CandidateResponse } from '@/lib/types/election';
import { Separator } from '@/components/ui/separator';
import Image from 'next/image';
import { Spinner } from '../ui/spinner';
import { CandidateCard } from './CandidateCard';
import { Button } from '@/components/ui/button';
import { election } from '@/lib/api/election';
import { toast } from 'sonner';
import { useRouter } from 'next/router';
import { Loader2 } from 'lucide-react';

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
	const router = useRouter();
	const [selectedCandidate, setSelectedCandidate] = useState<number | null>(
		null
	);

	const { data, isFetching, isError } = useQuery<CandidateResponse, Error>({
		queryKey: ['electionList'],
		queryFn: () => getCandidateList(),
		retry: 2,
		refetchOnWindowFocus: false,
	});

	const electionMutation = useMutation({
		mutationFn: (electionId: number) => election(electionId),
		onSuccess: (data) => {
			toast.success('투표 성공!');
			// 투표 성공 시 결과 페이지로 이동
			router.push('/resultPage');
		},
		onError: (error: any) => {
			toast.error('투표 실패: ' + (error?.message || '알 수 없는 오류'));
		},
	});

	const handleVote = (electionId: number) => {
		electionMutation.mutate(electionId);
	};

	const handleVoteClick = (electionId: number) => {
		handleVote(electionId);
	};

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
					<Image
						src="/img/vote.svg"
						alt="K제 21대 대통령 선거"
						width={300}
						height={150}
						className="mb-2"
					/>
				</div>

				<div className="max-w-lg mx-auto px-5">
					<ul className="flex flex-col space-y-3">
						{data?.data?.map((item) => (
							<li key={item.id}>
								<CandidateCard item={item} />
							</li>
						))}
					</ul>
				</div>

				<div className="px-5 fixed bottom-21 left-0 right-0">
					<Button
						className="w-full bg-blue-900 hover:bg-blue-800 text-white h-10"
						disabled={electionMutation.isPending}
						onClick={() => {
							if (selectedCandidate) {
								handleVoteClick(selectedCandidate);
							} else {
								toast.error('후보를 선택해주세요.');
							}
						}}
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
