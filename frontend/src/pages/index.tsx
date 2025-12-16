import Head from 'next/head';
import { useQuery } from '@tanstack/react-query';
import { GetServerSideProps } from 'next';
import { QueryClient, dehydrate } from '@tanstack/react-query';
import { useRouter } from 'next/router';

import PollPreviewSection from '@/components/Home/PollPreviewSection';
import PollCarouselSection from '@/components/Home/PollCarouselSection';
import ClosingSoonCarouselCard from '@/components/Home/cards/ClosingSoonCarouselCard';
import CategorySelector from '@/components/Home/CategorySelector';
import { getOpinionPolls, getPublicPolls } from '@/lib/api/poll';
import { Spinner } from '@/components/ui/spinner';
import PollCarouselEventCard from '@/components/Home/cards/PollCarouselEventCard';

const OPINION_POLL_SIZE = 5;
const POPULAR_POLL_SIZE = 3;
const ENDING_SOON_POLL_SIZE = 7;
const EVENT_POLL_SIZE = 5;

export default function Home() {
	const router = useRouter();

	// 메인 여론 조사
	const {
		data: opinionPollsData,
		isLoading: isOpinionLoading,
		isError: isOpinionError,
	} = useQuery({
		queryKey: ['homeOpinionPolls', OPINION_POLL_SIZE],
		queryFn: () =>
			getPublicPolls(
				OPINION_POLL_SIZE,
				undefined,
				undefined,
				'IN_PROGRESS',
				'LATEST'
			),
	});

	// 인기 투표
	const {
		data: popularPollsData,
		isLoading: isPopularLoading,
		isError: isPopularError,
	} = useQuery({
		queryKey: ['homePopularPolls', POPULAR_POLL_SIZE],
		queryFn: () =>
			getPublicPolls(
				POPULAR_POLL_SIZE,
				undefined,
				undefined,
				'IN_PROGRESS',
				'POPULAR'
			),
	});

	// 마감 임박 투표
	const {
		data: endingSoonPollsData,
		isLoading: isEndingSoonLoading,
		isError: isEndingSoonError,
	} = useQuery({
		queryKey: ['homeEndingSoonPolls', ENDING_SOON_POLL_SIZE],
		queryFn: () =>
			getPublicPolls(
				ENDING_SOON_POLL_SIZE,
				undefined,
				undefined,
				'IN_PROGRESS',
				'ENDING_SOON'
			),
	});

	// 이벤트 투표
	const {
		data: eventPollsData,
		isLoading: isEventLoading,
		isError: isEventError,
	} = useQuery({
		queryKey: ['homeEventPolls', EVENT_POLL_SIZE],
		queryFn: () =>
			getPublicPolls(EVENT_POLL_SIZE, undefined, '크리스마스', 'ALL', 'LATEST'),
	});

	const opinionPolls = opinionPollsData?.data?.content || [];
	const popularPolls = popularPollsData?.data?.content || [];
	const endingSoonPolls = endingSoonPollsData?.data?.content || [];
	const eventPolls = eventPollsData?.data?.content || [];
	const isLoading =
		isOpinionLoading ||
		isPopularLoading ||
		isEndingSoonLoading ||
		isEventLoading;
	const hasError =
		isOpinionError || isPopularError || isEndingSoonError || isEventError;

	return (
		<>
			<Head>
				<title>모두의 투표</title>
				<meta
					name="description"
					content="쉽게 만들고, 바로 공유하고, 함께 참여하는 투표 플랫폼"
				/>
			</Head>

			<main className="flex min-h-screen flex-col items-center bg-slate-50 pb-16">
				{/* 카테고리 선택 */}
				<CategorySelector />

				{isLoading ? (
					<div className="flex justify-center items-center min-h-[400px]">
						<Spinner className="w-10 h-10 text-blue-500" />
					</div>
				) : hasError ? (
					<div className="flex justify-center items-center min-h-[400px]">
						<p className="text-slate-500 text-lg">
							데이터를 불러오는 중 오류가 발생했습니다.
						</p>
					</div>
				) : (
					<>
						{/* 최신 투표 카루셀 */}
						{opinionPolls.length > 0 && (
							<PollCarouselSection
								polls={opinionPolls}
								autoplay={true}
								onClickMore={() => {
									router.push('/everyone-polls?sort=LATEST&status=IN_PROGRESS');
								}}
								onClickPoll={(pollId: string) => {
									router.push(`/everyone-polls/${pollId}`);
								}}
							/>
						)}

						{/* 인기 투표 섹션 */}
						{popularPolls.length > 0 && (
							<PollPreviewSection
								title="인기있는 모두의 투표"
								description="지금 가장 인기있는 모두의 투표를 확인해보세요."
								polls={popularPolls}
								onClickMore={() => {
									router.push(
										'/everyone-polls?sort=POPULAR&status=IN_PROGRESS'
									);
								}}
								onClickPoll={(pollId: string) => {
									router.push(`/everyone-polls/${pollId}`);
								}}
							/>
						)}

						{/* 이벤트 투표 카루셀 */}
						{eventPolls.length > 0 && (
							<PollCarouselSection
								title="이벤트 투표"
								description="크리스마스 이벤트 투표를 확인해보세요."
								polls={eventPolls}
								moreLabel={true}
								autoplay={true}
								onClickMore={() => {
									router.push('/everyone-polls?search=크리스마스&status=ALL');
								}}
								onClickPoll={(pollId: string) => {
									router.push(`/everyone-polls/${pollId}`);
								}}
								CardComponent={PollCarouselEventCard}
								paginationActiveColor="bg-red-600"
								paginationInactiveColor="bg-slate-300"
							/>
						)}

						{/* 마감 임박 투표 카루셀 */}
						{endingSoonPolls.length > 0 && (
							<PollCarouselSection
								title="마감 임박 투표"
								description="지금 마감 임박 투표를 확인해보세요."
								polls={endingSoonPolls}
								moreLabel={true}
								onClickMore={() => {
									router.push(
										'/everyone-polls?sort=ENDING_SOON&status=IN_PROGRESS'
									);
								}}
								onClickPoll={(pollId: string) => {
									router.push(`/everyone-polls/${pollId}`);
								}}
								CardComponent={ClosingSoonCarouselCard}
								paginationActiveColor="bg-amber-600"
								paginationInactiveColor="bg-slate-300"
							/>
						)}
					</>
				)}
			</main>
		</>
	);
}

export const getServerSideProps: GetServerSideProps = async () => {
	const queryClient = new QueryClient();

	// 병렬로 3가지 데이터 prefetch
	await Promise.all([
		// 최신 투표
		queryClient.prefetchQuery({
			queryKey: ['homeOpinionPolls', OPINION_POLL_SIZE],
			queryFn: () =>
				getPublicPolls(
					OPINION_POLL_SIZE,
					undefined,
					undefined,
					'IN_PROGRESS',
					'LATEST'
				),
		}),
		// 인기 투표
		queryClient.prefetchQuery({
			queryKey: ['homePopularPolls', POPULAR_POLL_SIZE],
			queryFn: () =>
				getPublicPolls(
					POPULAR_POLL_SIZE,
					undefined,
					undefined,
					'IN_PROGRESS',
					'POPULAR'
				),
		}),
		// 마감 임박 투표
		queryClient.prefetchQuery({
			queryKey: ['homeEndingSoonPolls', ENDING_SOON_POLL_SIZE],
			queryFn: () =>
				getPublicPolls(
					ENDING_SOON_POLL_SIZE,
					undefined,
					undefined,
					'IN_PROGRESS',
					'ENDING_SOON'
				),
		}),
	]);

	return {
		props: {
			dehydratedState: dehydrate(queryClient),
		},
	};
};
