import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import Head from 'next/head';

export default function SignupPage() {
	return (
		<>
			<Head>
				<title>회원가입 | KEP</title>
			</Head>

			<div className="min-h-screen ">
				<div className="bg-white px-4 py-6">
					<div className="w-full max-w-md mx-auto  p-6">
						{/* 로고 + KEP ? 버튼은 Header 컴포넌트에서 이미 처리 */}
						<h1 className="text-xl font-bold mb-6">회원가입</h1>

						{/* 가입 폼 */}
						<form className="space-y-4 mb-8">
							<div>
								<label className="text-sm font-medium">휴대폰 번호</label>
								<Input placeholder="휴대폰 번호를 입력해 주세요." />
							</div>
							<div>
								<label className="text-sm font-medium">비밀번호</label>
								<Input type="password" placeholder="비밀번호를 입력해주세요." />
							</div>
							<Button className="w-full bg-blue-900 text-white hover:bg-blue-800">
								가입하기
							</Button>
						</form>
					</div>
				</div>

				{/* 정보 안내 영역 */}
				<div className="w-full max-w-md mx-auto mt-6 space-y-5 bg-slate-100">
					<div className="text-center ">
						<h2 className="text-lg font-semibold mb-1 py-7.5">
							KEP는 어떤 서비스 인가요 ?
						</h2>
						<p className="text-md text-gray-500">
							“우리는 선거의 공정성과 시민의 의견을 최우선으로 생각합니다.
							신뢰할 수 있는 연구를 위해 철저하게 설계된 프로젝트입니다.”
						</p>
					</div>

					<div className="bg-white p-4 rounded-md shadow-sm">
						<h3 className="font-semibold mb-1">
							개인정보는 제3자에게 절대 제공되지 않습니다.
						</h3>
						<p className="text-sm text-muted-foreground">
							본 조사는 완전한 익명으로 진행되며, 수집된 정보는 오직 연구 목적에
							한하여 사용됩니다. 개인정보는 저장되지 않으며, 제3자에게 절대
							제공되지 않습니다.
						</p>
					</div>

					<div className="bg-white p-4 rounded-md shadow-sm">
						<h3 className="font-semibold mb-1">
							어떤 정보도 외부에 제공되지 않습니다.
						</h3>
						<p className="text-sm text-muted-foreground">
							Korea Elections Project는 익명으로 참여할 수 있으며, 어떤 정보도
							외부에 제공되지 않습니다. 공정하고 안전한 선거 문화 확산을 위해
							만들어진 시스템입니다.
						</p>
					</div>
				</div>
			</div>
		</>
	);
}
