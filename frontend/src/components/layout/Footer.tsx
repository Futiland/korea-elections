import Image from 'next/image';
export default function Footer() {
	return (
		<footer className="w-full bg-slate-100 text-slate-400 text-sm px-6 pb-20">
			<div className="max-w-xl mx-auto flex items-center justify-center py-6">
				<div className="relative w-[76px] h-[24px] ">
					<Image
						src="/img/logo-g.svg"
						alt="KEP 로고"
						fill
						className="object-contain"
					/>
				</div>
			</div>
			<div>
				<div className="max-w-xl mx-auto space-y-1 leading-relaxed flex flex-col items-start font-semibold">
					<p>상호명 : 푸티랜드(Futiland)</p>
					<p>대표자 : 이준희</p>
					<p>주소 : 서울특별시 강동구 천중로 264</p>
					<p>사업자등록번호 : 105-60-00842</p>
					<p>이메일 : joonheealert@gmail.com</p>
					<p>전화번호 : 070-8027-8679</p>
				</div>
			</div>
			<div className="py-4 text-center">
				&copy; {new Date().getFullYear()} Korea Elections. All rights reserved
			</div>
		</footer>
	);
}
