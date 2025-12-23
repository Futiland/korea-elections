export default function IntroduceLayout() {
	return (
		<div className="w-full space-y-6 py-2">
			{/* 헤더 섹션 */}
			<div className="text-center space-y-1 pb-2">
				<h1 className=" text-slate-900">
					<span className="text-blue-800 text-xl font-bold">KEP</span>
					<span className="text-slate-900 font-normal">
						{' '}
						– 시민이 만드는 여론조사
					</span>
				</h1>
				<p className="text-lg font-semibold text-blue-800 py-2">
					"참여해야 보인다"
				</p>
				<p className="text-sm text-slate-600 leading-relaxed ">
					KEP는 본인 인증된 시민만 참여하는
					<br className="block sm:hidden" /> 여론조사 플랫폼입니다.
					<br />
					투표에 참여한 사람만 결과를 확인할 수 있습니다.
				</p>
			</div>

			{/* 세 가지 원칙 */}
			<div className="space-y-2.5">
				<h2 className="text-base font-bold text-slate-900">세 가지 원칙</h2>

				<div className="bg-white border border-slate-200 rounded-md p-4 space-y-4">
					{/* 원칙 1 */}
					<div>
						<h3 className="text-sm font-bold text-blue-900 mb-1.5">
							1. 검증된 참여
						</h3>
						<ul className="space-y-1 text-xs text-slate-700">
							<li className="flex items-start">
								<span className="mr-1.5">•</span>
								<span>본인인증을 거친 만 18세 이상 내국인만 참여합니다.</span>
							</li>
							<li className="flex items-start">
								<span className="mr-1.5">•</span>
								<span>1인 1표, 중복 참여는 불가능합니다.</span>
							</li>
						</ul>
					</div>

					<div className="border-t border-slate-200"></div>

					{/* 원칙 2 */}
					<div>
						<h3 className="text-sm font-bold text-green-900 mb-1.5">
							2. 참여자만 결과 확인
						</h3>
						<ul className="space-y-1 text-xs text-slate-700">
							<li className="flex items-start">
								<span className="mr-1.5">•</span>
								<span>투표해야 결과를 볼 수 있습니다.</span>
							</li>
						</ul>
					</div>

					<div className="border-t border-slate-200"></div>

					{/* 원칙 3 */}
					<div>
						<h3 className="text-sm font-bold text-purple-900 mb-1.5">
							3. 시민이 질문을 만든다
						</h3>
						<ul className="space-y-1 text-xs text-slate-700">
							<li className="flex items-start">
								<span className="mr-1.5">•</span>
								<span>궁금한 이슈가 있다면 직접 여론조사를 만드세요.</span>
							</li>
							<li className="flex items-start">
								<span className="mr-1.5">•</span>
								<span>당신의 질문에 시민이 응답합니다.</span>
							</li>
						</ul>
					</div>
				</div>
			</div>

			{/* 기존 여론조사와 다른 점 */}
			<div className="space-y-2.5">
				<h2 className="text-base font-bold text-slate-900r">
					기존 여론조사와 다른 점
				</h2>

				<div className="bg-white border border-slate-200 rounded-md overflow-hidden">
					<div className="overflow-x-auto">
						<table className="w-full text-xs">
							<thead>
								<tr className="bg-slate-200 border-b border-slate-200">
									<th className="px-3 py-2 text-left font-semibold text-slate-700">
										구분
									</th>
									<th className="px-3 py-2 text-left font-semibold text-slate-700">
										기존 여론조사
									</th>
									<th className="px-3 py-2 text-left font-semibold text-blue-800">
										KEP
									</th>
								</tr>
							</thead>
							<tbody>
								<tr className="border-b border-slate-100">
									<td className="px-3 py-2 font-medium text-slate-700 min-w-[45px]">
										표본
									</td>
									<td className="px-3 py-2 text-slate-600">무작위 전화 표본</td>
									<td className="px-3 py-2 text-blue-800 font-medium">
										직접 참여한 시민
									</td>
								</tr>
								<tr className="border-b border-slate-100">
									<td className="px-3 py-2 font-medium text-slate-700">질문</td>
									<td className="px-3 py-2 text-slate-600">업체가 만든 질문</td>
									<td className="px-3 py-2 text-blue-800 font-medium">
										시민이 직접 질문
									</td>
								</tr>
								<tr>
									<td className="px-3 py-2 font-medium text-slate-700">결과</td>
									<td className="px-3 py-2 text-slate-600">
										매체가 결과를 전달
									</td>
									<td className="px-3 py-2 text-blue-800 font-medium">
										실시간으로 참여자가 직접 확인
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>

			{/* 익명성 보장 */}
			<div className="space-y-2.5">
				<h2 className="text-base font-bold text-slate-900">익명성 보장</h2>

				<div className="bg-slate-50 border border-slate-200 rounded-md p-3">
					<ul className="space-y-1.5 text-xs text-slate-700">
						<li className="flex items-start">
							<span className="mr-1.5 text-blue-600 font-bold">✓</span>
							<span>투표 기록에 개인 정보 미포함</span>
						</li>
						<li className="flex items-start">
							<span className="mr-1.5 text-blue-600 font-bold">✓</span>
							<span>누가 어떤 선택을 했는지 추적 불가</span>
						</li>
						<li className="flex items-start">
							<span className="mr-1.5 text-blue-600 font-bold">✓</span>
							<span>개인 정보 암호화</span>
						</li>
					</ul>
				</div>
			</div>

			{/* CTA */}
			<div className="py-2 text-center space-y-1.5">
				<h3 className="text-base font-bold text-blue-800">
					지금, 어떤 이슈가 궁금하세요?
				</h3>
				<p className="text-sm text-slate-700">
					직접 질문을 만들거나, 진행 중인 투표에 참여해보세요.
				</p>
			</div>
		</div>
	);
}
