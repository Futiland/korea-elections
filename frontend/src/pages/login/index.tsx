import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import Image from 'next/image';
import Head from 'next/head';

export default function LoginPage() {
	return (
		<>
			<Head>
				<title>로그인 | KEP</title>
			</Head>

			<div className="min-h-screen flex items-center justify-center px-4 bg-white">
				<div className="w-full max-w-sm  p-6 rounded-md">
					{/* 로고 */}
					<div className="flex flex-col items-center mb-6">
						<Image
							src="/img/logo-c.svg" // public 폴더 기준
							alt="KEP 로고"
							width={80}
							height={80}
							className="mb-2"
						/>
						<h1 className="text-5xl font-bold">KEP</h1>
					</div>

					{/* 입력 폼 */}
					<form className="space-y-4">
						<div>
							<label className="text-sm font-medium">휴대폰 번호</label>
							<Input placeholder="휴대폰 번호를 입력해 주세요." />
						</div>
						<div>
							<label className="text-sm font-medium">비밀번호</label>
							<Input type="password" placeholder="비밀번호를 입력해 주세요." />
						</div>

						{/* 버튼 */}
						<Button className="w-full bg-blue-900 hover:bg-blue-800 text-white">
							로그인
						</Button>

						<Button className="w-full bg-blue-100 text-blue-900 hover:bg-blue-200">
							회원가입
						</Button>

						<div className="flex flex-col items-center ">
							<Button variant="outline" className="py-2 px-4 mt-22">
								KEP는 어떤 서비스 인가요 ?
							</Button>
						</div>
					</form>
				</div>
			</div>
		</>
	);
}
