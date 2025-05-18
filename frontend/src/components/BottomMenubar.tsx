import { useRouter } from 'next/router';
import Link from 'next/link';
import clsx from 'clsx';
import { Home, Vote, CircleUserRound } from 'lucide-react';

const menus = [
	{ href: '/', label: '홈', icon: Home },
	{ href: '/vote', label: 'vote', icon: Vote },
	{ href: '/mypage', label: 'my', icon: CircleUserRound },
];

export default function BottomMenuBar() {
	const router = useRouter();

	return (
		<nav className="fixed bottom-0 left-0 right-0 z-50 border-t bg-white dark:bg-gray-950 md:hidden">
			<div className="flex justify-around items-center h-14 text-sm">
				{menus.map(({ href, label, icon: Icon }) => {
					const isActive = router.pathname === href;
					return (
						<Link
							key={href}
							href={href}
							className={clsx(
								'flex flex-col items-center justify-center gap-0.5 text-muted-foreground transition-colors',
								isActive && 'text-primary font-semibold'
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
