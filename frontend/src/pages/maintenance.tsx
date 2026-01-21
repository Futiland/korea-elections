import Head from 'next/head';
import { Wrench, Clock, Mail } from 'lucide-react';
import { Card } from '@/components/ui/card';

export default function MaintenancePage() {
	return (
		<>
			<Head>
				<title>점검 중 | KEP</title>
				<meta name="robots" content="noindex, nofollow" />
			</Head>

			<div className="min-h-screen flex items-center justify-center px-4">
				<Card className="w-full max-w-lg p-8 md:p-12 text-center space-y-6">
					{/* 아이콘 */}
					<div className="flex justify-center">
						<div className="flex items-center justify-center size-20 rounded-full bg-blue-100">
							<Wrench className="size-10 text-blue-600" />
						</div>
					</div>

					{/* 제목 */}
					<div className="space-y-2">
						<h1 className="text-2xl md:text-3xl font-bold text-slate-900">
							서비스 점검 중입니다
						</h1>
						<p className="text-sm md:text-base text-slate-600">
							더 나은 서비스를 제공하기 위해 시스템 점검을 진행하고 있습니다.
						</p>
					</div>

					{/* 점검 정보 */}
					<div className="space-y-4 pt-4 border-t border-slate-200">
						<div className="flex items-center justify-center gap-2 text-slate-700">
							<Clock className="size-5 text-slate-500" />
							<span className="text-sm font-medium">
								점검 예상 시간: 약 1-2시간
							</span>
						</div>

						<div className="bg-blue-50 rounded-lg p-4 text-left space-y-2">
							<p className="text-xs font-semibold text-blue-900 mb-2">
								점검 내용
							</p>
							<ul className="text-xs text-blue-800 space-y-1 list-disc list-inside">
								<li>서버 성능 최적화</li>
								<li>데이터베이스 업데이트</li>
								<li>보안 강화 작업</li>
							</ul>
						</div>
					</div>

					{/* 문의 안내 */}
					<div className="pt-4 border-t border-slate-200">
						<div className="flex items-center justify-center gap-2 text-slate-600 text-sm">
							<Mail className="size-4" />
							<span>문의사항이 있으시면 고객센터로 연락해주세요.</span>
						</div>
					</div>

					{/* 안내 메시지 */}
					<div className="pt-2">
						<p className="text-xs text-slate-500">
							점검이 완료되면 자동으로 서비스가 재개됩니다.
							<br />
							불편을 드려 죄송합니다.
						</p>
					</div>
				</Card>
			</div>
		</>
	);
}
