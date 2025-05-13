import Link from 'next/link';

export default function Header() {
	return (
		<header className="w-full bg-blue-600 text-white py-4 px-6 shadow">
			<div className="max-w-6xl mx-auto flex justify-between items-center">
				<h1 className="text-xl font-bold">Korea Elections</h1>
				<nav className="space-x-4">
					<Link href="/">
						<span className="hover:underline cursor-pointer">홈</span>
					</Link>
					<Link href="/vote">
						<span className="hover:underline cursor-pointer">투표</span>
					</Link>
					<Link href="/mypage">
						<span className="hover:underline cursor-pointer">마이페이지</span>
					</Link>
				</nav>
			</div>
		</header>
	);
}
