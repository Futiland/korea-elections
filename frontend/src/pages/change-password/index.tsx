import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import Head from 'next/head';
import { ChangeEvent, useEffect, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import PasswordField from '@/components/PasswordField';
import { useRouter } from 'next/router';
import { Loader2 } from 'lucide-react';
import type { ChangePasswordData } from '@/lib/types/account';
import { changePassword } from '@/lib/api/account';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';

export default function ChangePasswordPage() {
	const router = useRouter();
	const { showDialog, hideDialog } = useAlertDialog();

	const [changeData, setChangeData] = useState({
		verificationId: '',
		password: '',
		confirmPassword: '',
	});

	const changePasswordMutation = useMutation({
		mutationFn: (data: ChangePasswordData) => changePassword(data),
		onSuccess: (res) => {
			// router.push('/login');
			showDialog({
				message: '비밀번호 변경이 완료되었습니다.',
				discription: '변경된 비밀번호로 로그인 해주세요.',
				actions: (
					<Button
						className="w-full bg-blue-900 text-white"
						onClick={() => {
							router.push('/login');
							hideDialog();
						}}
					>
						확인
					</Button>
				),
			});
		},
		onError: (error: any) => {
			toast('비밀번호 변경 실패: ' + (error?.message || '알 수 없는 오류'));
		},
	});

	const onChangeInput = (e: ChangeEvent<HTMLInputElement>, key: string) => {
		let value = e.target.value;

		setChangeData({
			...changeData,
			[key]: value,
		});
	};

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();

		const params: ChangePasswordData = {
			verificationId: changeData.verificationId,
			password: changeData.password,
		};

		if (!changeData.verificationId) {
			toast('본인인증을 먼저 진행해 주세요.');
			router.push('/login');
			return;
		}

		if (!changeData.password || !changeData.confirmPassword) {
			toast('비밀번호를 입력해 주세요.');
			return;
		}

		if (changeData.password !== changeData.confirmPassword) {
			toast('비밀번호가 일치하지 않습니다.');
			return;
		}

		changePasswordMutation.mutate(params);
	};

	console.log('changeData', changeData);

	useEffect(() => {
		if (router.isReady) {
			const { identityVerificationId } = router.query;

			if (typeof identityVerificationId === 'string') {
				setChangeData((prev) => ({
					...prev,
					verificationId: identityVerificationId,
				}));
			}
		}
	}, [router.isReady, router.query]);

	return (
		<>
			<Head>
				<title>비밀번호 재설정</title>
			</Head>

			<div className="flex min-h-screen py-7 bg-white px-5">
				<div className="w-full max-w-lg mx-auto">
					<h1 className="text-xl font-bold mb-6">비밀번호 재설정</h1>
					<form className="space-y-4" onSubmit={handleSubmit}>
						{/* <div className=" ">
							<label className="text-sm">휴대폰 번호</label>
							<Input
								placeholder="휴대폰 번호를 입력해 주세요."
								className="h-10"
								required
								value={''}
								disabled
							/>
						</div> */}

						<PasswordField
							label="새 비밀번호"
							placeholder="비밀번호를 입력해주세요."
							value={changeData.password}
							onChange={(e) => onChangeInput(e, 'password')}
						/>
						<div>
							<PasswordField
								label="비밀번호 확인"
								placeholder="비밀번호를 입력해주세요."
								value={changeData.confirmPassword}
								onChange={(e) => onChangeInput(e, 'confirmPassword')}
							/>
							{changeData.password !== changeData.confirmPassword && (
								<p className="text-xs text-red-600 pt-1 pl-1">
									비밀번호가 일치하지 않습니다.
								</p>
							)}
						</div>

						<Button
							className="w-full bg-blue-900 text-white hover:bg-blue-800 h-10 mt-8"
							type="submit"
							disabled={changePasswordMutation.isPending}
						>
							{changePasswordMutation.isPending && (
								<Loader2 className="h-4 w-4 animate-spin" />
							)}
							비밀번호 변경
						</Button>
					</form>
				</div>
			</div>
		</>
	);
}
