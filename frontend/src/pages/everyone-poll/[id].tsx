import Head from 'next/head';
import { useRouter } from 'next/router';
import { useQuery } from '@tanstack/react-query';

import PollCard from '@/components/PollCard';
import type { PollResponse } from '@/lib/types/poll';
import { getPoll } from '@/lib/api/poll';
import { Spinner } from '@/components/ui/spinner';

export default function EveryonePollDetailPage() {
	const router = useRouter();
	const { id } = router.query;

	const pollId = Number(id);
	const enabled = !!pollId && !Number.isNaN(pollId);

	const {
		data: poll,
		isLoading,
		isError,
	} = useQuery<PollResponse>({
		queryKey: ['publicPoll', pollId],
		queryFn: () => getPoll(pollId),
		enabled,
		retry: 1,
	});

	if (!enabled) {
		return null; // 라우터가 아직 준비되지 않음
	}

	if (isLoading) {
		return (
			<div className="min-h-screen flex items-center justify-center bg-slate-50">
				<Spinner />
			</div>
		);
	}

	if (isError || !poll?.data) {
		return (
			<div className="min-h-screen flex items-center justify-center bg-slate-50">
				<p className="text-slate-500 text-sm">해당 투표를 찾을 수 없습니다.</p>
			</div>
		);
	}

	return (
		<>
			<Head>
				<title>{poll.data.title} - 모두의 투표</title>
			</Head>

			<main className="min-h-screen bg-slate-50">
				<section className="mx-auto max-w-2xl py-8 px-4">
					<PollCard pollData={poll.data} />
				</section>
			</main>
		</>
	);
}
