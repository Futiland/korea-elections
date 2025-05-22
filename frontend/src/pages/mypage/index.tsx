import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import Head from 'next/head';
import Image from 'next/image';
import { useEffect, useState } from 'react';
import { getUserInfo } from '@/lib/api/account';
import { useQuery } from '@tanstack/react-query';
import { useRouter } from 'next/router';
import type { UserInfo } from '@/lib/types/account';
import { formatDate } from '@/lib/date';
import IntroduceLayout from '@/components/IntroduceLayout';

export default function MyPage() {
	const router = useRouter();

	const [token, setToken] = useState<string | null>(null);

	const {
		data: user,
		isLoading,
		isError,
	} = useQuery<UserInfo>({
		queryKey: ['userInfo'],
		queryFn: getUserInfo,
		enabled: !!token, // 토큰이 있을 때만 쿼리 실행
		refetchOnWindowFocus: false,
		retry: 2,
	});

	useEffect(() => {
		if (typeof window !== 'undefined') {
			setToken(localStorage.getItem('token'));
		}
	}, []);

	if (isLoading) {
		return <div className="text-center py-10">로딩 중...</div>;
	}

	if (isError || !user) {
		return (
			<div className="min-h-screen bg-slate-100 ">
				<div className="bg-white">
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
								className="w-full bg-blue-100 text-blue-900 hover:bg-blue-200 h-10 mb-4"
							>
								회원가입
							</Button>
						</div>
					</div>
				</div>

				<div className="w-full max-w-lg mx-auto px-7.5 pt-6 py-30">
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

			<div className="min-h-screen bg-stale-100 px-4 py-6">
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
						<Input value={user.data.name} disabled className="bg-white h-10" />
					</div>

					{/* 휴대폰 번호 */}
					<div>
						<label className="text-sm">휴대폰 번호 (아이디)</label>
						<Input
							value={user.data.phoneNumber}
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
			</div>
		</>
	);
}
