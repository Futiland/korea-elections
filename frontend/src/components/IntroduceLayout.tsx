export default function IntroduceLayout() {
	return (
		<div className="w-full">
			<div className="text-center mb-5">
				<h2 className="text-lg font-semibold mb-5">
					“간단한 인증으로, 중요한 의견에 힘을 실어주세요 test”
				</h2>
				<div className="space-y-1">
					<p className="text-sm text-gray-500 font-semibold">
						KEP는 대통령 선거를 넘어, 국회의원 선거, 정책 결정, 사회적 쟁점 등
						한국 사회의 중요한 이슈에 대해 시민이 직접 참여하고, 투표하고,
						확인할 수 있는 시민참여형 플랫폼입니다.
					</p>
					<p className="text-sm text-gray-500 font-semibold">
						간단한 본인 인증을 통해 누구나 자신이 직접 내린 선택을 남기고,
						투명하고 신뢰할 수 있는 데이터로 민심을 함께 만들어갑니다.
					</p>
					<p className="text-sm text-gray-500 font-semibold">
						언론이나 정치권, 포털이 주도하던 정보에서 벗어나 시민이 주체가 되는
						공론장을 만드는 것, 그것이 KEP의 시작이자 목적입니다.
					</p>
					<p className="text-sm text-gray-500 font-semibold">
						이 프로젝트는 단순한 투표 시스템이 아니라, “우리 사회의 생각과
						흐름을 기록하고 함께 방향을 찾아가는 디지털 민심 실험”입니다.
					</p>
				</div>
			</div>

			<div className="space-y-5">
				<div className="bg-white p-4 rounded-md shadow-md">
					<h3 className="text-sm font-semibold mb-1">
						지금, 어떤 이슈에 관심을 가지고 있나요?
					</h3>
					<p className="text-sm text-muted-foreground">
						KEP에서는 지금 한국 사회 이슈의 민심을 직접 눈으로 확인해 볼수
						있습니다. 본인 인증을 거친 참여만 반영되기 때문에, 단순한 설문이
						아닌 신뢰도 높은 민심 데이터를 제공합니다. 여러분의 의견 또한 전체
						민심 흐름 안에서 하나의 의미 있는 데이터로 반영됩니다. 나의 입장과
						사회 전체의 민심 흐름을 함께 확인해보세요. 이제, 민심은 감이 아닌
						데이터입니다!
					</p>
				</div>

				<div className="bg-white p-4 rounded-md shadow-md">
					<h3 className="text-sm font-semibold mb-1">
						여론조사 없이도 민심을 확인할 수 있습니다.
					</h3>
					<p className="text-sm text-muted-foreground">
						복잡한 여론조사 전화에 응답할 필요 없이, 간단하고 명확한 방식으로
						참여할 수 있습니다.
					</p>
				</div>

				<div className="bg-white p-4 rounded-md shadow-md">
					<h3 className="text-sm font-semibold mb-1">
						개인정보는 제3자에게 절대 제공되지 않습니다.
					</h3>
					<p className="text-sm text-muted-foreground">
						본인 인증은 신뢰 있는 데이터 수집을 위한 최소한의 절차입니다.
						참여자는 익명으로 기록되며, 어떤 경우에도 개인정보는 외부로 제공되지
						않습니다.
					</p>
				</div>
			</div>
		</div>
	);
}
