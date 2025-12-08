// import HomePage from '@/components/PresidentElection';
import Head from 'next/head';

import PollPreviewSection from '@/components/Home/PollPreviewSection';
import router from 'next/router';
import PollCarouselSection from '@/components/Home/PollCarouselSection';
import ClosingSoonCarouselCard from '@/components/Home/cards/ClosingSoonCarouselCard';
import CategorySelector from '@/components/Home/CategorySelector';

export default function Home() {
	return (
		<>
			<Head>
				<title>
					{/* 제 21대 대통령 선거 투표_“21대 대통령 선거에 누구를 뽑으셨나요?” */}
					모두의 투표
				</title>
			</Head>

			<main className="flex min-h-screen flex-col items-center bg-slate-50 pb-16">
				{/* 카테고리 선택 */}
				<CategorySelector />

				<PollCarouselSection
					autoplay={true}
					onClickMore={() => {
						router.push('/everyone-poll');
					}}
					onClickPoll={(pollId) => {
						router.push(`/everyone-poll/${pollId}`);
					}}
				/>
				<PollPreviewSection
					title="인기있는 모두의 투표"
					description="지금 가장 인기있는 모두의 투표를 확인해보세요."
					onClickMore={() => {
						router.push('/everyone-poll');
					}}
					onClickPoll={(pollId) => {
						router.push(`/everyone-poll/${pollId}`);
					}}
				/>
				<PollCarouselSection
					title="마감 임박 투표"
					description="지금 마감 임박 투표를 확인해보세요."
					moreLabel={true}
					onClickMore={() => {
						router.push('/everyone-poll');
					}}
					onClickPoll={(pollId) => {
						router.push(`/everyone-poll/${pollId}`);
					}}
					CardComponent={ClosingSoonCarouselCard}
				/>
			</main>
		</>
	);
}
