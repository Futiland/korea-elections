import { Button } from '@/components/ui/button';
import Head from 'next/head';
import { useRouter } from 'next/router';
import IntroduceLayout from '@/components/layout/IntroduceLayout';
import { Spinner } from '@/components/ui/spinner';
import {
	Card,
	CardDescription,
	CardHeader,
	CardTitle,
} from '@/components/ui/card';
import { UserRound } from 'lucide-react';
import { Separator } from '@/components/ui/separator';
import MyPollList from './MyPollList';
import type { MyPageViewProps } from '@/components/My/useMyPagePresenter';

export default function MyPageView({
	user,
	isLoading,
	isError,
	isLoggedIn,
	isReady,
	onLogout,
	onDeleteAccount,
	myPolls,
	myParticipatedPublicPolls,
	myParticipatedOpinionPolls,
	isFetchingMyPolls,
	isFetchingMyParticipatedPublicPolls,
	isFetchingMyParticipatedOpinionPolls,
	isErrorMyPolls,
	isErrorMyParticipatedPublicPolls,
	isErrorMyParticipatedOpinionPolls,
}: MyPageViewProps) {
	const router = useRouter();

	if (isLoading || !isReady) {
		return (
			<div className="flex items-center justify-center text-center py-10 min-h-screen">
				<Spinner />
			</div>
		);
	}

	if (!isLoggedIn || !user) {
		return (
			<div className="h-screen">
				<div className="bg-slate-50">
					<div className="w-full max-w-lg mx-auto px-5 py-10">
						<h1 className="text-xl font-bold py-5">로그인 후 이용해 주세요.</h1>
						<div className="flex flex-col items-center space-y-3.5">
							<Button
								type="submit"
								className="w-full bg-blue-800 hover:bg-blue-700 text-white h-10"
								onClick={() => router.push('/login')}
							>
								로그인
							</Button>

							{/* 회원가입 버튼 */}
							<Button
								type="submit"
								onClick={() => router.push('/signup')}
								className="w-full bg-blue-200 text-blue-900 hover:bg-blue-200 h-10 mb-4"
							>
								회원가입
							</Button>
						</div>
					</div>
				</div>

				<div className="w-full max-w-lg mx-auto px-7.5 pt-6 pb-15 bg-slate-100">
					<IntroduceLayout />
				</div>
			</div>
		);
	}

	return (
		<>
			<Head>
				<title>마이페이지 | KEP</title>
			</Head>

			<div className="min-h-screen bg-stale-100 px-4 py-6 relative">
				<div className="w-full max-w-lg mx-auto space-y-4">
					{/* 마이페이지 제목 */}
					<div className="flex justify-between items-center">
						<h1 className="text-xl font-bold">마이페이지</h1>
					</div>

					<Card className="w-full bg-gradient-to-br from-blue-700 via-blue-600 to-sky-600 transition-colors py-8">
						<CardHeader>
							<CardTitle className="flex items-center justify-between ">
								<div className="flex items-center gap-3">
									<UserRound
										className="w-10 h-10 bg-slate-100 rounded-full p-2"
										color="#193cb8"
									/>
									<div className="gap-2 flex items-center justify-start">
										<p className="text-white text-lg font-bold">
											{user?.data.name} 님
										</p>
										<p className="text-slate-100 text-xs font-normal">
											{user?.data.phoneNumber}
										</p>
									</div>
								</div>
								<Button
									className="bg-blue-100 text-blue-900 hover:bg-slate-200 text-xs h-6 rounded-full px-3"
									onClick={onLogout}
								>
									로그아웃
								</Button>
							</CardTitle>
							<Separator className="bg-slate-400 my-5" />
							<CardDescription className="flex items-center justify-between gap-4">
								<div className="w-1/3 flex flex-col justify-center items-center gap-1	">
									<p className="text-white text-3xl font-bold">5</p>
									<span className="text-slate-100 text-xs">만든 투표</span>
								</div>
								<div className="w-1/3 flex flex-col justify-center items-center gap-1">
									<p className="text-white text-3xl font-bold">32</p>
									<span className="text-slate-100 text-xs">참여한 투표</span>
								</div>
								<div className="w-1/3 flex flex-col justify-center items-center gap-1">
									<p className="text-white text-3xl font-bold">2</p>
									<span className="text-slate-100 text-xs">입법 투표</span>
								</div>
							</CardDescription>
						</CardHeader>
					</Card>

					{/* <MyPollList title="내가 만든 모두의 투표" /> */}

					{myParticipatedPublicPolls.content.length > 0 && (
						<MyPollList
							title="참여한 모두의 투표"
							items={myParticipatedPublicPolls.content}
						/>
					)}

					{myParticipatedOpinionPolls.content.length > 0 && (
						<MyPollList
							title="참여한 입법 투표"
							items={myParticipatedOpinionPolls.content}
						/>
					)}
				</div>

				<div className="w-full flex items-center justify-center py-10">
					<Button
						variant="ghost"
						className=" text-slate-400 h-10"
						onClick={onDeleteAccount}
					>
						회원 탈퇴
					</Button>
				</div>
			</div>
		</>
	);
}
