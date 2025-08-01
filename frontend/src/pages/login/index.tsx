import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import Image from 'next/image';
import Head from 'next/head';
import { useMutation } from '@tanstack/react-query';
import { login } from '@/lib/api/account';
import type { LoginData } from '@/lib/types/account';
import { ChangeEvent, useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { Dialog, DialogContent, DialogFooter } from '@/components/CustomDialog';
import IntroduceLayout from '@/components/IntroduceLayout';
import { Loader2 } from 'lucide-react';
import { useAuthToken } from '@/hooks/useAuthToken';
import { Spinner } from '@/components/ui/spinner';
import PasswordField from '@/components/PasswordField';
import { REG_PHONE } from '@/lib/regex';
import { useVerificationPortOne } from '@/hooks/useVerificationPortOne';

export default function LoginPage() {
	const [phoneNumber, setPhoneNumber] = useState('');
	const [password, setPassword] = useState('');
	const [isErrorPhoneNumber, setisErrorPhoneNumber] = useState(false);
	const [isOpen, setIsOpen] = useState(false);

	const router = useRouter();

	const { isReady } = useAuthToken({ redirectIfLoggedIn: true });

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
			toast('로그인 실패: ' + (error?.message || '알 수 없는 오류'));
		},
	});

	const onChangePhonnumber = (e: ChangeEvent<HTMLInputElement>) => {
		let value = e.target.value;

		value = value.replace(/[^0-9]/g, '');
		REG_PHONE.test(value)
			? setisErrorPhoneNumber(false)
			: setisErrorPhoneNumber(true);

		setPhoneNumber(value);
	};

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		if (!phoneNumber) {
			toast('휴대폰 번호를 입력해 주세요.');
			return;
		}
		if (!password) {
			toast('비밀번호를 입력해 주세요.');
			return;
		}
		loginMutation.mutate({ phoneNumber, password });
	};

	const handleSignup = (e: React.FormEvent) => {
		e.preventDefault();
		router.push('/signup');
	};

	const changePassword = (e: React.FormEvent) => {
		e.preventDefault();
		// port one 본인 인증 절차 진행
		useVerificationPortOne({ redirectUrl: '/change-password' });
	};

	if (!isReady) {
		return (
			<div className="min-h-screen flex items-center justify-center">
				<Spinner />
			</div>
		);
	}

	return (
		<>
			<Head>
				<title>로그인 | KEP</title>
			</Head>

			<div className="flex items-center justify-center p-5 bg-white">
				<div className="w-full max-w-lg mx-auto">
					{/* 로고 */}
					<h1 className="flex flex-col items-center mt-20 mb-25">
						<Image
							src="/img/main-logo.svg"
							alt="KEP 로고"
							width={148}
							height={55}
						/>
					</h1>

					{/* 입력 폼 */}
					<form className="space-y-4" onSubmit={handleSubmit}>
						<div>
							<label className="text-sm">휴대폰 번호</label>
							<Input
								placeholder="휴대폰 번호를 입력해 주세요."
								value={phoneNumber}
								className="h-10"
								onChange={(e) => onChangePhonnumber(e)}
							/>
							{isErrorPhoneNumber ? (
								<p className="text-xs text-red-600 pt-1 pl-1">
									정확한 휴대폰 번호를 입력해주세요. 예) 01012345678
								</p>
							) : (
								<p className="text-xs text-muted-foreground pt-1 pl-1">
									숫자만 입력해주세요. 예) 01012345678
								</p>
							)}
						</div>
						<div>
							<PasswordField
								label="비밀번호"
								placeholder="비밀번호를 입력해주세요."
								value={password}
								onChange={(e) => setPassword(e.target.value)}
							/>
						</div>

						{/* 로그인 버튼 */}
						<Button
							type="submit"
							className="w-full bg-blue-900 hover:bg-blue-800 text-white h-10"
							disabled={loginMutation.isPending}
						>
							{loginMutation.isPending && (
								<Loader2 className="h-4 w-4 animate-spin" />
							)}
							로그인
						</Button>

						{/* 회원가입 버튼 */}
						<Button
							onClick={handleSignup}
							className="w-full bg-blue-100 text-blue-900 hover:bg-blue-200 h-10"
							disabled={loginMutation.isPending}
						>
							회원가입
						</Button>
					</form>

					<div className="flex flex-col items-center my-6">
						<Button
							variant="ghost"
							className="px-4 h-7"
							onClick={changePassword}
						>
							비밀번호 찾기
						</Button>
					</div>

					<div className="flex flex-col items-center mt-8">
						<Button
							variant="outline"
							className="py-2 px-4 mt-4 h-10"
							onClick={() => setIsOpen(true)}
						>
							KEP는 어떤 서비스 인가요?
						</Button>
					</div>

					<Dialog open={isOpen} onOpenChange={setIsOpen}>
						<DialogContent className="bg-slate-100 px-5 py-6 w-[calc(100%-40px)] max-h-[calc(100vh-60px)] overflow-y-scroll">
							<IntroduceLayout />
							<DialogFooter>
								<Button
									className="w-full bg-blue-900 text-white hover:bg-blue-800"
									onClick={() => setIsOpen(false)}
								>
									확인
								</Button>
							</DialogFooter>
						</DialogContent>
					</Dialog>
				</div>
			</div>
		</>
	);
}
