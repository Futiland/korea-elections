import { useRouter } from 'next/router';
import Link from 'next/link';
import clsx from 'clsx';
import { Home, History, User } from 'lucide-react';

const menus = [
	{ href: '/', label: '홈', icon: Home },
	{ href: '/vote', label: '선거결과', icon: History },
	{ href: '/mypage', label: '마이페이지', icon: User },
];

export default function BottomMenuBar() {
	const router = useRouter();

	return (
		<nav className="fixed bottom-0 left-0 right-0 z-50 border-t bg-white dark:bg-gray-950 p-3">
			<div className="flex justify-around items-center h-14 text-sm">
				{menus.map(({ href, label, icon: Icon }) => {
					const isActive = router.pathname === href;
					return (
						<Link
							key={href}
							href={href}
							className={clsx(
								'flex flex-col items-center justify-center gap-0.5 transition-colors',
								isActive ? 'text-blue-800 font-semibold' : 'text-slate-500'
							)}
						>
							<Icon className="w-5 h-5" />
							{label}
						</Link>
					);
				})}
			</div>
		</nav>
	);
}
