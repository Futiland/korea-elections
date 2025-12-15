import Head from 'next/head';

export default function OpinionPoll() {
	return (
		<>
			<Head>
				<title>여론조사 투표</title>
			</Head>
			<div className="min-h-screen bg-slate-50">
				<div className="max-w-4xl mx-auto px-4 py-8">
					{/* 페이지 제목 */}
					<div className="mb-8">
						<h1 className="text-3xl font-bold text-slate-900 mb-2">
							여론조사 투표
						</h1>
						<p className="text-slate-600">여러분의 소중한 의견을 들려주세요.</p>
					</div>

					<p className="text-slate-600 text-center py-12">준비중입니다.</p>
				</div>
			</div>
		</>
	);
}
