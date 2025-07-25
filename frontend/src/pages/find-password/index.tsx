import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import Head from 'next/head';
import { ChangeEvent, useEffect, useState } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import PasswordField from '@/components/PasswordField';
import { REG_PHONE } from '@/lib/regex';
import { Loader2 } from 'lucide-react';

export default function FindPasswordPage() {
	const [isErrorPhoneNumber, setisErrorPhoneNumber] = useState(false);

	const onChangeInput = (e: ChangeEvent<HTMLInputElement>, key: string) => {
		let value = e.target.value;

		if (key === 'phoneNumber') {
			value = value.replace(/[^0-9]/g, '');
			REG_PHONE.test(value)
				? setisErrorPhoneNumber(false)
				: setisErrorPhoneNumber(true);
		}

		// setUserInfo({
		// 	...userInfo,
		// 	[key]: value,
		// });
	};

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();

		// if (!userInfo.password || !userInfo.confirmPassword) {
		// 	toast('비밀번호를 입력해 주세요.');
		// 	return;
		// }

		if (isErrorPhoneNumber) {
			toast('비밀번호를 확인해 주세요.');
			return;
		}
	};

	return (
		<>
			<Head>
				<title>비밀번호 찾기</title>
			</Head>

			<div className="flex min-h-screen py-7 bg-white">
				<div className="w-full max-w-lg mx-auto">
					<h1 className="text-xl font-bold mb-6">비밀번호 재설정</h1>
					<form className="space-y-4" onSubmit={handleSubmit}>
						<div className=" ">
							<label className="text-sm">휴대폰 번호</label>
							<Input
								placeholder="휴대폰 번호를 입력해 주세요."
								className="h-10"
								required
								value={''}
								disabled
							/>
						</div>

						<PasswordField
							label="새 비밀번호"
							placeholder="비밀번호를 입력해주세요."
							value={''}
							onChange={(e) => onChangeInput(e, 'password')}
						/>
						<div>
							<PasswordField
								label="비밀번호 확인"
								placeholder="비밀번호를 입력해주세요."
								value={''}
								onChange={(e) => onChangeInput(e, 'confirmPassword')}
							/>
							{/* {userInfo.password !== userInfo.confirmPassword && (
								<p className="text-xs text-red-600 pt-1 pl-1">
									비밀번호가 일치하지 않습니다.
								</p>
							)} */}
						</div>

						<Button
							className="w-full bg-blue-900 text-white hover:bg-blue-800 h-10 mt-8"
							type="submit"
							// disabled={
							// 	signupMutation.isPending || stopper?.data.status === 'ACTIVE'
							// }
						>
							{/* {signupMutation.isPending && (
							<Loader2 className="h-4 w-4 animate-spin" />
						)} */}
							비밀번호 변경
						</Button>
					</form>
				</div>
			</div>
		</>
	);
}
