import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import Head from 'next/head';
import { ChangeEvent, useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useMutation, useQuery } from '@tanstack/react-query';
import { signup, signupStopper } from '@/lib/api/account';
import type {
	SignupRequestData,
	SignupStopperResponse,
} from '@/lib/types/account';
import IntroduceLayout from '@/components/IntroduceLayout';
import { Loader2 } from 'lucide-react';
import { useAuthToken } from '@/hooks/useAuthToken';
import { Spinner } from '@/components/ui/spinner';
import * as PortOne from '@portone/browser-sdk/v2';
import PasswordField from '@/components/PasswordField';
import { SignupInputData } from '@/lib/types/account';
import { REG_PHONE } from '@/lib/regex';
import Footer from '@/components/Footer';
import TermsDialog from './TermsDialog';

export default function SignupPage() {
	const router = useRouter();

	const { isReady } = useAuthToken({ redirectIfLoggedIn: true });

	const redirectPath =
		typeof router.query.redirect === 'string' ? router.query.redirect : '/';

	const [userInfo, setUserInfo] = useState<SignupInputData>({
		phoneNumber: '',
		password: '',
		confirmPassword: '',
		terms: false,
	});

	const [isErrorPhoneNumber, setisErrorPhoneNumber] = useState(false);
	const [identityVerificationId, setIdentityVerificationId] = useState('');
	const [isVisibleTermsDialog, setIsVisibleTermsDialog] = useState(false);

	const {
		data: stopper,
		isFetching,
		isError,
	} = useQuery({
		queryKey: ['getStopper'],
		queryFn: signupStopper,
		refetchOnWindowFocus: false,
		retry: 2,
	});

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

		// 회원가입 및 본인인증 일시 정지
		if (stopper?.data.status === 'ACTIVE') {
			toast(stopper?.data.message);
			return;
		}

		localStorage.setItem('userInfo_phone', userInfo.phoneNumber);

		PortOne.requestIdentityVerification({
			storeId: process.env.NEXT_PUBLIC_PORTONE_STORE_ID!, // 필수
			identityVerificationId: `verificationId_${Date.now()}_${Math.random()
				.toString(36)
				.substring(2, 8)}`, // 본인인증 ID
			channelKey: process.env.NEXT_PUBLIC_PORTONE_CHANNEL_KEY!, // 필수
			redirectUrl: `${window.location.origin}/signup`, // 필수
			redirect: true,
			onError: (err: PortOne.PortOneError) => {
				console.log(err);
			},
		} as PortOne.IdentityVerificationRequest)
			.then((res) => {
				// ✅ 인증 성공시 결과 반환됨 Pc

				if (!res) {
					toast.error('인증 결과를 받아오지 못했습니다.');
					return;
				}

				// 본인인증 실패 message
				if (res.message) {
					toast.error(`${res.message}`);
					localStorage.removeItem('userInfo_phone');
					return;
				}

				// 수동 리다이렉트
				const query = new URLSearchParams({
					identityVerificationId: res.identityVerificationId,
					identityVerificationTxId: res.identityVerificationTxId,
					transactionType: res.transactionType,
				}).toString();

				window.location.href = `/signup?${query}`;
			})
			.catch((err: PortOne.IdentityVerificationError) => {
				console.error('인증 실패', err);
				// 실패시 처리
			});
	};

	const onChangeInput = (e: ChangeEvent<HTMLInputElement>, key: string) => {
		let value = e.target.value;

		if (key === 'phoneNumber') {
			value = value.replace(/[^0-9]/g, '');
			REG_PHONE.test(value)
				? setisErrorPhoneNumber(false)
				: setisErrorPhoneNumber(true);
		}

		setUserInfo({
			...userInfo,
			[key]: value,
		});
	};

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		if (!userInfo.phoneNumber) {
			toast('휴대폰 번호를 입력해 주세요.');
			return;
		}
		if (!identityVerificationId) {
			toast.error('본인 인증을 완료해 주세요');
			return;
		}
		if (!userInfo.password || !userInfo.confirmPassword) {
			toast('비밀번호를 입력해 주세요.');
			return;
		}
		if (isErrorPhoneNumber) {
			toast('비밀번호를 확인해 주세요.');
			return;
		}
		if (!userInfo.terms) {
			toast('개인정보 수집·이용에 대한 동의를 확인해 주세요.');
			return;
		}
		signupMutation.mutate({
			phoneNumber: userInfo.phoneNumber,
			password: userInfo.password,
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
				setUserInfo({
					...userInfo,
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
										value={userInfo.phoneNumber}
										disabled={stopper?.data.status === 'ACTIVE'}
										onChange={(e) => onChangeInput(e, 'phoneNumber')}
									/>

									<Button
										className=" bg-blue-100 text-blue-900 hover:bg-blue-200 h-10"
										disabled={
											signupMutation.isPending ||
											identityVerificationId !== '' ||
											isErrorPhoneNumber
											// || userInfo.phoneNumber === ''
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
										value={userInfo.password}
										onChange={(e) => onChangeInput(e, 'password')}
									/>
									<div>
										<PasswordField
											label="비밀번호 확인"
											placeholder="비밀번호를 한번 더 입력해주세요."
											value={userInfo.confirmPassword}
											onChange={(e) => onChangeInput(e, 'confirmPassword')}
										/>
										{userInfo.password !== userInfo.confirmPassword && (
											<p className="text-xs text-red-600 pt-1 pl-1">
												비밀번호가 일치하지 않습니다.
											</p>
										)}
									</div>

									<div className="flex items-center gap-2">
										<label className="flex items-center gap-2">
											<input
												type="checkbox"
												checked={userInfo.terms}
												onChange={() =>
													setUserInfo((prev) => ({
														...prev,
														terms: !prev.terms,
													}))
												}
												className="w-4 h-4 border-gray-300 rounded text-blue-600 focus:ring-blue-500"
											/>
											<span className="text-sm text-gray-700">
												[필수] 개인정보 수집·이용에 대한 동의
											</span>
										</label>
										<span
											className="text-sm text-gray-500 underline cursor-pointer"
											onClick={() => setIsVisibleTermsDialog(true)}
										>
											자세히 보기
										</span>
									</div>
								</>
							)}
							<Button
								className="w-full bg-blue-900 text-white hover:bg-blue-800 h-10"
								type="submit"
								disabled={
									signupMutation.isPending || stopper?.data.status === 'ACTIVE'
								}
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
				<div className="max-w-lg mx-auto py-8 px-7 bg-slate-50">
					<IntroduceLayout />
				</div>
			</div>

			<TermsDialog
				isOpen={isVisibleTermsDialog}
				setIsOpen={setIsVisibleTermsDialog}
			/>

			<Footer />
		</>
	);
}
