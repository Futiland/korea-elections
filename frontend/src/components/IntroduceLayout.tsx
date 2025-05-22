export default function IntroduceLayout() {
	return (
		<div className="w-full ">
			<div className="text-center mb-5">
				<h2 className="text-lg font-semibold mb-5">
					KEP는 어떤 서비스 인가요 ?
				</h2>
				<p className="text-sm text-gray-500 font-semibold">
					“우리는 선거의 공정성과 시민의 의견을 최우선으로 생각합니다. 신뢰할 수
					있는 연구를 위해 철저하게 설계된 프로젝트입니다.”
				</p>
			</div>

			<div className="space-y-5">
				<div className="bg-white p-4 rounded-md shadow-md">
					<h3 className="text-sm font-semibold mb-1">
						개인정보는 제3자에게 절대 제공되지 않습니다.
					</h3>
					<p className="text-sm text-muted-foreground">
						본 조사는 완전한 익명으로 진행되며, 수집된 정보는 오직 연구 목적에
						한하여 사용됩니다. 개인정보는 저장되지 않으며, 제3자에게 절대
						제공되지 않습니다.
					</p>
				</div>

				<div className="bg-white p-4 rounded-md shadow-md">
					<h3 className="text-sm font-semibold mb-1">
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
	);
}
