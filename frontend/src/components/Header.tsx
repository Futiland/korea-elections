import Link from 'next/link';
import { useRouter } from 'next/router';
import { Button } from '@/components/ui/button';
import {
	DropdownMenu,
	DropdownMenuTrigger,
	DropdownMenuContent,
	DropdownMenuItem,
} from '@/components/ui/dropdown-menu';
import { Menu } from 'lucide-react';

export default function Header() {
	const router = useRouter();

	return (
		<header className="w-full border-b bg-white dark:bg-gray-950">
			<div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
				{/* 로고 */}
				<Link href="/">
					<span className="text-lg font-semibold">Korea Elections Project</span>
				</Link>

				{/* 네비게이션 */}
				<nav className="hidden md:flex gap-4 text-sm font-medium">
					<Link
						href="/"
						className={`hover:text-primary transition-colors ${
							router.pathname === '/' ? 'text-primary' : ''
						}`}
					>
						Home
					</Link>
					<Link
						href="/about"
						className={`hover:text-primary transition-colors ${
							router.pathname === '/about' ? 'text-primary' : ''
						}`}
					>
						About
					</Link>
					<Link
						href="/contact"
						className={`hover:text-primary transition-colors ${
							router.pathname === '/contact' ? 'text-primary' : ''
						}`}
					>
						Contact
					</Link>
				</nav>

				{/* 우측 */}
				<div className="flex items-center gap-2">
					<Button variant="outline" size="sm">
						로그인
					</Button>

					{/* 모바일 메뉴 */}
					<DropdownMenu>
						<DropdownMenuTrigger asChild>
							<Button variant="ghost" size="icon" className="md:hidden">
								<Menu className="h-5 w-5" />
							</Button>
						</DropdownMenuTrigger>
						<DropdownMenuContent align="end">
							<DropdownMenuItem>
								<Link href="/">Home</Link>
							</DropdownMenuItem>
							<DropdownMenuItem>
								<Link href="/about">About</Link>
							</DropdownMenuItem>
							<DropdownMenuItem>
								<Link href="/contact">Contact</Link>
							</DropdownMenuItem>
						</DropdownMenuContent>
					</DropdownMenu>
				</div>
			</div>
		</header>
	);
}
