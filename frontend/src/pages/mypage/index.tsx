import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import Head from 'next/head';
import { getUserInfo, deleteAccount } from '@/lib/api/account';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useRouter } from 'next/router';
import type { UserInfo } from '@/lib/types/account';
import IntroduceLayout from '@/components/layout/IntroduceLayout';
import { Spinner } from '@/components/ui/spinner';
import { useAuthToken } from '@/hooks/useAuthToken';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';
import { toast } from 'sonner';
import {
	Card,
	CardDescription,
	CardHeader,
	CardTitle,
} from '@/components/ui/card';
import { UserRound } from 'lucide-react';
import { Separator } from '@/components/ui/separator';

import MyVoteList from './MyVoteList';

export default function MyPage() {
	const router = useRouter();
	const { showDialog, hideDialog } = useAlertDialog();

	const { isLoggedIn, isReady } = useAuthToken();

	const {
		data: user,
		isFetching,
		isError,
	} = useQuery<UserInfo>({
		queryKey: ['userInfo'],
		queryFn: getUserInfo,
		enabled: isLoggedIn && isReady, // 토큰이 있을 때만 쿼리 실행
		refetchOnWindowFocus: false,
		retry: 2,
	});

	const logoutHandler = () => {
		localStorage.removeItem('token');
		router.push({
			pathname: '/login',
			query: { redirect: router.asPath },
		});
	};

	const deleteMutation = useMutation({
		mutationFn: deleteAccount,
		onSuccess: () => {
			toast.success('회원 탈퇴가 완료되었습니다.');
			localStorage.removeItem('token');
			router.replace('/');
		},
		onError: (error: any) => {
			toast.error(error?.message || '회원 탈퇴 중 오류가 발생했습니다.');
		},
	});

	const handleDelete = () => {
		showDialog({
			message: '정말로 탈퇴하시겠어요?',
			discription:
				'탈퇴 시 모든 데이터가 삭제되며 복구가 불가능하며, 30일 후 재가입이 가능합니다.',
			actions: (
				<div className="flex gap-2 w-full">
					<Button
						className="w-1/2 bg-slate-200 text-slate-900 hover:bg-slate-200"
						onClick={() => hideDialog()}
					>
						취소
					</Button>
					<Button
						className="w-1/2 bg-red-600 hover:bg-red-500 text-white"
						onClick={() => {
							deleteMutation.mutate();
							hideDialog();
						}}
						disabled={deleteMutation.isPending}
					>
						{deleteMutation.isPending ? '처리 중...' : '회원 탈퇴'}
					</Button>
				</div>
			),
		});
	};

	if (isFetching || !isReady) {
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
								className="w-full bg-blue-900 hover:bg-blue-800 text-white h-10"
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

					<Card className="w-full bg-blue-800 transition-colors py-8">
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
									onClick={logoutHandler}
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

					<MyVoteList title="내가 만든 모두의 투표" />
					<MyVoteList title="참여한 모두의 투표" />
					<MyVoteList title="참여한 입법 투표" />
				</div>

				<div className="w-full flex items-center justify-center py-10">
					<Button
						variant="ghost"
						className=" text-slate-400 h-10"
						onClick={handleDelete}
					>
						회원 탈퇴
					</Button>
				</div>
			</div>
		</>
	);
}
