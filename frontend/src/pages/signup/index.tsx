import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import Head from 'next/head';
import { useState } from 'react';
import { useRouter } from 'next/router';
import { useMutation } from '@tanstack/react-query';
import { signup } from '@/lib/api/account';
import type { SignupData } from '@/lib/types/account';
import IntroduceLayout from '@/components/IntroduceLayout';

export default function SignupPage() {
	const router = useRouter();

	const redirectPath =
		typeof router.query.redirect === 'string' ? router.query.redirect : '/';

	const signupMutation = useMutation({
		mutationFn: (data: SignupData) => signup(data),
		onSuccess: () => {
			toast('회원가입이 완료되었습니다.');
			router.push(redirectPath);
		},
		onError: (error: any) => {
			toast('회원가입 실패: ' + (error?.message || '알 수 없는 오류'));
		},
	});

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		signupMutation.mutate({
			name: '채현',
			phoneNumber: '01031817072',
			password: '1122',
			gender: 'FEMALE',
			birthDate: new Date('1990-01-01'),
			ci: 'ci1235',
		});
	};

	return (
		<>
			<Head>
				<title>회원가입 | KEP</title>
			</Head>

			<div className="min-h-screen">
				<div className="bg-white py-6 px-4">
					<div className="w-full max-w-lg mx-auto">
						{/* 로고 + KEP ? 버튼은 Header 컴포넌트에서 이미 처리 */}
						<h1 className="text-xl font-bold mb-6">회원가입</h1>

						{/* 가입 폼 */}
						<form className="space-y-4 mb-8" onSubmit={handleSubmit}>
							<div>
								<label className="text-sm">휴대폰 번호</label>
								<Input placeholder="휴대폰 번호를 입력해 주세요." />
							</div>
							<div>
								<label className="text-sm">비밀번호</label>
								<Input type="password" placeholder="비밀번호를 입력해주세요." />
							</div>
							<Button
								className="w-full bg-blue-900 text-white hover:bg-blue-800 h-10"
								type="submit"
							>
								{signupMutation.isPending ? '가입하는 중...' : '가입하기'}
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
