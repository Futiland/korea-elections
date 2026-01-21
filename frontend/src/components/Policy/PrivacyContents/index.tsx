import { Divider } from '@/components/ui/divider';

export default function PrivacyContents() {
	return (
		<div className="p-6">
			{/* 헤더 */}
			<div className="mb-8">
				<h1 className="text-3xl md:text-4xl font-bold text-slate-900 mb-3">
					개인정보 처리방침
				</h1>
				<p className="text-slate-600 text-sm md:text-base">
					시행일자: 2026년 1월 21일
				</p>
			</div>

			{/* 서문 */}
			<div className="mb-8">
				<p className="text-slate-700">
					KEP 프로젝트 운영팀(이하 '운영팀')은 「개인정보 보호법」 등 관련 법령을
					준수하며, 이용자의 개인정보를 안전하게 보호하고 적법하게 처리하고자
					합니다. 이에 따라, 개인정보의 처리 목적, 수집 항목, 보유 기간, 이용자의
					권리 등을 다음과 같이 안내드립니다.
				</p>
			</div>

			<Divider className="bg-slate-200 my-8" />

			{/* 본문 섹션들 */}
			<div className="space-y-6">
				{/* 1. 개인정보의 처리 목적 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						1. 개인정보의 처리 목적
					</h2>
					<p className="text-slate-700 mb-4">
						운영팀은 다음의 목적을 위해 개인정보를 처리합니다. 수집한 개인정보는
						아래 목적 이외에는 사용되지 않으며, 이용 목적이 변경되는 경우 사전에
						이용자의 동의를 받습니다.
					</p>
					<ul className="list-disc list-inside  text-slate-700 ml-4">
						<li>본인 확인 및 회원가입, 참여자 관리</li>
						<li>유권자 자격 확인 및 중복 투표 방지</li>
						<li>유료 서비스 이용 시 결제 처리</li>
						<li>투표 결과 분석 및 통계 산출</li>
						<li>민원 처리, 문의 대응, 운영 개선</li>
					</ul>
				</section>

				<Divider className="bg-slate-200 my-6" />

				{/* 2. 수집 항목 및 수집 방법 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						2. 수집 항목 및 수집 방법
					</h2>
					<ul className="text-slate-700">
						<li>
							<span className="font-semibold">필수 항목</span>: 이름, 생년월일,
							성별, 휴대전화번호, 중복가입확인정보(CI)
						</li>
						<li>
							<span className="font-semibold">자동 수집 정보</span>: IP주소,
							브라우저/OS, 방문기록, 이용기록(검색, 투표, 열람 등), 광고 식별자
							등
						</li>
						<li>
							<span className="font-semibold">수집 방법</span>: 웹사이트 및 앱
							이용 시, 본인 인증 및 결제 시, 로그 분석 도구를 통한 자동 수집
						</li>
					</ul>
				</section>

				<Divider className="bg-slate-200 my-6" />

				{/* 3. 개인정보 수집에 대한 동의 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						3. 개인정보 수집에 대한 동의
					</h2>
					<p className="text-slate-700 ">
						이용자는 회원가입 시 개인정보 수집 및 이용에 대해 동의하게 되며,
						동의는 체크박스 및 확인 버튼 등을 통해 명시적으로 진행됩니다.
						개인정보 수집에 대한 동의 없이 서비스 이용은 제한될 수 있습니다.
					</p>
				</section>

				<Divider className="bg-slate-200 my-6" />

				{/* 4. 개인정보 보유 및 이용 기간 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						4. 개인정보 보유 및 이용 기간
					</h2>
					<ul className="list-disc list-inside text-slate-700 ml-4">
						<li>
							개인정보는 이용 목적 달성 시까지 보관하며, 탈퇴 요청 또는 보유기간
							경과 시 지체 없이 삭제합니다.
						</li>
						<li>
							관련 법령에 따라 일정 기간 보관이 필요한 경우, 해당 법령에 따라
							별도로 안전하게 보관합니다.
						</li>
					</ul>
				</section>

				<Divider className="bg-slate-200 my-6" />

				{/* 5. 개인정보 제3자 제공 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						5. 개인정보 제3자 제공
					</h2>
					<p className="text-slate-700 mb-4">
						운영팀은 이용자의 개인정보를 제3자에게 제공하지 않으며, 다음의 경우에만
						예외적으로 제공될 수 있습니다.
					</p>
					<ul className="list-disc list-inside text-slate-700 ml-4">
						<li>이용자의 사전 동의를 받은 경우</li>
						<li>법령에 따라 제공이 필요한 경우</li>
						<li>
							통계 분석 등을 위해 식별이 불가능한 형태	로 제공하는 경우
						</li>
					</ul>
				</section>

				<Divider className="bg-slate-200 my-6" />

				{/* 6. 개인정보 처리 위탁 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						6. 개인정보 처리 위탁
					</h2>
					<p className="text-slate-700 mb-4">
						서비스 운영을 위해 아래의 외부 서비스 제공업체에 일부 업무를 위탁할 수
						있습니다.
					</p>
					<ul className="list-disc list-inside text-slate-700 ml-4 mb-4">
						<li>
							<span className="font-semibold">본인 인증 서비스</span>: KCP인증,
							KG이니시스통합인증
						</li>
					</ul>
					<p className="text-slate-600 text-sm italic">
						※ 위탁사가 변경될 경우, 본 방침 또는 서비스 화면을 통해 사전 공지합니다.
					</p>
				</section>

				<Divider className="bg-slate-200 my-6" />

				{/* 7. 이용자의 권리 및 행사 방법 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						7. 이용자의 권리 및 행사 방법
					</h2>
					<p className="text-slate-700 mb-4">
						이용자는 언제든지 자신의 개인정보에 대해 다음의 권리를 행사할 수
						있습니다.
					</p>
					<ul className="list-disc list-inside text-slate-700 ml-4 mb-4">
						<li>개인정보 열람, 정정, 삭제, 처리정지 요청</li>
						<li>개인정보 수집 및 이용 동의 철회, 회원탈퇴 요청</li>
					</ul>
					<p className="text-slate-700 ">
						권리는 이메일 또는 앱 내 설정 메뉴를 통해 행사할 수 있으며, 운영팀은
						요청을 받은 후 지체 없이 필요한 조치를 취합니다.
					</p>
				</section>

				<Divider className="bg-slate-200 my-6" />

				{/* 8. 개인정보 보호 담당자 안내 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						8. 개인정보 보호 담당자 안내
					</h2>
					<p className="text-slate-700 mb-4">
						운영팀은 개인정보와 관련한 문의 사항 또는 민원을 처리하기 위하여 아래와
						같이 담당자를 지정하고 있습니다.
					</p>
					<div className="bg-slate-50 rounded-lg mb-4">
						<ul className="text-slate-700">
							<li>
								<span className="font-semibold">이름</span>: 이준희
							</li>
							<li>
								<span className="font-semibold">이메일</span>:{' '}
								<a
									href="mailto:joonheealert@gmail.com"
									className="text-blue-600 hover:text-blue-800 underline"
								>
									joonheealert@gmail.com
								</a>
							</li>
							<li>
								<span className="font-semibold">응답 가능 시간</span>: 평일 10:00
								~ 17:00
							</li>
						</ul>
					</div>
					<p className="text-slate-600 text-sm italic">
						※ 법인이 아닌 프로젝트 팀 특성상 전담 보호책임자는 별도 지정하지 않으며,
						운영진이 공동 대응합니다.
					</p>
				</section>

				<Divider className="bg-slate-200 my-6" />

				{/* 9. 개인정보의 안전성 확보 조치 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						9. 개인정보의 안전성 확보 조치
					</h2>
					<ul className="list-disc list-inside text-slate-700 ml-4">
						<li>개인정보 접근권한 최소화 및 암호화</li>
						<li>내부 운영진 대상 정기적 보안 교육</li>
						<li>
							외부 공격에 대비한 방화벽 및 보안 점검 도구 운영
						</li>
					</ul>
				</section>

					<Divider className="bg-slate-200 my-6" />

				{/* 10. 정책 변경 안내 */}
				<section>
					<h2 className="text-xl md:text-2xl font-semibold text-slate-900 mb-4">
						10. 정책 변경 안내
					</h2>
					<p className="text-slate-700">
						본 방침은 서비스 개선이나 관련 법령 변경 시 변경될 수 있으며, 변경
						사항은 사전에 앱 또는 웹사이트를 통해 공지합니다. 중요한 내용의 변경
						시 최소 7일 전부터 공지합니다.
					</p>
				</section>

				<Divider className="bg-slate-200 my-8" />

				{/* 시행일자 */}
				<section className="bg-slate-50 rounded-lg p-4 md:p-6">
					<p className="text-slate-700 font-semibold">
						[시행일자] 본 방침은 2026년 1월 21일부터 시행됩니다.
					</p>
				</section>
			</div>
		</div>
	);
}
