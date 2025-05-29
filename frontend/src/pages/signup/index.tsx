import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import Head from 'next/head';
import { ChangeEvent, useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useMutation } from '@tanstack/react-query';
import { signup } from '@/lib/api/account';
import type { SignupRequestData } from '@/lib/types/account';
import IntroduceLayout from '@/components/IntroduceLayout';
import { Loader2 } from 'lucide-react';
import { useAuthToken } from '@/hooks/useAuthToken';
import { Spinner } from '@/components/ui/spinner';
import * as PortOne from '@portone/browser-sdk/v2';
import PasswordField from '@/components/PasswordField';
import { SignupInputData } from '@/lib/types/account';
import { REG_PHONE } from '@/lib/regex';

export default function SignupPage() {
	const router = useRouter();

	const { isReady } = useAuthToken({ redirectIfLoggedIn: true });

	const redirectPath =
		typeof router.query.redirect === 'string' ? router.query.redirect : '/';

	const [useInfo, setUseInfo] = useState<SignupInputData>({
		phoneNumber: '',
		password: '',
		confirmPassword: '',
	});

	const [isErrorPhoneNumber, setisErrorPhoneNumber] = useState(false);
	const [identityVerificationId, setIdentityVerificationId] = useState('');

	const signupMutation = useMutation({
		mutationFn: (data: SignupRequestData) => signup(data),
		onSuccess: (res) => {
			localStorage.setItem('token', res.data.token);
			localStorage.removeItem('userInfo_phone');
			toast('회원가입이 완료되었습니다.');
			router.push(redirectPath);
		},
		onError: (error: any) => {
			toast('회원가입 실패: ' + (error?.message || '알 수 없는 오류'));
		},
	});

	const requestCertification = (e: React.FormEvent) => {
		e.preventDefault();
		localStorage.setItem('userInfo_phone', useInfo.phoneNumber);

		PortOne.requestIdentityVerification({
			storeId: process.env.NEXT_PUBLIC_PORTONE_STORE_ID!, // 필수
			identityVerificationId: `test_${Date.now()}_${Math.random()
				.toString(36)
				.substring(2, 8)}`, // 본인인증 ID
			channelKey: process.env.NEXT_PUBLIC_PORTONE_CHANNEL_KEY!, // 필수
			redirectUrl: `${window.location.origin}/signup`, // 필수
			redirect: true,
			windowType: {
				pc: 'REDIRECTION',
			},
			onSuccess: (res: PortOne.IdentityVerificationResponse) => {
				console.log('onSuccess', res);
			},
		} as PortOne.IdentityVerificationRequest);
	};

	const onChangeInput = (e: ChangeEvent<HTMLInputElement>, key: string) => {
		let value = e.target.value;

		if (key === 'phoneNumber') {
			value = value.replace(/[^0-9]/g, '');
			REG_PHONE.test(value)
				? setisErrorPhoneNumber(false)
				: setisErrorPhoneNumber(true);
		}

		setUseInfo({
			...useInfo,
			[key]: value,
		});
	};

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		if (!useInfo.phoneNumber) {
			toast('휴대폰 번호를 입력해 주세요.');
			return;
		}
		if (!identityVerificationId) {
			toast.error('본인 인증을 완료해 주세요');
			return;
		}
		if (!useInfo.password || !useInfo.confirmPassword) {
			toast('비밀번호를 입력해 주세요.');
			return;
		}
		if (isErrorPhoneNumber) {
			toast('비밀번호를 확인해 주세요.');
			return;
		}

		signupMutation.mutate({
			phoneNumber: useInfo.phoneNumber,
			password: useInfo.password,
			verificationId: identityVerificationId,
			verificationType: 'MOBILE',
		});
	};

	useEffect(() => {
		if (router.isReady) {
			const query = new URLSearchParams(window.location.search);
			const id = query.get('identityVerificationId');

			const phoneNumber = localStorage.getItem('userInfo_phone');
			if (id) {
				setIdentityVerificationId(id);
			}
			if (phoneNumber) {
				setUseInfo({
					...useInfo,
					phoneNumber,
				});
			}
		}
	}, [router.isReady]);

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
				<title>회원가입 | KEP</title>
			</Head>

			<div>
				<div className="bg-white py-6 px-4">
					<div className="w-full max-w-lg mx-auto">
						{/* 로고 + KEP ? 버튼은 Header 컴포넌트에서 이미 처리 */}
						<h1 className="text-xl font-bold mb-6">회원가입</h1>

						{/* 가입 폼 */}
						<form className="space-y-4 mb-8" onSubmit={handleSubmit}>
							<div>
								<label className="text-sm">휴대폰 번호</label>
								<div className="flex items-center justify-between space-x-2 ">
									<Input
										placeholder="휴대폰 번호를 입력해 주세요."
										className="h-10"
										required
										value={useInfo.phoneNumber}
										onChange={(e) => onChangeInput(e, 'phoneNumber')}
									/>

									<Button
										className=" bg-blue-100 text-blue-900 hover:bg-blue-200 h-10"
										disabled={
											signupMutation.isPending || identityVerificationId !== ''
										}
										onClick={requestCertification}
									>
										{identityVerificationId ? '인증 완료' : '본인 인증'}
									</Button>
								</div>
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
							{identityVerificationId && (
								<>
									<PasswordField
										label="비밀번호"
										placeholder="비밀번호를 입력해주세요."
										value={useInfo.password}
										onChange={(e) => onChangeInput(e, 'password')}
									/>
									<div>
										<PasswordField
											label="비밀번호 확인"
											placeholder="비밀번호를 한번 더 입력해주세요."
											value={useInfo.confirmPassword}
											onChange={(e) => onChangeInput(e, 'confirmPassword')}
										/>
										{useInfo.password !== useInfo.confirmPassword && (
											<p className="text-xs text-red-600 pt-1 pl-1">
												비밀번호가 일치하지 않습니다.
											</p>
										)}
									</div>
								</>
							)}
							<Button
								className="w-full bg-blue-900 text-white hover:bg-blue-800 h-10"
								type="submit"
								disabled={signupMutation.isPending}
							>
								{signupMutation.isPending && (
									<Loader2 className="h-4 w-4 animate-spin" />
								)}
								가입하기
							</Button>
						</form>
					</div>
				</div>

				{/* 정보 안내 영역 */}
				<div className="max-w-lg mx-auto py-8 px-7 bg-slate-100">
					<IntroduceLayout />
				</div>
			</div>
		</>
	);
}
