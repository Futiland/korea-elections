import Head from 'next/head';
import { useRouter } from 'next/router';
import { useQuery, QueryClient, dehydrate } from '@tanstack/react-query';
import type { GetStaticProps, GetStaticPaths } from 'next';

import PollCard from '@/components/PollCard';
import type { PollResponse } from '@/lib/types/poll';
import { getPoll } from '@/lib/api/poll';
import { Spinner } from '@/components/ui/spinner';
import { Button } from '@/components/ui/button';
import { Home } from 'lucide-react';

export default function EveryonePollDetailPage() {
	const router = useRouter();
	const { id } = router.query;

	const pollId = Number(id);
	const enabled = !!pollId && !Number.isNaN(pollId);

	const {
		data: poll,
		isFetching,
		isError,
	} = useQuery<PollResponse>({
		queryKey: ['publicPoll', pollId],
		queryFn: () => getPoll(pollId),
		enabled,
		retry: 1,
		refetchOnWindowFocus: false,
	});

	if (!enabled) {
		return null; // 라우터가 아직 준비되지 않음
	}

	if (isFetching) {
		return (
			<div className="min-h-screen flex items-center justify-center bg-slate-50">
				<Spinner className="w-10 h-10 text-blue-500" />
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
				<meta property="og:title" content={poll.data.title} />
				<meta property="og:description" content={poll.data.description} />
				<meta property="og:image" content="/img/everyone-polls.png" />
				<meta
					property="og:url"
					content={`${process.env.NEXT_PUBLIC_BASE_URL}/everyone-polls/${poll.data.id}`}
				/>
				<meta property="og:locale" content="ko_KR" />
				<meta property="og:type" content="website" />
				<meta property="og:site_name" content="KEP" />
				<meta property="og:image:width" content="1200" />
				<meta property="og:image:height" content="630" />
			</Head>

			<main className="min-h-screen bg-slate-50 pb-24">
				<section className="mx-auto max-w-2xl py-8 px-4 space-y-4">
					<PollCard pollData={poll.data} />
					<div className="flex justify-center">
						<div className="mx-auto px-4 justify-center flex w-full max-w-sm items-center gap-2">
							<Button
								type="button"
								variant="outline"
								size="icon"
								className="h-10 w-10 rounded-full"
								onClick={() => router.push('/')}
								aria-label="홈으로 이동"
							>
								<Home className="size-5" />
							</Button>
							<Button
								type="button"
								size="default"
								className="w-34 h-10 rounded-full border border-blue-200 bg-blue-50 text-blue-800 hover:bg-blue-100 font-semibold text-sm"
								onClick={() => router.push('/everyone-polls')}
							>
								다른 투표 보기
							</Button>
						</div>
					</div>
				</section>
			</main>
		</>
	);
}

export const getStaticPaths: GetStaticPaths = async () => {
	return {
		paths: [],
		fallback: 'blocking',
	};
};

export const getStaticProps: GetStaticProps = async (context) => {
	const id = context.params?.id;
	const pollId = Number(id);

	if (!pollId || Number.isNaN(pollId)) {
		return {
			notFound: true,
		};
	}

	const queryClient = new QueryClient();

	try {
		await queryClient.prefetchQuery<PollResponse>({
			queryKey: ['publicPoll', pollId],
			queryFn: () => getPoll(pollId),
		});
	} catch (error) {
		return {
			notFound: true,
		};
	}

	return {
		props: {
			dehydratedState: dehydrate(queryClient),
		},
		revalidate: 600,
	};
};
