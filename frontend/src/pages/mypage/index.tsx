import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import Head from 'next/head';
import { getUserInfo, deleteAccount } from '@/lib/api/account';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useRouter } from 'next/router';
import type { UserInfo } from '@/lib/types/account';
import { formatDate } from '@/lib/date';
import IntroduceLayout from '@/components/layout/IntroduceLayout';
import { Spinner } from '@/components/ui/spinner';
import { useAuthToken } from '@/hooks/useAuthToken';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';
import { toast } from 'sonner';

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
			<div className="text-center py-10 min-h-screen">
				<Spinner />
			</div>
		);
	}

	if (!isLoggedIn || !user) {
		return (
			<div className=" bg-slate-10 ">
				<div className="bg-slate-50">
					<div className="w-full max-w-lg mx-auto px-5">
						<h1 className="text-xl font-bold py-5">로그인 하세요</h1>
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

				<div className="w-full max-w-lg mx-auto px-7.5 py-6 bg-slate-100">
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

						{/* 프로필 이미지 (예시용) */}
						{/* <Image
							src="/profile.png" // public 폴더 기준
							alt="프로필"
							width={48}
							height={48}
							className="rounded-full"
						/> */}
					</div>

					<div>
						<label className="text-sm">이름</label>
						<Input value={user?.data.name} disabled className="bg-white h-10" />
					</div>

					{/* 휴대폰 번호 */}
					<div>
						<label className="text-sm">휴대폰 번호 (아이디)</label>
						<Input
							value={user?.data.phoneNumber}
							disabled
							className="bg-white h-10"
						/>
					</div>

					{/* 가입일 */}
					<div>
						<label className="text-sm">가입일</label>
						<Input
							value={formatDate(user.data.createdAt)}
							disabled
							className="bg-white h-10"
						/>
					</div>

					<div className="flex justify-end">
						<Button
							variant="ghost"
							className="text-slate-600 h-10"
							onClick={logoutHandler}
						>
							로그아웃
						</Button>
					</div>

					{/* 비밀번호 변경 */}
					{/* <div className="flex gap-2">
						<div className="flex-1">
							<label className="text-sm font-medium">비밀번호</label>
							<Input
								type="password"
								value="******"
								disabled
								className="bg-white h-10"
							/>
						</div>
						<Button className="mt-6 bg-gray-900 text-white hover:bg-gray-800">
							비밀번호 변경
						</Button>
					</div> */}
				</div>
				<div className="w-full absolute bottom-34 left-0 flex items-center justify-center space-x-4">
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
