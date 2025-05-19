import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import Head from 'next/head';
import Image from 'next/image';

export default function MyPage() {
	return (
		<>
			<Head>
				<title>마이페이지 | KEP</title>
			</Head>

			<div className="min-h-screen bg-stale-100 px-4 py-6">
				<div className="w-full max-w-md mx-auto p-6 space-y-6">
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

					{/* 휴대폰 번호 */}
					<div>
						<label className="text-sm font-medium">휴대폰 번호</label>
						<Input value="010-1234-****" disabled className="bg-white" />
					</div>

					{/* 가입일 */}
					<div>
						<label className="text-sm font-medium">가입일</label>
						<Input value="2025-05-04" disabled className="bg-white" />
					</div>

					{/* 비밀번호 변경 */}
					<div className="flex gap-2">
						<div className="flex-1">
							<label className="text-sm font-medium">비밀번호</label>
							<Input
								type="password"
								value="******"
								disabled
								className="bg-white"
							/>
						</div>
						<Button className="mt-6 bg-gray-900 text-white hover:bg-gray-800">
							비밀번호 변경
						</Button>
					</div>
				</div>
			</div>
		</>
	);
}
