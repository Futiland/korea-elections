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

interface OpinionPollDetailPageProps {
	pollData?: PollResponse['data'];
}

export default function OpinionPollDetailPage({
	pollData: initialPollData,
}: OpinionPollDetailPageProps) {
	const router = useRouter();
	const { id } = router.query;

	const pollId = Number(id);
	const enabled = !!pollId && !Number.isNaN(pollId);

	const {
		data: poll,
		isFetching,
		isError,
	} = useQuery<PollResponse>({
		queryKey: ['opinionPoll', pollId],
		queryFn: () => getPoll(pollId),
		enabled,
		retry: 1,
		refetchOnWindowFocus: false,
		initialData: initialPollData
			? ({ data: initialPollData } as PollResponse)
			: undefined,
	});

	if (!enabled) {
		return null; // 라우터가 아직 준비되지 않음
	}

	if (isFetching && !poll?.data) {
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

	const ogTitle = poll.data.title;
	const ogDescription =
		poll.data.description ||
		'KEP는 본인 인증된 시민만 참여하는 여론조사 플랫폼입니다.';
	const ogUrl = `${process.env.NEXT_PUBLIC_BASE_URL}/opinion-polls/${poll.data.id}`;

	return (
		<>
			<Head>
				<title>{ogTitle} - 민심 투표</title>
				<meta name="description" content={ogDescription} />
				<meta key="og:title" property="og:title" content={ogTitle} />
				<meta
					key="og:description"
					property="og:description"
					content={ogDescription}
				/>
				<meta key="og:url" property="og:url" content={ogUrl} />
				<meta property="og:image" content="/img/everyone-polls.png" />
				<meta property="og:locale" content="ko_KR" />
				<meta property="og:type" content="website" />
				<meta property="og:site_name" content="모두의 투표" />
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
								onClick={() => router.push('/opinion-polls')}
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
		const pollData = await queryClient.fetchQuery<PollResponse>({
			queryKey: ['opinionPoll', pollId],
			queryFn: () => getPoll(pollId),
		});

		return {
			props: {
				dehydratedState: dehydrate(queryClient),
				pollData: pollData.data, // 서버 사이드에서 메타 태그 설정을 위한 데이터
			},
			revalidate: 600,
		};
	} catch (error) {
		return {
			notFound: true,
		};
	}
};
