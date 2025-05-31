export default function IntroduceLayout() {
	return (
		<div className="w-full">
			<div className="text-center mb-5">
				<h2 className="text-lg font-semibold mb-5">
					“21대 대통령 선거에 누구를 뽑으셨나요?”
				</h2>
				<div className="space-y-1">
					<p className="text-sm text-gray-500 font-semibold">
						KEP는 본인 인증을 통해 실제 선거와 유사한 투표 데이터를 확인할 수
						있는 시민 참여형 서비스입니다.
					</p>
					<p className="text-sm text-gray-500 font-semibold">
						언론, 포털, 정치권이 독점하던 정보에 시민이 직접 접근할 수 있도록
						하여, 투명하고 신뢰할 수 있는 선거 문화를 확산하는 기반이 됩니다.
					</p>
					<p className="text-sm text-gray-500 font-semibold">
						이 프로젝트는 시민의 정치적 선택을 되돌아보고, 더 나은 민주주의를
						위한 작지만 의미 있는 발걸음으로 시작되었습니다.
					</p>
				</div>
			</div>

			<div className="space-y-5">
				<div className="bg-white p-4 rounded-md shadow-md">
					<h3 className="text-sm font-semibold mb-1">
						실제 선거와 유사한 투표 데이터를 확인할 수 있는 서비스입니다.
					</h3>
					<p className="text-sm text-muted-foreground">
						사용자는 간단한 인증 절차만으로 자신의 선택을 되짚어볼 수 있으며,
						인증 기반의 집계 데이터를 통해 실제 민심을 스스로 확인하는 경험을 할
						수 있습니다.
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
						Korea Elections Project는 익명으로 참여할 수 있으며, 개인정보는
						어떠한 경우에도 외부에 제공되지 않습니다.
					</p>
				</div>
			</div>
		</div>
	);
}
