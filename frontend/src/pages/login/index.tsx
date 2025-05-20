import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import Image from 'next/image';
import Head from 'next/head';
import { useMutation } from '@tanstack/react-query';
import { login } from '@/lib/api/account';
import type { LoginData } from '@/lib/types/account';
import { useState } from 'react';
import { useRouter } from 'next/router';

export default function LoginPage() {
	const [phoneNumber, setPhoneNumber] = useState('');
	const [password, setPassword] = useState('');
	const router = useRouter();

	// 쿼리에서 redirect 경로 가져오기 (ex: /login?redirect=/vote)
	const redirectPath =
		typeof router.query.redirect === 'string' ? router.query.redirect : '/';

	const loginMutation = useMutation({
		mutationFn: (data: LoginData) => login(data),
		onSuccess: (data) => {
			// 토큰 저장 및 페이지 이동
			localStorage.setItem('token', data.data.token);
			router.push(redirectPath);
		},
		onError: (error: any) => {
			alert('로그인 실패: ' + (error?.message || '알 수 없는 오류'));
		},
	});

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		loginMutation.mutate({ phoneNumber, password });
	};

	return (
		<>
			<Head>
				<title>로그인 | KEP</title>
			</Head>

			<div className="min-h-screen flex items-center justify-center px-4 bg-white">
				<div className="w-full max-w-sm p-6 rounded-md">
					{/* 로고 */}
					<div className="flex flex-col items-center mb-6">
						<Image
							src="/img/logo-c.svg"
							alt="KEP 로고"
							width={80}
							height={80}
							className="mb-2"
						/>
						<h1 className="text-5xl font-bold">KEP</h1>
					</div>

					{/* 입력 폼 */}
					<form className="space-y-4" onSubmit={handleSubmit}>
						<div>
							<label className="text-sm font-medium">휴대폰 번호</label>
							<Input
								placeholder="휴대폰 번호를 입력해 주세요."
								value={phoneNumber}
								onChange={(e) => setPhoneNumber(e.target.value)}
							/>
						</div>
						<div>
							<label className="text-sm font-medium">비밀번호</label>
							<Input
								type="password"
								placeholder="비밀번호를 입력해 주세요."
								value={password}
								onChange={(e) => setPassword(e.target.value)}
							/>
						</div>

						{/* 로그인 버튼 */}
						<Button
							type="submit"
							className="w-full bg-blue-900 hover:bg-blue-800 text-white"
							disabled={loginMutation.isPending}
						>
							{loginMutation.isPending ? '로그인 중...' : '로그인'}
						</Button>

						{/* 회원가입 버튼 */}
						<Button
							type="button"
							onClick={() => router.push('/signup')}
							className="w-full bg-blue-100 text-blue-900 hover:bg-blue-200"
						>
							회원가입
						</Button>

						<div className="flex flex-col items-center">
							<Button variant="outline" className="py-2 px-4 mt-4">
								KEP는 어떤 서비스 인가요 ?
							</Button>
						</div>
					</form>
				</div>
			</div>
		</>
	);
}
